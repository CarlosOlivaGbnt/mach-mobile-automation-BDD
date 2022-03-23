package com.mach.core.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mach.core.model.SuiteResult;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubReporter {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubReporter.class);
    private static final String GITHUB_CONTENT_TYPE = "application/vnd.github.v3+json";

    private GitHubReporter() {
        // Implicit constructor to hide default public one
    }

    public static boolean updatePRComment(SuiteResult suiteResult) {
        final String githubUser = "sismo-bot";
        final String githubToken = System.getenv("GITHUB_TOKEN_FOR_CHECKS");
        final String prUrl = suiteResult.getPr();

        if (prUrl == null || prUrl.isEmpty() || "none".equals(prUrl) || null == githubToken) {
            LOG.debug("no prUrl or github token provided.");
            return false;
        }

        String prEndpoint = prUrl.replace("https://github.com/", "https://api.github.com/repos/")
                .replace("/pull/", "/issues/");

        Response issue = RestAssured.given().auth().preemptive().basic(githubUser, githubToken)
                .and().accept(GITHUB_CONTENT_TYPE)
                .get(prEndpoint);

        String commentsBody = RestAssured.given().auth().preemptive().basic(githubUser, githubToken)
                .and().accept(GITHUB_CONTENT_TYPE)
                .get(issue.body().jsonPath().getString("comments_url"))
                .body().asString();
        JsonArray comments = JsonParser.parseString(commentsBody).getAsJsonArray();

        String method = "POST";
        String message = "## Status de pruebas automatizadas\n| Suite | Status |\n|:-:|:-:|";
        String url = issue.body().jsonPath().getString("comments_url");
        for (int i = 0; i < comments.size(); i++) {
            JsonObject comment = comments.get(i).getAsJsonObject();
            if (githubUser.equals(comment.getAsJsonObject("user").get("login").getAsString())) {
                message = comment.get("body").getAsString();
                url = comment.get("url").getAsString();
                method = "PATCH";
            }
        }

        String suiteName = suiteResult.getSuiteName();
        String newLine = null;
        if (suiteResult.getSecondsElapsed() == 0) {
            newLine = String.format("| %s | %s |", suiteName, MachExecutionStatus.RUNNING.getMessage());
        } else {
            MachExecutionStatus newStatus = MachExecutionStatus.fromSuiteResult(suiteResult);
            newLine = String.format("| [%s](%s) | %s |", suiteName, suiteResult.getAllureLink(), newStatus.getMessage());
        }
        if (message.contains(suiteName)) {
            String oldLineRegex = "\\|.*" + suiteName.replace("[", "\\[") + "[^\\n]*";
            message = message.replaceAll(oldLineRegex, newLine);
        } else {
            message = message + "\n" + newLine;
        }
        JsonObject body = new JsonObject();
        body.addProperty("body", message);

        int statusCode = RestAssured.given().auth().preemptive().basic(githubUser, githubToken)
                .and().accept(GITHUB_CONTENT_TYPE)
                .and().contentType(ContentType.JSON)
                .and().body(body.toString())
                .request(method, url)
                .statusCode();

        LOG.info("updatePRComment: status code {} after sending message", statusCode);
        return statusCode == 200 || statusCode == 201;
    }

}
