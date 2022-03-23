package com.mach.core.db;

import com.mach.core.exception.MachException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

public class PhoneVerificationServiceDAO extends AutomationMongoDao {

	private MongoCollection<Document> collection;
    
    public PhoneVerificationServiceDAO() {
    	super("phone-verification-service");
        collection = database.getCollection("phones");
    }

    /**
     * return true if the Phone Number Exist in the database
     * @param phone eight numbers. example 87654321
     * @return
     * @throws Exception
     */
    public boolean isPhoneNumberExist(String phone) {
        String phoneNumber = getFullPhoneNumber(phone);
        Document myDoc = collection.find(Filters.eq("phone", phoneNumber)).first();
        return myDoc != null;
    }

    /**
     * return the machId if the phone exists, otherwise return null
     * @param phone eight numbers. example 87654321
     * @return the machId or null
     * @throws Exception
     */
    public String getMachidByPhone(String phone) {
        String phoneNumber = getFullPhoneNumber(phone);
        Document myDoc = collection.find(Filters.eq("phone", phoneNumber)).first();

        if(myDoc == null){
            return null;
        }

        return myDoc.getString("machId");
    }

    /**
     * return the full phone number 56912345678 if the phoneNumber is as expected
     * @param phoneNumber
     * @return
     * @throws Exception
     */
    private String getFullPhoneNumber(String phoneNumber) {
        if(phoneNumber.length()!=8 && !StringUtils.isNumeric(phoneNumber)){
            throw new MachException("PhoneVerificationServiceDAO::loadMachIdByRut:: phoneNumber must be 8 numbers");
        }
        return "569" + phoneNumber;
    }


}
