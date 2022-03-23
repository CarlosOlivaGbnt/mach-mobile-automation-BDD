package com.mach.core.db;

import com.mongodb.client.MongoDatabase;

public abstract class AutomationMongoDao {

    protected MongoDatabase database;

    protected AutomationMongoDao(final String databaseName) {
        database = MachAutomationMongoConnection.getInstance().getConnection().getDatabase(databaseName);
    }

}
