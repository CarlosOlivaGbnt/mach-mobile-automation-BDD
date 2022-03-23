package com.mach.core.db;

import com.mach.core.util.EnumCardDeliveryStatus;
import com.mach.core.util.RandomValues;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PhysicalCardLogisticsServiceDAO extends AutomationMongoDao {

	private MongoCollection<Document> collectionPhysicalCardTrackings;
	private MongoCollection<Document> collectionDeliveryDelayTrackers;

	private static final String MACH_ID = "machId";
	private static final String CREATED_AT = "createdAt";
	private static final String UPDATED_AT = "updatedAt";
	private static final String STATUS_HISTORY = "statusHistory";
	private static final String COURIER_TRACKING_NUMBER = "courierTrackingNumber";
	private static final String CURRENT_STATE = "currentState";
	private static final String PHYSICAL_CARD_ID = "physicalCardId";

	public PhysicalCardLogisticsServiceDAO() {
		super("physical-card-logistics-service");
		collectionPhysicalCardTrackings = database.getCollection("physicalcardtrackings");
		collectionDeliveryDelayTrackers = database.getCollection("deliverydelaytrackers");
	}

	public String getStateDelivery(String machId) {
		Document myDoc = collectionPhysicalCardTrackings.find(Filters.eq(MACH_ID, machId)).first();
		return myDoc == null ? null : myDoc.getString(CURRENT_STATE);
	}

	public Boolean removeStateDelivery(String machId) {
		return collectionPhysicalCardTrackings.deleteOne(Filters.eq(MACH_ID, machId)).getDeletedCount() == 1;
	}
	
	/**
	 * set the state delivery of physical card in collection Physicalcardtrackings by the state
	 * @param machId
	 * @param state use ENUM_VALID_STATUS.
	 * @return
	 */
	public boolean setStateDelivery(String machId, EnumCardDeliveryStatus state) {

		Document myDoc = collectionPhysicalCardTrackings.find(Filters.eq(MACH_ID, machId)).first();
		myDoc.remove(CURRENT_STATE);
		myDoc.append(CURRENT_STATE, state.getConstDB());
		return collectionPhysicalCardTrackings.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
	}

	public boolean addPhysicalCardLogistics(String machId, String documentNumber, String applicationId, ObjectId physicalCardApplicationsId, ObjectId physicalCardId) {

		ObjectId physicalCardTrackingId = new ObjectId();
		Document doc = new Document();

		doc.append("preStamped", false);
		doc.append("isDeliveryDelayed", false);
		doc.append("attemptNumber", 1);
		doc.append(MACH_ID, machId);
		doc.append(PHYSICAL_CARD_ID, physicalCardId.toString());
		doc.append(CREATED_AT, Instant.now());
		doc.append(UPDATED_AT, Instant.now());
		doc.append("receptionAcknowledgedAt", Instant.now());
		doc.append("__v", 0);
		doc.append("trackingNumber", getNewRandomTrackingNumber());
		doc.append("physicalCardTrackingId", physicalCardTrackingId.toString());
		doc.append(CURRENT_STATE, EnumCardDeliveryStatus.STATUS_004_DELIVERED.getConstDB());
		doc.append(COURIER_TRACKING_NUMBER, "");

		List<Document> array = new ArrayList<>();
		Document docHistory = new Document();
		docHistory.append("state", EnumCardDeliveryStatus.STATUS_001_ISSUED.getConstDB());
		docHistory.append(CREATED_AT, Instant.now());
		docHistory.append(UPDATED_AT, Instant.now());
		array.add(docHistory);
		doc.append(STATUS_HISTORY,array);

		doc.append("physicalCardApplication", getDocumentPhysicalCardApplication(machId, physicalCardId, documentNumber, applicationId, physicalCardApplicationsId));
		doc.append("deliveryAddress", getDocumentAddress());

		collectionPhysicalCardTrackings.insertOne(doc);
		return true;

	}

	private Document getDocumentPhysicalCardApplication(String machId, ObjectId physicalCardId, String documentNumber, String applicationId, ObjectId physicalCardApplicationsId) {

		Document doc = new Document();
		doc.append("_id", physicalCardApplicationsId);
		doc.append(MACH_ID, machId);
		doc.append(CREATED_AT, Instant.now());
		doc.append(UPDATED_AT, Instant.now());
		doc.append("__v", 0);
		doc.append("completedAt", Instant.now());
		doc.append(PHYSICAL_CARD_ID, physicalCardId);
		doc.append("applicationId", applicationId);
		doc.append("documentNumber", documentNumber);
		doc.append("deliveryTime", 10);

		doc.append("profile",getDocumentProfile());
		doc.append("payment",getDocumentPayment());
		doc.append("address",getDocumentAddress());

		return doc;
	}

	 public static Document getDocumentPayment() {
		
		Document payment = new Document();
		payment.append(CREATED_AT, Instant.now());
		payment.append(UPDATED_AT, Instant.now());
		payment.append("amount", 4550);
		payment.append("type", "initial-campaign");
		payment.append("contract", "71822941646303780000");
		payment.append("annulmentReference", "515836bb1d647dc");

		Document paymentEvidence = new Document();
		paymentEvidence.append("transactionNumber", 994);
		paymentEvidence.append("contract", "71822941646303780000");
		paymentEvidence.append("movementExtractNumber", 5137475);
		paymentEvidence.append("balance", 3908.54220907296);
		paymentEvidence.append("processorId", "994_5137475");
		payment.append("paymentEvidence",paymentEvidence);
		return payment;
	}

	 public static Document getDocumentProfile() {
		
		Document profile = new Document();
		profile.append("firstName", "Julia");
		profile.append("lastName", "Aguilera");
		profile.append("email", "j.aguilera@yopmail.com");
		profile.append(CREATED_AT, Instant.now());
		profile.append(UPDATED_AT, Instant.now());
		profile.append("phoneNumber", "56951155162");
		return profile;
	}

	 public static Document getDocumentAddress() {

		Document address = new Document();
		address.append("streetNumber", "33");
		address.append("addressExtra", "depto 44");
		address.append("message", "cerca de los bomberos");
		address.append("streetName", "calle larga");
		address.append(CREATED_AT, Instant.now());
		address.append(UPDATED_AT, Instant.now());

		Document region = new Document();
		region.append("code","04");
		region.append("name","Región de Coquimbo");

		Document borough = new Document();
		borough.append("code","10");
		borough.append("name","Illapel");

		address.append("borough",borough);
		address.append("region",region);

		return address;
	}

	/**
	 * get a new random trackingNumber for the collection Physical card trackings
	 * @return
	 */
	private String getNewRandomTrackingNumber(){
		String id = getRandom25digits();
		while (existTrackingNumber(id))
		{
			id = getRandom25digits();
		}
		return id;
	}

	/**
	 * return true if the trackingNumber exists in the collection Physical card trackings
	 * @param trackingNumber
	 * @return
	 */
	private boolean existTrackingNumber(String trackingNumber){
		Document myDoc = collectionPhysicalCardTrackings.find(Filters.eq("trackingNumber", trackingNumber)).first();
		return myDoc != null;
	}

	/**
	 * return a random string with 25 digits
	 * @return
	 */
	private String getRandom25digits(){
		StringBuilder response = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			response.append(RandomValues.getValue(10000, 99999));
		}
		return response.toString();
	}

	public boolean updateCourierTrackingNumber(String machId, String courierTrackingNumber) {
		Document myDoc = collectionPhysicalCardTrackings.find(Filters.eq(MACH_ID, machId)).first();
		if(myDoc != null ){
			if(myDoc.containsKey(COURIER_TRACKING_NUMBER)) {
				myDoc.remove(COURIER_TRACKING_NUMBER);
			}
			if(courierTrackingNumber != null){
				myDoc.append(COURIER_TRACKING_NUMBER, courierTrackingNumber);
			}
			return collectionPhysicalCardTrackings.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
		}
		return false;
	}

	public Document getCardTrackingIdDocument(String physicalCardId) {
		Document filter = new Document(PHYSICAL_CARD_ID, physicalCardId);
		Document projection = new Document("_id", true);
		return collectionPhysicalCardTrackings.find(filter).projection(projection).first();
	}

	public boolean updateStateWithHistory(String physicalCardId, EnumCardDeliveryStatus status, String stateCause) {
		Document filter = new Document(PHYSICAL_CARD_ID, physicalCardId);
		Document update = new Document();
		Document history = new Document();
		history.append("state", status.getConstDB());
		if(stateCause != null && !stateCause.isEmpty()){
			history.append("stateCause", stateCause);
		}
		history.append(CREATED_AT, Instant.now());
		history.append(UPDATED_AT, Instant.now());
		update.append("$set", new Document(CURRENT_STATE, status.getConstDB()));
		update.append("$addToSet", new Document(STATUS_HISTORY, history));
		return collectionPhysicalCardTrackings.updateOne(filter, update).wasAcknowledged();
	}
	public boolean updateStateWithHistory(String physicalCardId, EnumCardDeliveryStatus status) {
		return updateStateWithHistory(physicalCardId,status,null);
	}

	public boolean collectionDeliveryDelayTrackersInsertOne(Document document) {
		return collectionDeliveryDelayTrackers.insertOne(document).wasAcknowledged();
	}

	public boolean collectionDeliveryDelayTrackersDeleteOne(Document document) {
	    return collectionDeliveryDelayTrackers.deleteOne(document).wasAcknowledged();
	}

	public boolean removeStateHistory(String machId) {
		List<Document> array = new ArrayList<>();
		Document myDoc = collectionPhysicalCardTrackings.find(Filters.eq(MACH_ID, machId)).first();
		myDoc.remove(STATUS_HISTORY);
		myDoc.append(STATUS_HISTORY, array);
		return collectionPhysicalCardTrackings.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
	}
}
