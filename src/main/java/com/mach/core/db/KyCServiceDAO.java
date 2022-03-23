package com.mach.core.db;

import com.mach.core.exception.MachException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.Instant;

public class KyCServiceDAO extends AutomationMongoDao {

    MongoCollection<Document> collection;

    public KyCServiceDAO() {
    	super("kyc-service");
        collection = database.getCollection("customerverifications");
    }

    /**
     * Return true if the user have the Validation TEF
     * @param machId
     * @return
     */
    public boolean hasUserValidationTEF(String machId) {
        if(machId.isEmpty()){
            throw new MachException("machId must be not empty");
        }
        Document myDoc = collection.find(Filters.eq("machId", machId)).first();
        return myDoc != null;
    }

    /**
     * Add a Validation TEF to the machId
     * @param machId
     * @return
     */
    public void addValidationTEF(String machId) {

        if(hasUserValidationTEF(machId)){
            return;
        }
        final String now = Instant.now().toString();
        Document myDoc = new Document();
        myDoc.append("machId", machId);
        myDoc.append("__v", 0);
        myDoc.append("type", "TEF");
        myDoc.append("createdAt", now);
        myDoc.append("updatedAt", now);
        myDoc.append("verifiedAt", now);

        collection.insertOne(myDoc);
    }

}
