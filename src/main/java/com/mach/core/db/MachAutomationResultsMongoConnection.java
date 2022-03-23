package com.mach.core.db;

import com.mach.core.exception.MachException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachAutomationResultsMongoConnection {

	private static final Logger LOG = LoggerFactory.getLogger(MachAutomationResultsMongoConnection.class);
	private static MachAutomationResultsMongoConnection instance = null;
	private MongoClient mongoClient = null;
	private String user = System.getenv("MONGODB_USER");
	private String pass = System.getenv("MONGODB_PASS");

	private MachAutomationResultsMongoConnection() {
		if (user == null || pass == null) {
			throw new MachException("MONGODB_USER or MONGODB_PASS is not set");
		}

		ConnectionString connString = new ConnectionString("mongodb://" + user + ":" + pass
				+ "@automationresultsmetrics-shard-00-00-oiwtk.mongodb.net:27017,automationresultsmetrics-shard-00-01-oiwtk.mongodb.net:27017,automationresultsmetrics-shard-00-02-oiwtk.mongodb.net:27017/?replicaSet=AutomationResultsMetrics-shard-0&authSource=admin&tls=true");
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString)
				.retryWrites(true).build();
		mongoClient = MongoClients.create(settings);
	}

	public MongoClient getConnection() {
		return mongoClient;
	}

	public static synchronized MachAutomationResultsMongoConnection getInstance() {
		if (instance == null) {
			try {
				instance = new MachAutomationResultsMongoConnection();
			} catch (Exception e) {
				LOG.error("Error at MachAutomationResultsMongoConnection, e: ", e);
				System.exit(1);
			}
		}
		return instance;
	}

}
