package com.mach.core.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class DeviceServiceDAO extends AutomationMongoDao {

    public DeviceServiceDAO() {
    	super("device-service");
    }

    /**
     * Return the machId by sending the UUID
     * @param uuid is the phone identifier
     * @return machId
     * @throws Exception
     */
    public String getMachIdByUUID(String uuid){
        MongoCollection<Document> collection = database.getCollection("devices");
        Document myDoc = collection.find(Filters.eq("uuid", uuid)).first();
        if(myDoc == null) {
        	return "";
        }
        return (String) myDoc.get("machId");
    }

}
