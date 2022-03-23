package com.mach.core.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class EolianServiceDAO extends AutomationMongoDao {

    MongoCollection<Document> collectionUsers;
    MongoCollection<Document> collectionPermissions;

    private static final String USERS = "users";

    public EolianServiceDAO() {
    	super("eolian-service");
        collectionUsers = database.getCollection(USERS);
        collectionPermissions = database.getCollection("permissions");
    }

    public boolean addUserIntoPermission(String machId) {

        Document myDoc = collectionPermissions.find(Filters.eq("name", "prepaid_cards_pin_state_enabled")).first();
        List<String> users = (List<String>) myDoc.get(USERS);
        users.add((machId));
        myDoc.remove(USERS);
        myDoc.append(USERS, users);
        return collectionPermissions.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
    }

    public boolean addUser(String machId) {

        if(collectionUsers.find(Filters.eq("machId", machId)).first() != null) {
            return true;
        }

        Document myDoc = new Document();
        List<Document> groups = new ArrayList<>();
        myDoc.append("groups", groups);
        myDoc.append("machId", machId);
        myDoc.append("__v", 0);
        collectionUsers.insertOne(myDoc);
        return true;
    }

}
