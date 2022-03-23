package com.mach.core.config.amazon;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.devicefarm.AWSDeviceFarm;
import com.amazonaws.services.devicefarm.AWSDeviceFarmClientBuilder;
import com.amazonaws.services.devicefarm.model.ListRunsRequest;
import com.amazonaws.services.devicefarm.model.ListRunsResult;
import com.amazonaws.services.devicefarm.model.Run;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.mach.core.config.CurrentReporter;
import com.mach.core.config.MachExecutionEnvironment;
import com.mach.core.config.MachProfileResolver;
import com.mach.core.config.MachProperties;
import io.qameta.allure.CommandLine;
import io.qameta.allure.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static com.mach.core.config.CommandRunner.executeCommand;

public class ResultReporter {

	public static final String GENERATED_RESULTS_PATH = File.separator + "tmp" + File.separator + "allure-results";
	private static final String GENERATED_REPORT_PATH = File.separator + "tmp" + File.separator + "allure-site";
	private static final Logger LOG = LoggerFactory.getLogger(ResultReporter.class);
	private static final MachExecutionEnvironment machExecutionEnvironment = System.getenv("EXECUTION_ENVIRONMENT") == null ? MachExecutionEnvironment.LOCAL : MachExecutionEnvironment.valueOf(System.getenv("EXECUTION_ENVIRONMENT"));
	private static final String S3_ACCESS_KEY = System.getenv("AWS_ACCESS_KEY_ID");
	private static final String S3_SECRET_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
	private static final String S3_REGION = System.getenv("AWS_DEFAULT_REGION") == null ? "us-west-2" : System.getenv("AWS_DEFAULT_REGION");
	private static final String PROJECT_ARN = System.getenv("DEVICEFARM_PROJECT_ARN") == null ? System.getenv("AWS_DF_AUTO_ARN") : System.getenv("DEVICEFARM_PROJECT_ARN");
	private static final String EXECUTION_NAME = System.getenv("EXECUTION_NAME");

	private String bucket;
	private boolean uploadLocalToS3;
	private boolean recordLocalVideo;
	private String executionUser;
	private String executionDate;
	private static Run deviceFarmRun;

	public ResultReporter(){
		MachProperties properties = MachProperties.getInstance();
		bucket = properties.getString("S3_BUCKET","automation-results.soymach.com");
		uploadLocalToS3 = Boolean.valueOf(properties.getString("appium.uploadLocalToS3","false"));
		recordLocalVideo = Boolean.valueOf(properties.getString("appium.recordLocalVideo","false"));
	}

	private static Run getDeviceFarmRun() {
		if(deviceFarmRun == null) {
			int tries = 0;
			Optional<Run> runOpt;
			String nextToken = null;
			AWSDeviceFarm client = AWSDeviceFarmClientBuilder.standard()
					.withRegion(Regions.fromName(S3_REGION))
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(S3_ACCESS_KEY, S3_SECRET_KEY)))
					.build();

			do {
				ListRunsResult listRunsResult = client.listRuns(new ListRunsRequest().withArn(PROJECT_ARN).withNextToken(nextToken));
				nextToken = listRunsResult.getNextToken();
				runOpt = listRunsResult.getRuns().stream().filter(run -> run.getName().equals(EXECUTION_NAME)).findAny();
				tries++;
			} while (!runOpt.isPresent() && tries < 10);

			if (runOpt.isPresent()) {
				deviceFarmRun = runOpt.get();
			} else {
				LOG.warn("No run ARN found for {} after 10 tries.", EXECUTION_NAME);
			}
		}

		return deviceFarmRun;
	}

	private String getPathInS3() {
		String pathS3 = "skip_uploadLocalToS3";
		if (isAWSRun()) {
			String[] suiteAndTime = EXECUTION_NAME.split("-");
			pathS3 = bucket + "/public/reports/" + MachProfileResolver.getActiveProfile().toLowerCase() + File.separator + CurrentReporter.getFileSuite() + "-" + suiteAndTime[suiteAndTime.length - 1];
		} else if (uploadLocalToS3) {
			pathS3 = bucket + "/public/reports/" + MachProfileResolver.getActiveProfile().toLowerCase() + File.separator + CurrentReporter.getFileSuite() + "-" + getExecutionDate();
		}
		LOG.debug("getPathInS3: {}", pathS3);
		return pathS3;
	}

	public String getS3Link() {
		return "http://" + getPathInS3() + "/index.html";
	}

	public ResultReporter writeToAllureEnvironment(String key, String value) {
		if(key != null && value != null) {
			try (FileWriter fileWriter = new FileWriter(GENERATED_RESULTS_PATH + File.separator + "environment.properties", true)) {
				fileWriter.write(key + "=" + value + "\n");
			} catch (IOException e) {
				LOG.error("Could not write to allure environment.properties", e);
			}
		}
		return this;
	}

	public ResultReporter uploadAllureReport() {
		if(isAWSRun() || uploadLocalToS3){
			executeCommand("mkdir -p " + GENERATED_REPORT_PATH);
			LOG.debug("upload allureCommand result: {}", allureCommand("generate", "--clean", GENERATED_RESULTS_PATH, "-o", GENERATED_REPORT_PATH));
			if(recordLocalVideo){
				String videoFile = CurrentReporter.getVideoFile(isAWSRun(), uploadLocalToS3, EXECUTION_NAME, MachProfileResolver.getActiveProfile(), getExecutionDate());
				executeCommand("cp /tmp/" + videoFile + " " + GENERATED_REPORT_PATH + "/data/attachments/" + videoFile);
			}
			uploadFolderToS3();
		} else {
			LOG.debug("skip uploadAllureReport Local to AWS S3, by properties appium.uploadLocalToS3");
		}
		return this;
	}

	private int allureCommand(String... command) {
		final CommandLine commandLine = new CommandLine((Path) null);
		final ExitCode exitCode = commandLine
				.parse(command)
				.orElseGet(commandLine::run);
		return exitCode.getCode();
	}

	public void uploadFolderToS3(){
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(S3_ACCESS_KEY, S3_SECRET_KEY))).withRegion(Regions.US_WEST_2)
				.build();
		TransferManager xferMgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
		try {
			MultipleFileUpload xfer = xferMgr.uploadDirectory(bucket,
					getPathInS3().replace(bucket+"/",""), new File(GENERATED_REPORT_PATH), true);
			xfer.waitForCompletion();
		} catch ( Exception e) {
			LOG.error(e.getMessage());
		}
		xferMgr.shutdownNow();
	}

	public static String getDeviceFarmLink() {
		if (isAWSRun()) {
			final String runArn = getRunArn();
			if(runArn == null){
				return null;
			}
			String[] fullProjectArn = PROJECT_ARN.split(":");
			String shortProjectArn = fullProjectArn[fullProjectArn.length - 1];
			String[] fullRunArn = runArn.split("/");
			String shortRunArn = fullRunArn[fullRunArn.length - 1];
			return "https://us-west-2.console.aws.amazon.com/devicefarm/home?region=" + S3_REGION + "#/projects/"
					+ shortProjectArn + "/runs/" + shortRunArn;
		}
		return null;
	}

	public static String getRunArn() {
		if (isAWSRun() && getDeviceFarmRun() != null && deviceFarmRun != null) {
			return deviceFarmRun.getArn();
		}
		return null;
	}

	public String getExecutionUser() {
		return executionUser;
	}

	public void setExecutionUser(String executionUser) {
		this.executionUser = executionUser;
	}

	protected static boolean isAWSRun() {
		return MachExecutionEnvironment.AWS.equals(machExecutionEnvironment);
	}

	public String getAwsS3VideoURL() {
		String urlVideo = "skip_uploadLocalVideoToS3";
		String videoFile = CurrentReporter.getVideoFile(isAWSRun(),uploadLocalToS3, EXECUTION_NAME, MachProfileResolver.getActiveProfile(),getExecutionDate());

		if (isAWSRun() || uploadLocalToS3) {
			urlVideo = "http://" + getPathInS3() + "/data/attachments/" + videoFile;
		}
		LOG.debug("getAwsS3VideoURL: {}", urlVideo);
		return urlVideo;
	}

	public String getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(String executionDate) {
		this.executionDate = executionDate;
	}
}
