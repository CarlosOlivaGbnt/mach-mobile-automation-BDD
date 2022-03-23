package com.mach.core.model.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.mach.core.db.AutomationResultsMongoDao;
import com.mach.core.model.Suite;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Optional;

public class SuiteRepository extends AutomationResultsMongoDao {

    private static SuiteRepository instance;
    private MongoCollection<Document> collection;
    private Gson gson;

    private SuiteRepository() {
        super("automation-results");
        collection = database.getCollection("suites");
        gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, (JsonDeserializer<ObjectId>) (je, type, jdc) -> new ObjectId(je.getAsJsonObject().get("$oid").getAsString())).create();
    }

    public static SuiteRepository getInstance() {
        if (instance == null) {
            instance = new SuiteRepository();
        }
        return instance;
    }

    public Optional<Suite> findByFileNameAndPlatform(String fileName, String platform) {
        Document suiteDoc = collection.find(Filters.and(Filters.eq("fileName", fileName), Filters.eq("platform", platform))).first();
        if (suiteDoc == null) {
            return Optional.empty();
        }
        return Optional.of(gson.fromJson(suiteDoc.toJson(), Suite.class));
    }

    public void save(Suite suite, boolean isNew) {
        Document updateDoc = Document.parse(gson.toJson(suite));
        updateDoc.remove("_id");
        updateDoc.remove("lastScheduled");
        updateDoc.append("lastScheduled", Instant.now());
        if(isNew){
            collection.insertOne(updateDoc);
        } else {
            collection.replaceOne(Filters.and(Filters.eq("fileName", suite.getFileName()), Filters.eq("platform", suite.getPlatform())), updateDoc);
        }

    }
}
