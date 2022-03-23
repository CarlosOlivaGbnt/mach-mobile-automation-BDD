package com.mach.core.db;

import com.mach.core.config.MachProperties;
import com.mach.core.exception.MachException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachAutomationMongoConnection {

	private static final Logger LOG = LoggerFactory.getLogger(MachAutomationMongoConnection.class);
	private static MachAutomationMongoConnection instance = null;
	private MongoClient mongoClient = null;
	private String user;
	private String pass;

	private MachAutomationMongoConnection() {
		MachProperties properties = MachProperties.getInstance();
		user = properties.getString("MONGODB_USER");
		pass = properties.getString("MONGODB_PASS");
		if (user == null || pass == null) {
			throw new MachException("MONGODB_USER or MONGODB_PASS is not set");
		}
		ConnectionString connString = new ConnectionString("mongodb://" + user + ":" + pass
				+ "@automation-shard-00-00-oiwtk.mongodb.net:27017,automation-shard-00-01-oiwtk.mongodb.net:27017,automation-shard-00-02-oiwtk.mongodb.net:27017/?replicaSet=Automation-shard-0&authSource=admin&ssl=true");
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString)
				.retryWrites(true).build();
		mongoClient = MongoClients.create(settings);
	}

	public MongoClient getConnection() {
		return mongoClient;
	}

	public static synchronized MachAutomationMongoConnection getInstance() {
		if (instance == null) {
			try {
				instance = new MachAutomationMongoConnection();
			} catch (Exception e) {
				LOG.error("Error at MachAutomationMongoConnection, e: ", e);
				System.exit(1);
			}
		}
		return instance;
	}

}
