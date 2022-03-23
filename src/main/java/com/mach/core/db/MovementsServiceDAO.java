package com.mach.core.db;

import com.mach.core.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class MovementsServiceDAO extends AutomationMongoDao {

    public MovementsServiceDAO() {
        super("movements-service");
    }

    public boolean removeUserInteractionsByMessage(User user, String message) {
        MongoCollection<Document> collection = database.getCollection("interactions");
        final DeleteResult deleteResult = collection.deleteMany(and(eq("movements.message", message), eq("machId", user.getMachId())));
        return deleteResult.wasAcknowledged();
    }

    public boolean removeUserMovementsByMessage(User user, String message) {
        MongoCollection<Document> collection = database.getCollection("movements");
        final DeleteResult deleteResult = collection.deleteMany(and(eq("message", message),eq("fromMachId", user.getMachId())));
        return deleteResult.wasAcknowledged();
    }

}
