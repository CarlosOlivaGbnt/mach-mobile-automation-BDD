package com.mach.core.db;

import com.mach.core.exception.MachException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;


public class PrepaidCardsServiceDAO extends AutomationMongoDao {

	private MongoCollection<Document> collectionPhysicalCardApplications;
	private MongoCollection<Document> collectionPhysicalCards;
	private MongoCollection<Document> collectionPhysicalCardWhitelists;
	private MongoCollection<Document> collectionPhysicalCardCovid19Whitelists;
	// tarjeta virtual
	private MongoCollection<Document> collectionPrepaidCards;

	private static final String MACH_ID = "machId";
	private static final String DOCUMENT_NUMBER = "documentNumber";
	private static final String CREATED_AT = "createdAt";
	private static final String UPDATED_AT = "updatedAt";
	private static final String PHYSICAL_CARD_ID = "physicalCardId";
	private static final String DELIVERY_TIME = "deliveryTime";
	private static final String IS_TOKENIZED = "isTokenized";
	private static final String STATE = "state";
	private static final String APPLICATION_ID = "applicationId";
	private static final String PAYMENT = "payment";
	private static final String PIDE_TU_TARJETA_FISICA = "Pide tu tarjeta física";

	public enum StatusPhysicalCard {

		EMITTED("EMITTED","Validando solicitud"),
		PENDING("PENDING","Validando solicitud"),
		VALIDATING("VALIDATING","En validación"),
		PIN_PENDING("PIN_PENDING","Crear clave tarjeta"),
		PIN_CHANGING("PIN_CHANGING","Configurando clave"),
		ACTIVE("ACTIVE","4444"),
		FROZEN("FROZEN","Bloqueada"),
		FREEZING("FREEZING","Bloqueando"),
		UNFREEZING("UNFREEZING","Desbloqueando"),
		BLOCKING("BLOCKING","Bloqueando tu tarjeta"),
		BLOCKED("BLOCKED",PIDE_TU_TARJETA_FISICA),
		BANNED("BANNED",PIDE_TU_TARJETA_FISICA),
		DISABLED("DISABLED","Bloqueada por seguridad"),
		//ANNULLING("ANNULLING","Anulando solicitud"), // desde el front no se deberia llegar a este estado. El desarrollo desde tecnocom quedo incompleto
		ANNULLED("ANNULLED",PIDE_TU_TARJETA_FISICA);

		private String label;
		private String title;
		public String getLabel() {
			return this.label;
		}
		public String getTitle() {
			return this.title;
		}
		StatusPhysicalCard(String label, String title) {
			this.label = label;
			this.title = title;
		}
	}

	public PrepaidCardsServiceDAO() {
		super("prepaid-cards-service");
		collectionPhysicalCardWhitelists = database.getCollection("physicalcardwhitelists");
		collectionPhysicalCardCovid19Whitelists = database.getCollection("physicalcardcovid19whitelists");
		collectionPhysicalCardApplications = database.getCollection("physicalcardapplications");
		collectionPhysicalCards = database.getCollection("physicalcards");
		collectionPrepaidCards = database.getCollection("prepaidcards");
	}

	public boolean addUserToPhysicalCardWhitelist(String idMach, String documentNumber) {
		Instant now = Instant.now();
		Document myDoc = new Document();
		myDoc.append(MACH_ID, idMach);
		myDoc.append("__v", 0);
		myDoc.append(DOCUMENT_NUMBER, documentNumber);
		myDoc.append(CREATED_AT, now);
		myDoc.append(UPDATED_AT, now);

		collectionPhysicalCardWhitelists.insertOne(myDoc);
		return true;

	}
	
	public boolean removePhysicalCardWhitelistsByMachId(String machId) {
		return collectionPhysicalCardWhitelists.deleteOne(Filters.eq(MACH_ID, machId)).getDeletedCount() == 1;
	}

	/**
	 * remove all Card Applications By MachId
	 * @param machId
	 * @return
	 */
	public boolean removePhysicalCardApplicationsByMachId(String machId) {
		return collectionPhysicalCardApplications.deleteMany(Filters.eq(MACH_ID, machId)).getDeletedCount() >= 1;
	}

	/**
	 * Remove only the last Card Applications By MachId
	 * @param machId
	 * @return
	 */
	public boolean removeLastPhysicalCardApplications(String machId) {

		Document myDoc = collectionPhysicalCardApplications.find(Filters.eq(MACH_ID, machId))
				.sort(Filters.eq(CREATED_AT, -1)).first();
		
		if(myDoc == null){
			return false;
		}

		return collectionPhysicalCardApplications.deleteOne(
				Filters.and(Filters.eq(MACH_ID, machId),
						Filters.eq(APPLICATION_ID, myDoc.getString(APPLICATION_ID))))
				.getDeletedCount() == 1;
	}
	
	public boolean removePhysicalCardsByMachId(String machId) {
		return collectionPhysicalCards.deleteOne(Filters.eq(MACH_ID, machId)).getDeletedCount() == 1;
	}

	public boolean setPricePhysicalCardApplicationsByMachId(String machId, int newPrice) {

		Document myDoc = collectionPhysicalCardApplications.find(Filters.eq(MACH_ID, machId)).first();
		myDoc.remove(PAYMENT);

		Instant now = Instant.now();
		Document myNewDoc = new Document();
		myNewDoc.append("type", "initial-campaign");
		myNewDoc.append("amount", newPrice);
		myNewDoc.append(CREATED_AT, now);
		myNewDoc.append(UPDATED_AT, now);
		
		myDoc.append(PAYMENT, myNewDoc);
		
		return collectionPhysicalCardApplications.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
		
	}

	public boolean setDeliveryTimePhysicalCardApplications(String machId, String deliveryTime) {
				
		if (!StringUtils.isNumeric(deliveryTime)) {
			throw new MachException("The deliveryTime is not numeric: " + deliveryTime);
		}
		
		Document myDoc = collectionPhysicalCardApplications.find(Filters.eq(MACH_ID, machId)).first();
		myDoc.remove(DELIVERY_TIME);
		myDoc.append(DELIVERY_TIME, deliveryTime);
		
		return collectionPhysicalCardApplications.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
		
	}

	public boolean addPhysicalCardCovid19Whitelists(String machId, String documentNumber) {

		if (hasUserInPhysicalCardCovid19Whitelists(machId)){
			return false;
		}
		Instant now = Instant.now();
		Document doc = new Document();
		doc.append(MACH_ID, machId);
		doc.append("__v", 0);
		doc.append(CREATED_AT, now);
		doc.append(UPDATED_AT, now);
		doc.append(DOCUMENT_NUMBER, documentNumber);
		collectionPhysicalCardCovid19Whitelists.insertOne(doc);
		return true;
	}

	public boolean hasUserInPhysicalCardCovid19Whitelists(String machId) {
		if(machId.isEmpty()){
			throw new MachException("machId must be not empty");
		}
		Document myDoc = collectionPhysicalCardCovid19Whitelists.find(Filters.eq(MACH_ID, machId)).first();
		return myDoc != null;
	}

	/**
	 * set the state of physical card in collection collectionPhysicalCards by the state
	 * @param machId
	 * @param state use StatusPhysicalCard.
	 * @return
	 */
	public boolean setStatePhysicalCard(String machId, StatusPhysicalCard state) {
		Document myDoc = collectionPhysicalCards.find(Filters.eq(MACH_ID, machId)).first();

		myDoc.remove(STATE);
		myDoc.append(STATE, state.getLabel());

		return collectionPhysicalCards.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
	}

	/**
	 * return the state of the collection PhysicalCards, or null if do not exists
	 * @param machId
	 * @return
	 */
	public String getStatePhysicalCard(String machId) {
		Document myDoc = collectionPhysicalCards.find(Filters.eq(MACH_ID, machId)).first();
		if (myDoc == null) {
			return null;
		}
		return myDoc.getString(STATE);
	}

	public boolean addPhysicalCard(String machId, String physicalCardId) {

		Instant now = Instant.now();
		ObjectId id = new ObjectId();

		Document doc = new Document();
		doc.append("_id", id);
		doc.append(STATE, StatusPhysicalCard.ACTIVE.getLabel());
		doc.append("holderNumber", 2);
		doc.append(MACH_ID, machId);
		doc.append(PHYSICAL_CARD_ID, physicalCardId);
		doc.append(CREATED_AT, now);
		doc.append(UPDATED_AT, now);
		doc.append("__v", 0);
		doc.append("clientNumber", "60024188");
		doc.append("contract", "51666544820185490000");
		doc.append("holderName", "ORLANDO EDGARDO GONZALEZ PEREZ");
		doc.append("expirationMonth", "09");
		doc.append("expirationYear", "2025");
		doc.append("last4Pan", "4444");
		doc.append("panHash", "c6b25ffac5d2c50f41af3ac4d97d07d95fd4f2d5f9fcef98dc709e5741060508");//7187
		collectionPhysicalCards.insertOne(doc);
		return true;

	}

	/**
	 * add a Physical card application in the same collection
	 * @param machId
	 * @param documentNumber
	 * @return
	 */
	public boolean addPhysicalcardApplications(String machId, String documentNumber, String physicalCardId) {

		Instant now = Instant.now();
		ObjectId applicationId = new ObjectId();

		Document doc = new Document();
		doc.append(APPLICATION_ID, applicationId.toString());
		doc.append(MACH_ID, machId);
		doc.append(PHYSICAL_CARD_ID, physicalCardId);
		doc.append(CREATED_AT, now);
		doc.append(UPDATED_AT, now);
		doc.append("completedAt", now);
		doc.append("__v", 0);
		doc.append(DELIVERY_TIME, 10);
		doc.append(DOCUMENT_NUMBER, documentNumber);

		doc.append("profile",PhysicalCardLogisticsServiceDAO.getDocumentProfile());

		doc.append(PAYMENT,PhysicalCardLogisticsServiceDAO.getDocumentPayment());

		doc.append("address",PhysicalCardLogisticsServiceDAO.getDocumentAddress());

		collectionPhysicalCardApplications.insertOne(doc);
		return true;

	}

	public String getApplicationId(String machId) {
		Document myDoc = collectionPhysicalCardApplications.find(Filters.eq(MACH_ID, machId)).first();
		return myDoc == null ? null : myDoc.getString(APPLICATION_ID);
	}

	public ObjectId getPhysicalCardApplicationsId(String machId) {
		Document myDoc = collectionPhysicalCardApplications.find(Filters.eq(MACH_ID, machId)).first();
		return myDoc == null ? null : (ObjectId) myDoc.get("_id");
	}

	public String getEmittedCardID(String machId) {
		Document filter = new Document();
		filter.append(MACH_ID, machId);
		filter.append(STATE, StatusPhysicalCard.EMITTED.getLabel());
		Document projection = new Document(PHYSICAL_CARD_ID, true);
		Document queryResult = collectionPhysicalCards.find(filter).projection(projection).first();
		return queryResult.getString(PHYSICAL_CARD_ID);
	}

	public boolean setIsTokenizedVirtualCard(String machId, boolean value){
		Document myDoc = collectionPrepaidCards.find(Filters.eq(MACH_ID, machId)).first();
		myDoc.remove(IS_TOKENIZED);
		myDoc.append(IS_TOKENIZED, value);
		return collectionPrepaidCards.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
	}

	public boolean addVirtualCard(String machId) {
		Instant now = Instant.now();
		Document doc = new Document();
		doc.append(STATE, "active");
		doc.append("holderNumber", 2);
		doc.append(IS_TOKENIZED, false);
		doc.append(MACH_ID, machId);
		doc.append(CREATED_AT, now);
		doc.append(UPDATED_AT, now);
		doc.append("__v", 0);
		doc.append("clientNumber", "78922137");
		doc.append("contract", "40319428250709080000");
		doc.append("holderName", "ORLANDO EDGARDO GONZALEZ PEREZ");
		doc.append("expirationMonth", "09");
		doc.append("expirationYear", "2025");
		doc.append("last4Pan", "4281");
		collectionPrepaidCards.insertOne(doc);
		return true;
	}

	public boolean removeAllCardApplications(String machId) {
		return collectionPhysicalCardApplications.deleteMany(Filters.eq(MACH_ID, machId)).wasAcknowledged();
	}

	public boolean removeAllVirtualCards(String machId){
		return collectionPrepaidCards.deleteMany(Filters.eq(MACH_ID, machId)).wasAcknowledged();
	}

}
