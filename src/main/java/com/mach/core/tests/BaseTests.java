package com.mach.core.tests;

import com.google.common.io.Resources;
import com.mach.core.api.SlackAPI;
import com.mach.core.config.BaseConfig;
import com.mach.core.config.CommandRunner;
import com.mach.core.config.CurrentReporter;
import com.mach.core.config.GitHubReporter;
import com.mach.core.config.MachExecutionEnvironment;
import com.mach.core.config.MachExecutionStatus;
import com.mach.core.config.MachProfileResolver;
import com.mach.core.config.MachProperties;
import com.mach.core.config.MongoBDConfig;
import com.mach.core.config.amazon.ResultReporter;
import com.mach.core.config.driver.AppiumDriverFactory;
import com.mach.core.db.UsersMongoRepository;
import com.mach.core.model.DeviceLocal;
import com.mach.core.model.Suite;
import com.mach.core.model.SuiteResult;
import com.mach.core.model.User;
import com.mach.core.model.repository.DeviceLocalMongoRepository;
import com.mach.core.model.repository.SuiteRepository;
import com.mach.core.model.repository.SuiteResultRepository;
import com.mach.core.util.ConstantsTimeOut;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mach.core.config.CommandRunner.executeCommand;
import static com.mach.core.util.ConstantsTimeOut.FAST_CHECK;
import static com.mach.core.util.ConstantsTimeOut.TIMEOUT_CHECK;
import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBe;


@ContextConfiguration(classes = {BaseConfig.class, MongoBDConfig.class}, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles(resolver = MachProfileResolver.class)
@PropertySource("classpath:appium.properties")
@Listeners(value = { ConditionalSkipTest.class, MachTestNGListener.class })
public class BaseTests extends AbstractTestNGSpringContextTests implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTests.class);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private MachExecutionEnvironment executionEnvironment;
    private MachProperties properties;
    private String executionUser;
    private String automationBranch;
    private boolean recordLocalVideo;
    private boolean uploadLocalToS3;
    private boolean playFinishSoundSuite;
    private String appcenterBranch;
    private String prUrl;
    private ResultReporter resultReporter;
    private static String appVersion;
    private String platform;
    private String textDate;

    @Autowired private SuiteResultRepository suiteResultRepository;
    @Autowired private DeviceLocalMongoRepository deviceLocalMongoRepository;

    @Override
    public void afterPropertiesSet() {
        final String FALSO = "false";
        properties = MachProperties.getInstance();
        resultReporter = new ResultReporter();

        executionEnvironment = MachExecutionEnvironment.valueOf(properties.getString("EXECUTION_ENVIRONMENT", "LOCAL").toUpperCase());
        executionUser = properties.getString("EXECUTION_USER",System.getenv("USER"));
        automationBranch = properties.getString("AUTOMATION_BRANCH","automation_no_branch");
        recordLocalVideo = Boolean.parseBoolean(properties.getString("appium.recordLocalVideo",FALSO));
        uploadLocalToS3 = Boolean.parseBoolean(properties.getString("appium.uploadLocalToS3",FALSO));
        playFinishSoundSuite = Boolean.parseBoolean(properties.getString("appium.playFinishSoundSuite",FALSO));
        appcenterBranch = properties.getString("APPCENTER_BRANCH","appcenter_no_branch");
        prUrl = properties.getString("PULL_REQUEST");
        platform = MachProfileResolver.getActiveProfile();
    }

    protected AppiumDriver<MobileElement> getDriver() {
        return AppiumDriverFactory.getInstance().getDriver();
    }

    @BeforeSuite
    public void cleanPreviousSuiteResults(ITestContext context) {
        context.getSuite().getResults().clear();
        executeCommand("rm -rf " + ResultReporter.GENERATED_RESULTS_PATH);
    }

    @BeforeSuite
    public void setRunningOnPR(ITestContext context) {
        String pullRequestrUrl = System.getenv("PULL_REQUEST");
        if (pullRequestrUrl != null && !pullRequestrUrl.isEmpty() && !"none".equals(pullRequestrUrl)) {
            SuiteResult suiteResult = new SuiteResult.SuiteResultBuilder().ofContext(context).build();
            suiteResult.setPr(pullRequestrUrl);
            GitHubReporter.updatePRComment(suiteResult);
        }
    }

    @BeforeClass
    public void shouldStartLocalVideoRecording() {
        if (recordLocalVideo && MachExecutionEnvironment.LOCAL.equals(executionEnvironment) && getDriver() != null) {
            startVideo();
        }
    }

    @BeforeClass
    public String getAppVersion() {
        if (appVersion != null || getDriver() == null || CurrentReporter.getFileSuite().contains("n-simulator")) {
            LOG.info("skip get APP version: {}", appVersion);
            return "no_version";
        }
        By elementBy;

        if (MobilePlatform.ANDROID.equals(platform)) {
            elementBy = By.id("txtVersion");
        } else {
            elementBy = MobileBy.iOSNsPredicateString("value CONTAINS 'V.'");
        }
        List<WebElement> elementList;
        try {
            elementList = new WebDriverWait(getDriver(), ConstantsTimeOut.TIMEOUT_DYNAMIC_ELEMENTS).ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class).ignoring(TimeoutException.class).until(numberOfElementsToBe(elementBy, 1));

            if (elementList != null && !elementList.isEmpty()) {
                appVersion = elementList.get(0).getText();
            } else {
                appVersion = "N/A";
            }

        } catch (WebDriverException e) {
            LOG.error("Couldn't find app version", e);
        }
        LOG.info("APP version {}", appVersion);

        return appVersion;
    }

    @AfterMethod(alwaysRun = true)
    protected void evidenceAfterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.SUCCESS) {
            screenshot("Test Passed");
        } else if (result.getStatus() == ITestResult.FAILURE){
            screenshot("Test Failed");
            attachScreenSource(getScreenSource());
        }
    }

    protected byte[] screenshot(String value) {
    	byte[] fileContent = null;

        value = value + "_" + LocalDateTime.now().format(dateFormatter);

        if(getDriver() == null || (MachExecutionEnvironment.LOCAL.equals(executionEnvironment) && !properties.getBoolean("appium.enabled.screenshot"))){
            LOG.error("Can not take a screenshot, getAppiumDriver() is null or screenshot is disabled");
            return new byte[0];
        }
    	try {
    	    fileContent = getDriver().getScreenshotAs(OutputType.BYTES);
            ByteArrayInputStream bis = new ByteArrayInputStream(fileContent);
            Allure.addAttachment(value,bis);
    	    LOG.info("screenshot ok file: {}", value);
    	} catch (WebDriverException e) {
    		LOG.error("Can not take a screenshot. e: {0}", e);
		}
        return fileContent;
    }

    private String attachScreenSource(String screenSource) {
        LOG.info("Screen source code (XML): {}", screenSource);
        Allure.addAttachment("attachScreenSource", screenSource);
        return screenSource;
    }

    @Step("Wait for {0} seconds")
    protected void waitOn(int seconds) {
        try {
            new WebDriverWait(getDriver(), seconds)
                    .withTimeout(Duration.ofSeconds(seconds))
                    .pollingEvery(Duration.ofSeconds(FAST_CHECK))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nonExistingElement")));
        } catch (TimeoutException e) {
            LOG.debug("Finished waiting {} seconds.", seconds);
        }
    }

    @Step("Put app in background for {0} seconds")
    protected void runInBackground(int seconds) {
        try {
            getDriver().closeApp();
            waitOn(seconds);
            getDriver().launchApp();
        } catch (WebDriverException e){
            LOG.warn("runInBackground fail, e:", e);
        }
    }

    @AfterSuite(alwaysRun = true)
    protected void saveSuiteResults(ITestContext context) {

        resultReporter = resultReporter == null ? new ResultReporter(): resultReporter;
        playFinishSound();

        SuiteResult suiteResult = new SuiteResult.SuiteResultBuilder().ofContext(context).build();
        appendDeviceInfo(suiteResult);
        suiteResult.setEnvironment(executionEnvironment.name());
        suiteResult.setPlatform(platform);
        suiteResult.setExecutionUser(executionUser);
        suiteResult.setAutomationBranch("automation_no_branch".equals(automationBranch) ? CommandRunner.executeCommand("git branch --show-current").get(0) : automationBranch);
        suiteResult.setAppcenterBranch(appcenterBranch);
        suiteResult.setExecutionDate(textDate);
        resultReporter.setExecutionDate(textDate);
        suiteResult.setAppVersion(appVersion);
        suiteResult.setAwsLink(ResultReporter.getDeviceFarmLink());
        suiteResult.setAllureLink(resultReporter.getS3Link());
        suiteResult.setUpdatedAt();
        suiteResult.setRunArn(ResultReporter.getRunArn());
        resultReporter.setExecutionUser(executionUser);
        resultReporter.writeToAllureEnvironment("AWS-DF-Link", suiteResult.getAwsLink());

        if(MachExecutionEnvironment.AWS.equals(executionEnvironment) || recordLocalVideo && uploadLocalToS3){
            resultReporter.writeToAllureEnvironment("Video", resultReporter.getAwsS3VideoURL());
        }
        resultReporter.writeToAllureEnvironment("App", appVersion);
        resultReporter.writeToAllureEnvironment("Platform", suiteResult.getPlatform());
        resultReporter.writeToAllureEnvironment("OS", suiteResult.getDevicePlatformVersion());
        resultReporter.writeToAllureEnvironment("Device", suiteResult.getDeviceModel());
        resultReporter.writeToAllureEnvironment("Requester", resultReporter.getExecutionUser());
        resultReporter.writeToAllureEnvironment("App-branch", suiteResult.getAppcenterBranch());
        resultReporter.writeToAllureEnvironment("Automation-branch", suiteResult.getAutomationBranch());

        LOG.info("PR associated: {}", prUrl);
        if (prUrl != null && !"none".equals(prUrl)) {
            suiteResult.setPr(prUrl);
            GitHubReporter.updatePRComment(suiteResult);
        }

        resultReporter.uploadAllureReport();

        if (MachExecutionEnvironment.AWS.name().equals(suiteResult.getEnvironment())) {
            SlackAPI.notifyOnSlack(suiteResult,resultReporter.getS3Link());
        }

        suiteResult = suiteResultRepository.save(suiteResult);
        Optional<Suite> existSuite = SuiteRepository.getInstance().findByFileNameAndPlatform(suiteResult.getSuiteFileName(), suiteResult.getPlatform().toLowerCase());
        Suite suite = new Suite.SuiteBuilder().ofContext(context).build();
        boolean addNewSuite = false;

        if (existSuite.isPresent()){
            suite = existSuite.get();
        } else {
            addNewSuite = true;
        }
        suite.setLastResult(suiteResult);

        LOG.trace("suite Status: {} fromSuiteResult: {}", suite.getStatus(), MachExecutionStatus.fromSuiteResult(suiteResult));

        if (MachExecutionStatus.PASSED.equals(MachExecutionStatus.fromSuiteResult(suiteResult))) {
        	suite.setLastPassedResult(suiteResult);
        	if ("Works w/maint".equals(suite.getStatus())) {
        	    suite.setStatus("Works");
            } else if (!"Works".equals(suite.getStatus())) {
        	    suite.setStatus("Works w/maint");
            }
            if (MachExecutionEnvironment.AWS.name().equals(suiteResult.getEnvironment())) {
                suite.setResult("Passed");
            } else {
                suite.setResult("Passed local");
            }
        }
        SuiteRepository.getInstance().save(suite, addNewSuite);

        LOG.info("Saved suite result to S3 and DB (suiteResult) : {}", suiteResult);
        LOG.info("Saved suite to S3 and DB (suites) : {}", suite);

        quitDriver();
    }

    public void quitDriver() {
        if (getDriver() != null && MachExecutionEnvironment.LOCAL.equals(executionEnvironment)) {
            SessionId sessionId = getDriver().getSessionId();
            getDriver().quit();
            LOG.info("Session {} was killed", sessionId);
        }
    }

    private void appendDeviceInfo(SuiteResult suiteResult) {
        if (MachExecutionEnvironment.AWS.name().equals(suiteResult.getEnvironment())) {
            suiteResult.setDevicePlatformVersion(properties.getString("DEVICEFARM_DEVICE_OS_VERSION"));
            suiteResult.setDeviceModel(properties.getString("DEVICEFARM_DEVICE_NAME"));

        } else if (MobilePlatform.ANDROID.equals(platform) && getDriver() != null) {
            Map<String, Object> deviceProperties = null;
            try {
                deviceProperties = getDriver().getSessionDetails();
            } catch (WebDriverException e){
                LOG.error("appendDeviceInfo: fail at append Device Info");
                return;
            }
            if (deviceProperties.containsKey("deviceModel")) {
                suiteResult.setDeviceModel(deviceProperties.get("deviceModel").toString());
            }
            if (deviceProperties.containsKey("deviceManufacturer")) {
                suiteResult.setDeviceBrand(deviceProperties.get("deviceManufacturer").toString());
            }
            if (deviceProperties.containsKey("platformVersion")) {
                suiteResult.setDevicePlatformVersion(deviceProperties.get("platformVersion").toString());
            }
        } else {
            suiteResult.setDeviceModel(CommandRunner.executeCommand("ideviceinfo -k ProductType").get(0));
            suiteResult.setDevicePlatformVersion(CommandRunner.executeCommand("ideviceinfo -k ProductVersion").get(0));
        }
    }

    protected void updateUser(User user) {
        if (user.getId() != null && !user.getId().isEmpty()) {
            UsersMongoRepository.getInstance().save(user);
        }
    }

    private void startVideo() {
        try{
            if(MobilePlatform.ANDROID.equals(platform) && executeCommand("adb shell screenrecord").contains("Must specify output file (see --help).")){
                stopVideo(CurrentReporter.getFileSuite());
                ((CanRecordScreen) getDriver()).startRecordingScreen(
                        new AndroidStartScreenRecordingOptions()
                                .enableForcedRestart()
                                .withTimeLimit(Duration.ofMinutes(30)));
            }

            if(!MobilePlatform.ANDROID.equals(platform)){
                stopVideo(CurrentReporter.getFileSuite());
                ((CanRecordScreen) getDriver()).startRecordingScreen(
                        new IOSStartScreenRecordingOptions().withVideoType("mpeg4")
                                .withVideoQuality(IOSStartScreenRecordingOptions.VideoQuality.LOW)
                                .enableForcedRestart()
                                .withTimeLimit(Duration.ofMinutes(30)));
            }
        } catch (WebDriverException e){
            LOG.error("Couldn't Create video file.", e);
        }

    }

     private void stopVideo(String filename) {
        if (!recordLocalVideo || !MachExecutionEnvironment.LOCAL.equals(executionEnvironment) || getDriver() == null) {
            return;
        }

        byte[] data = null;

        try {
            String base64String = ((CanRecordScreen) getDriver()).stopRecordingScreen();
            data = Base64.decodeBase64(base64String);
        } catch (WebDriverException e){
            LOG.error("ERROR at stopRecordingScreen, e:", e);
        }

        if (data == null || data.length < 1){
            return;
        }

        String destinationPath = "/tmp/" + CurrentReporter.getVideoFile(MachExecutionEnvironment.AWS.equals(executionEnvironment),uploadLocalToS3,filename,platform,textDate);
        Path path = Paths.get(destinationPath);

        try {
            Files.write(path, data);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            Allure.addAttachment("video_" + LocalDateTime.now().format(dateFormatter) + ".mp4",bis);
            LOG.info("Local Video in: {}.", destinationPath);
        } catch (IOException e) {
            LOG.error("Couldn't Create video file.", e);
        } catch (Exception e) {
            LOG.error("Couldn't Attach video file.", e);
        }

    }

    @AfterClass(alwaysRun = true)
    protected void appendFilesToAllure(ITestContext context) {
        textDate = LocalDateTime.now().format(dateFormatter);
        LOG.info("begin appendFilesToAllure, date: {}", textDate);
        attachTestLogToAllureReport("/tmp/suite_report.log");
        stopVideo(CurrentReporter.getFileSuite());
    }

    private void attachTestLogToAllureReport(String pathFileLog) {
        try {
            String texts = FileUtils.readFileToString(new File(pathFileLog), StandardCharsets.UTF_8);
            LOG.info("attach ok LOG, length: {}", texts.length());
            Allure.addAttachment("log_file",texts);
        } catch (IOException e) {
            LOG.error("Couldn't Attach suite_report.log", e);
        }
    }

    protected String getAppVersionString() {
        return appVersion;
    }

    protected String getScreenSource() {
        String screenSource = "no_source";
        if (getDriver() == null) {
            LOG.error("NOT Screen source code. Exception: getAppiumDriver() = null");
            return screenSource;
        }
        try {
            screenSource = getDriver().getPageSource();
        } catch (WebDriverException e) {
            LOG.error("NOT Screen source code. Exception: {}", e.getMessage());
        }
        return screenSource;
    }

    private String getLocalDevice(){
        String udid = String.valueOf(getDriver().getSessionDetails().get("udid"));
        if(udid.equals("null")){
            return String.valueOf(getDriver().getSessionDetails().get("deviceName"));
        }
        return udid;
    }

    protected String getDeviceIdMACHFromLocal(){
        String localDeviceUdid = getLocalDevice();
        try {
            DeviceLocal deviceLocal = deviceLocalMongoRepository.findFirstByDeviceUdidLocal(localDeviceUdid);
            return deviceLocal.getDeviceIdMACH();
        } catch (NullPointerException e) {
            LOG.warn("Your deviceUdidLocal:'{}' was not found on Collection 'device' on DB 'automation-results'", localDeviceUdid);
        }
        return "";
    }

    protected boolean isLocalRun() {
        return MachExecutionEnvironment.LOCAL.equals(executionEnvironment);
    }

    private void playFinishSound() {

        if(isLocalRun() && playFinishSoundSuite){
            File audioFile = new File(Resources.getResource("finish_suite_sound.wav").getFile());
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
                audioLine.open(format);
                audioLine.start();
                byte[] bytesBuffer = new byte[4096];
                int bytesRead = -1;

                while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
                    audioLine.write(bytesBuffer, 0, bytesRead);
                }
                audioLine.drain();
                audioLine.close();
                audioStream.close();
            } catch (UnsupportedAudioFileException e) {
                LOG.warn("The specified audio file is not supported, e:", e);
            } catch (LineUnavailableException e) {
                LOG.warn("Audio line for playing back is unavailable, e:", e);
            } catch (IOException e) {
                LOG.warn("Error playing the audio file e:", e);
            }
        }
    }

    @Step("Reinstall app")
    protected void resetApp() {
        try{

            if(MachExecutionEnvironment.AWS.equals(executionEnvironment) && !MobilePlatform.ANDROID.equals(platform)){
                LOG.debug("re install ipa on aws df ios");
                final String BUNDLE_ID = System.getenv("BUNDLE_ID_IPA_IOS");
                getDriver().terminateApp(BUNDLE_ID);
                getDriver().removeApp(BUNDLE_ID);
                executeCommand("ideviceinstaller -i " + System.getenv("DEVICEFARM_APP_PATH"));
                getDriver().activateApp(BUNDLE_ID);
            } else {
                playFinishSound();
                LOG.debug("reset app");
                getDriver().resetApp();
            }
            waitOn(TIMEOUT_CHECK);

        } catch (WebDriverException e){
            LOG.error("ERROR on reset app, e: ",e);
        }
    }
}
