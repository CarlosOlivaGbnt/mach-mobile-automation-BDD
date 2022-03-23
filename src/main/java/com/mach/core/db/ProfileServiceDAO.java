package com.mach.core.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class ProfileServiceDAO extends AutomationMongoDao {

	private MongoCollection<Document> collectionAddresses;
	private MongoCollection<Document> collectionConnectedAccounts;
	private MongoCollection<Document> collectionProfiles;
	private MongoCollection<Document> collectionAdditionalProfileInfos;
	private static final String EMAIL = "email";
	private static final String MACH_ID = "machId";

	public enum ValidSetFields
	{ 
		FIRST_NAME("firstName"), LAST_NAME("lastName"), EMAIL(ProfileServiceDAO.EMAIL);
		
		private String label; 
		  
	    public String getLabel() 
	    { 
	        return this.label; 
	    } 
	  
	    private ValidSetFields(String label) 
	    { 
	        this.label = label; 
	    } 
	}
	public enum ValidFieldAdditionalProfileInfos
	{
		ADDRESS("address"), ADDRESS_NUMBER("addressNumber"), ADDRESS_EXTRA("addressExtra");

		private String label;

		public String getLabel()
		{
			return this.label;
		}

		private ValidFieldAdditionalProfileInfos(String label)
		{
			this.label = label;
		}
	}

	public ProfileServiceDAO() {
		super("profile-service");
		collectionAddresses = database.getCollection("addresses");
		collectionConnectedAccounts = database.getCollection("connectedaccounts");
		collectionProfiles = database.getCollection("profiles");
		collectionAdditionalProfileInfos = database.getCollection("additionalprofileinfos");
	}

	/**
	 * Remover Vinculacion con google por email
	 * 
	 * @param email
	 * @return
	 */
	public Boolean removeConnectedAccountsByMail(String email) {
		return collectionConnectedAccounts.deleteOne(Filters.eq(EMAIL, email)).getDeletedCount() == 1;
	}

	public String getProfileEmailByMachId(String machId) {
		return collectionProfiles.find(Filters.eq(MACH_ID, machId)).first().getString(EMAIL);
	}
	
	public boolean removeAddressesByMachId(String machId) {
		return collectionAddresses.deleteOne(Filters.eq(MACH_ID, machId)).getDeletedCount() == 1;
	}

	public boolean setProfiles(ValidSetFields field, String machId, String txtNameOriginal) {
		
		Document myDoc = collectionProfiles.find(Filters.eq(MACH_ID, machId)).first();
		
		myDoc.remove(field.getLabel());
		myDoc.append(field.getLabel(), txtNameOriginal);

		return collectionProfiles.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;

	}

	public Document findAdditionalInfoByMachId(String machId) {
		return collectionAdditionalProfileInfos.find(Filters.eq(MACH_ID, machId)).first();
	}

	/**
	 * Return the AdditionalProfileInfo (address,addressNumber,addressExtra) by machId
	 * @param machId
	 * @param field
	 * @return
	 */
	public String getAdditionalProfileInfo(String machId, ValidFieldAdditionalProfileInfos field) {
		Document doc = collectionAdditionalProfileInfos.find(Filters.eq(MACH_ID, machId)).first();

		if(doc == null){
			return "";
		}

		return doc.getString(field.getLabel());
	}



}
