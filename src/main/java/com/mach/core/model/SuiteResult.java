package com.mach.core.model;

import com.google.gson.Gson;
import com.mach.core.config.CurrentReporter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.testng.ISuiteResult;
import org.testng.ITestContext;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Saves data into data base Automation Results
 */
@Document(collection = "suiteResult")
public class SuiteResult {

    @Id
    private String id;

    private String platform;
    private String suiteName;
    private String suiteFileName;
    private String environment;
    private String executionDate;
    private String awsLink;
    private String allureLink;
    private String runArn;
    private double minutesMetered;
    private double minutesTotal;
    private long passedResults;
    private long failedResults;
    private long skippedResults;
    private long failedConfigs;
    private String deviceBrand;
    private String deviceModel;
    private String devicePlatformVersion;
    private String appVersion;
    private String executionUser;
    private long secondsElapsed;
    private Instant updatedAt;
    private String automationBranch;
    private String appcenterBranch;
    private String pr;

    private SuiteResult() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setDeviceModel(String device) {
        this.deviceModel = device;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(String executionDate) {
        this.executionDate = executionDate;
    }

    public String getAwsLink() {
        return awsLink;
    }

    public void setAwsLink(String awsLink) {
        this.awsLink = awsLink;
    }

    public String getAllureLink() {
        return allureLink;
    }

    public void setAllureLink(String allureLink) {
        this.allureLink = allureLink;
    }

    public long getPassedResults() {
        return passedResults;
    }

    public void setPassedResults(long passedResults) {
        this.passedResults = passedResults;
    }

    public long getFailedResults() {
        return failedResults;
    }

    public void setFailedResults(long failedResults) {
        this.failedResults = failedResults;
    }

    public long getFailedConfigs() {
        return failedConfigs;
    }

    public void setFailedConfigs(long failedConfigs) {
        this.failedConfigs = failedConfigs;
    }

    public long getSkippedResults() {
        return skippedResults;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setSkippedResults(long skippedResults) {
        this.skippedResults = skippedResults;
    }

    public String getSuiteFileName() {
        return suiteFileName;
    }

    public void setSuiteFileName(String suiteFileName) {
        this.suiteFileName = suiteFileName;
    }

    public void setDeviceBrand(String brand) {
        this.deviceBrand = brand;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public String getDevicePlatformVersion() {
        return devicePlatformVersion;
    }

    public void setDevicePlatformVersion(String devicePlatformVersion) {
        this.devicePlatformVersion = devicePlatformVersion;
    }

    public void setAppVersion(String machVersion) {
        this.appVersion = machVersion;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setSecondsElapsed(long secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
    }

    public long getSecondsElapsed() {
        return secondsElapsed;
    }
    
    @Override
    public String toString() {
    	return new Gson().toJson(this);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    public double getMinutesMetered() {
        return minutesMetered;
    }

    public void setMinutesMetered(double minutesMetered) {
        this.minutesMetered = minutesMetered;
    }

    public double getMinutesTotal() {
        return minutesTotal;
    }

    public void setMinutesTotal(double minutesTotal) {
        this.minutesTotal = minutesTotal;
    }

    public String getRunArn() {
        return runArn;
    }

    public void setRunArn(String runArn) {
        this.runArn = runArn;
    }

    public String getAutomationBranch() {
        return automationBranch;
    }

    public void setAutomationBranch(String automationBranch) {
        this.automationBranch = automationBranch;
    }

    public String getAppcenterBranch() {
        return appcenterBranch;
    }

    public void setAppcenterBranch(String appcenterBranch) {
        this.appcenterBranch = appcenterBranch;
    }

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public static class SuiteResultBuilder {
        private long failedCount;
        private long failedConfigsCount;
        private long skippedCount;
        private long passedCount;
        private long secondsElapsed;
        private String suiteName;
        private String filename;

        public SuiteResultBuilder() {
        	failedConfigsCount = 0;
            failedCount = 0;
            skippedCount = 0;
            passedCount = 0;
            secondsElapsed = 0;
        }

        public SuiteResultBuilder ofContext(ITestContext context) {
            String tmpSuiteName = context == null ? CurrentReporter.getFileSuite() : context.getSuite().getName();
            final String originalSuiteFilename = context == null ? tmpSuiteName : context.getSuite().getXmlSuite().getFileName();
            String tmpFilename = originalSuiteFilename.substring(originalSuiteFilename.lastIndexOf(File.separator) + 1).replace(".xml", "");
            this.suiteName = "Device Farm container suite".equals(tmpSuiteName) ? System.getenv("SUITE_NAME") : tmpSuiteName;
            this.filename = "testng".equals(tmpFilename) ? System.getenv("SUITE_NAME") : tmpFilename;

            if(context != null){
                context.getSuite().getResults().forEach((key, value) -> this.passedCount += value.getTestContext().getPassedTests().size());
                context.getSuite().getResults().forEach((key, value) -> this.failedCount += value.getTestContext().getFailedTests().size());
                context.getSuite().getResults().forEach((key, value) -> this.failedConfigsCount += value.getTestContext().getFailedConfigurations().size());
                context.getSuite().getResults().forEach((key, value) -> this.skippedCount += value.getTestContext().getSkippedTests().size());
                context.getSuite().getResults().forEach((key, value) -> this.secondsElapsed += getTestElapsedTimeInMilis(value));
                context.getSuite().getResults().values().stream().map(this::getTestElapsedTimeInMilis).reduce(Long::sum).ifPresent(s -> this.secondsElapsed = convertMiliToSeconds(s));
            }
             return this;
        }

        public SuiteResult build() {
            final SuiteResult suiteResult = new SuiteResult();
            suiteResult.setSkippedResults(this.skippedCount);
            suiteResult.setFailedResults(this.failedCount);
            suiteResult.setFailedConfigs(this.failedConfigsCount);
            suiteResult.setPassedResults(this.passedCount);
            suiteResult.setSecondsElapsed(this.secondsElapsed);
            suiteResult.setSuiteName(this.suiteName);
            suiteResult.setSuiteFileName(this.filename);
            return suiteResult;
        }

        private long getTestElapsedTimeInMilis(ISuiteResult result) {
            return Math.abs(result.getTestContext().getEndDate().getTime() - result.getTestContext().getStartDate().getTime());
        }

        private long convertMiliToSeconds(long mili) {
            return TimeUnit.SECONDS.convert(mili, TimeUnit.MILLISECONDS);
        }
    }

}
