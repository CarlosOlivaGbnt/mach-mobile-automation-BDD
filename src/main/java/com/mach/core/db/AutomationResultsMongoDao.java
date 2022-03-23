package com.mach.core.db;

import com.mongodb.client.MongoDatabase;

public abstract class AutomationResultsMongoDao {

    protected MongoDatabase database;

    protected AutomationResultsMongoDao(final String databaseName) {
        database = MachAutomationResultsMongoConnection.getInstance().getConnection().getDatabase(databaseName);
    }

}
