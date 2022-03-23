package com.mach.core.api;

import com.mach.core.config.CommandRunner;
import com.mach.core.config.MachExecutionStatus;
import com.mach.core.config.MachProfileResolver;
import com.mach.core.model.SuiteResult;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.Optional;

public class SlackAPI {

    public static final String SLACK_SOS_FILE = System.getenv("SLACK_SOS_FILE");
    private static final String EXECUTION_USER = System.getenv("EXECUTION_USER") == null ? System.getenv("USER") : System.getenv("EXECUTION_USER");
    private static final String SLACK_URL = System.getenv("SLACK_URL");
    private static final String APP_CENTER_BRANCH = System.getenv("APPCENTER_BRANCH") == null ? "appcenter_no_branch" : System.getenv("APPCENTER_BRANCH");

    private SlackAPI(){}

    public static void notifyOnSlack(SuiteResult suiteResult, @Optional("no_link") String urlLink) {

        final String messageTemplate = "" + "{" + "\"username\": \"%s\","
                + "\"icon_url\": \"http://www.iconeasy.com/icon/png/Movie%%20%%26%%20TV/Adium%%20Eve%%20Wall-E/Awake.png\","
                + "\"attachments\": [{" + "\"fallback\": \":%s: %s.\","
                + "\"pretext\": \":unnamed:\\t%s\\n:%s:\\t*%s*\\n:unnamed:\\t<%s|Ver reporte>\","
                + "\"color\": \"%s\","
                + "\"fields\": [" + "{\"value\":\"*Passed:* %d\"}," + "{\"value\":\"*Failed:* %d\"},"
                + "{\"value\":\"*Skipped:* %d\"}," + "{\"value\":\"*Total:* %d\"}" + "]" + "}]" + "}";

        final String color = MachExecutionStatus.fromSuiteResult(suiteResult).getColor();

        final String message = String.format(messageTemplate, EXECUTION_USER, MachProfileResolver.getActiveProfile().toLowerCase(), suiteResult.getSuiteFileName(), APP_CENTER_BRANCH, MachProfileResolver.getActiveProfile().toLowerCase(), suiteResult.getSuiteFileName(), urlLink, color,
                suiteResult.getPassedResults(), suiteResult.getFailedResults(), suiteResult.getSkippedResults(), suiteResult.getPassedResults() + suiteResult.getFailedResults() + suiteResult.getSkippedResults());

        // envia el mensaje via Slack
        RestAssured.given().contentType(ContentType.JSON).body(message).post(SLACK_URL);
        // elimina el mensaje SOS de Slack
        CommandRunner.executeCommand("rm -f " + SLACK_SOS_FILE);
    }

}
