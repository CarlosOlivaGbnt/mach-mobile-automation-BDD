package com.mach.core.db;

import com.mach.core.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.Instant;

public class EmailVerificationServiceDAO extends AutomationMongoDao {

	private MongoCollection<Document> collectionEmails;

	private static final String MACH_ID = "machId";
	private static final String CONFIRMED_AT = "confirmedAt";
	private static final String EMAIL = "email";
	private static final String UNIQUE_EMAIL = "uniqueEmail";
	private static final String UNCONFIRMED_EMAIL = "unconfirmedEmail";

	public EmailVerificationServiceDAO() {
		super("email-verification-service");
		collectionEmails = database.getCollection("emails");
	}
	
	/**
	 * return the machId by email
	 * @param googleMail
	 * @return
	 */
	public String getMachIdByEmails(String googleMail) {
		
		Document myDoc = collectionEmails.find(Filters.eq(EMAIL, googleMail)).first();

		if (myDoc == null) {
			return null;
		}

		return myDoc.getString(MACH_ID);
	}

	/**
	 * Change the mail from Validated (or confirmed) to Unvalidated (or unconfirmed) by emailValidated
	 * its opposite to {@link #confirmUserEmail}
	 * @param emailValidated
	 * @param emailNotValidated
	 * @return true if the change can be made
	 */
	public boolean undoEmailValidationByEmail(String emailValidated, String emailNotValidated) {

		Document myDoc = collectionEmails.find(Filters.eq(EMAIL, emailValidated)).first();
		
		if(myDoc == null) {
			return false;
		}
		
		removeIfKeyExist(myDoc, UNCONFIRMED_EMAIL);
		removeIfKeyExist(myDoc, CONFIRMED_AT);
		removeIfKeyExist(myDoc, EMAIL);
		removeIfKeyExist(myDoc, UNIQUE_EMAIL);
		
		myDoc.append(UNCONFIRMED_EMAIL, emailNotValidated);

		return collectionEmails.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;

	}
	
	/**
	 * Set the email unconfirmed by another one
	 * @param machId
	 * @param unconfirmedEmail
	 * @return
	 */
	public boolean setUnconfirmedEmail(String machId, String unconfirmedEmail) {

		Document myDoc = collectionEmails.find(Filters.eq(MACH_ID, machId)).first();
		myDoc.remove(UNCONFIRMED_EMAIL);
		myDoc.append(UNCONFIRMED_EMAIL, unconfirmedEmail);

		return collectionEmails.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
		
	}
	
	/**
	 * return true if the machId has any Validated or Confirmed email
	 * @param machId
	 * @return
	 */
	public boolean getConfirmedEmail(String machId) {
		return collectionEmails.find(Filters.eq(MACH_ID, machId)).first().containsKey(EMAIL);
	}
	
	private void removeIfKeyExist(Document myDoc, String key) {

		if(myDoc.containsKey(key)) {
			myDoc.remove(key);
		}
		
	}
	
	public boolean isEmailExist(String email) {
		
		Document myDoc = collectionEmails.find(Filters.eq(EMAIL, email)).first();
		return myDoc != null;
	}

	/**
	 * Change the mail from Unvalidated (or unconfirmed) to Validated (or confirmed) by user.getEmail()
	 * its opposite to {@link #undoEmailValidationByEmail}
	 * @param user
	 */
	public boolean confirmUserEmail(User user) {
		Document myDoc = collectionEmails.find(Filters.eq(MACH_ID, user.getMachId())).first();
		myDoc.remove(UNCONFIRMED_EMAIL);
		myDoc.append(EMAIL, user.getEmail());
		myDoc.append(UNIQUE_EMAIL, user.getEmail());
		myDoc.append(CONFIRMED_AT,  Instant.now());
		return collectionEmails.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
	}
}
