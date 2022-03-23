package com.mach.core.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.mach.core.model.repository")
public class MongoBDConfig extends AbstractMongoClientConfiguration {

    private final String MONGODB_URL = "mongodb+srv://%s:%s@automationresultsmetrics-oiwtk.mongodb.net/test?retryWrites=true&w=majority";

    @Value("${MONGODB_USER}")
    private String MONGODB_USER;

    @Value("${MONGODB_PASS}")
    private String MONGODB_PASS;

    protected String getDatabaseName() {
        return "automation-results";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(String.format(MONGODB_URL, MONGODB_USER, MONGODB_PASS));
    }
}
