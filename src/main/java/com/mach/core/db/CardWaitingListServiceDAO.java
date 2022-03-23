package com.mach.core.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class CardWaitingListServiceDAO extends AutomationMongoDao {

	private MongoCollection<Document> collectionPhysicalcardrequests;

	public CardWaitingListServiceDAO() {
		super("card-waiting-list-service");
		collectionPhysicalcardrequests = database.getCollection("physicalcardrequests");
	}

	public Integer getNumberQueueByMachId(String machId) {
		Document myDoc = collectionPhysicalcardrequests.find(Filters.eq("machId", machId)).first();
		return myDoc.getInteger("initialIndex");
	}

	public Boolean removeQueueByMachId(String machId) {
		return collectionPhysicalcardrequests.deleteOne(Filters.eq("machId", machId)).getDeletedCount() == 1;
	}

}
