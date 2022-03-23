package com.mach.core.model;

import com.google.gson.Gson;
import com.mach.core.config.CurrentReporter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.testng.ITestContext;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Document(collection = "suites")
public class Suite {

	@Id
	private String id;
	private String status;
	private SuiteResult lastPassedResult;
	private SuiteResult lastResult;
	private List<String> bugs;
	private String description;
	private String comment;
	private String platform;
	private String fileName;
	private String name;
	private Instant lastScheduled;
	private boolean requiresBluetooth;
	private boolean requiresContacts;
	private String result;
	private String assignee;

	private Suite() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SuiteResult getLastPassedResult() {
		return lastPassedResult;
	}

	public void setLastPassedResult(SuiteResult lastPassedResult) {
		this.lastPassedResult = lastPassedResult;
	}

	public SuiteResult getLastResult() {
		return lastResult;
	}

	public void setLastResult(SuiteResult lastResult) {
		this.lastResult = lastResult;
	}

	public List<String> getBugs() {
		return bugs;
	}

	public void setBugs(List<String> bugs) {
		this.bugs = bugs;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public Instant getLastScheduled() {
		return lastScheduled;
	}

	public void setLastScheduled(Instant lastScheduled) {
		this.lastScheduled = lastScheduled;
	}

	public void setLastScheduled() {
		this.lastScheduled = Instant.now();
	}

	public boolean isRequiresBluetooth() {
		return requiresBluetooth;
	}

	public void setRequiresBluetooth(boolean requiresBluetooth) {
		this.requiresBluetooth = requiresBluetooth;
	}

	public boolean isRequiresContacts() {
		return requiresContacts;
	}

	public void setRequiresContacts(boolean requiresContacts) {
		this.requiresContacts = requiresContacts;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public static class SuiteBuilder {

		private String suiteName;
		private String filename;
		private String platform;

		public SuiteBuilder() {
			// Default constructor
		}

		public SuiteBuilder ofContext(ITestContext context) {
			final String tmpSuiteName = context == null ? CurrentReporter.getFileSuite() : context.getSuite().getName();
			final String originalSuiteFilename = context == null ? tmpSuiteName : context.getSuite().getXmlSuite().getFileName();
			String tmpFilename = originalSuiteFilename.substring(originalSuiteFilename.lastIndexOf(File.separator) + 1).replace(".xml", "");
			suiteName = "Device Farm container suite".equals(tmpSuiteName) ? System.getenv("SUITE_NAME") : tmpSuiteName;
			filename = "testng".equals(tmpFilename) ? System.getenv("SUITE_NAME") : tmpFilename;
			platform = originalSuiteFilename.contains("android") ? "android" : "ios";
			return this;
		}

		public Suite build() {
			final Suite suite = new Suite();
			suite.setName(this.suiteName);
			suite.setFileName(this.filename);
			suite.setPlatform(platform);
			return suite;
		}

	}

}
