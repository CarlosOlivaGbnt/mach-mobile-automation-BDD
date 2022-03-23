package com.mach.core.config;

import org.testng.Reporter;

public class CurrentReporter {

    private CurrentReporter() {
    }

    public static String getFileSuite(){
        String suiteFileName = Reporter.getCurrentTestResult().getTestContext().getSuite().getXmlSuite().getFileName();
        return suiteFileName.substring(suiteFileName.lastIndexOf('/') + 1).replace(".xml", "");
    }

    public static String getVideoFile(boolean isAWSRun, boolean uploadLocalToS3, String awsExecutionName, String platform, String executionDate){
        String videoFile = "no_local_video.mp4";

        if (isAWSRun) {
            videoFile = awsExecutionName.replace("/","_") + ".mp4";
        } else if (uploadLocalToS3) {
            videoFile = platform.toLowerCase() + "_" + getFileSuite() + "-" + executionDate + ".mp4";
        }
        return videoFile;
    }
}
