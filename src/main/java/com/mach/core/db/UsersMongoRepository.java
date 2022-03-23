package com.mach.core.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.mach.core.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class UsersMongoRepository extends AutomationResultsMongoDao {

    private static UsersMongoRepository instance;

    private MongoCollection<Document> collection;
    private Gson gson;

    private UsersMongoRepository() {
        super("automation-results");
        collection = database.getCollection("user");
        gson = new GsonBuilder().registerTypeAdapter(ObjectId.class,
                (JsonDeserializer<ObjectId>) (je, type, jdc) -> new ObjectId(je.getAsJsonObject().get("$oid").getAsString())
            ).create();
    }

    public static UsersMongoRepository getInstance() {
        if (instance == null) {
            instance = new UsersMongoRepository();
        }
        return instance;
    }

    public List<User> findAll() {
        List<User> results = new ArrayList<>();
        collection.find().forEach(userDoc -> results.add(gson.fromJson(userDoc.toJson(), User.class)));
        return results;
    }

    public User findFirstByName(String name) {
        return gson.fromJson(collection.find(Filters.eq("name", name)).first().toJson(), User.class);
    }

    public User findFirstByAccountRUT(String rut) {
        return gson.fromJson(collection.find(Filters.eq("accountRUT", rut)).first().toJson(), User.class);
    }

    public void save(User user) {
        Document updateDoc = Document.parse(gson.toJson(user));
        updateDoc.remove("_id");
        collection.replaceOne(Filters.eq("accountRUT", user.getAccountRUT()), updateDoc);
    }

}
