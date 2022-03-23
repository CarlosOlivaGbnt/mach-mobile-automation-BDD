package com.mach.core.db;

import com.mach.core.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountServiceDAO extends AutomationMongoDao {

	private MongoCollection<Document> collectionAccounts;
    private static final Logger LOG = LoggerFactory.getLogger(AccountServiceDAO.class);
    
    public AccountServiceDAO() {
    	super("account-service");
    	collectionAccounts = database.getCollection("accounts");
    }

    /**
     * return the same user with the machID atribute
     * @param user
     * @return user
     */
    public User loadMachIdByRut(User user) {
        String rut = user.getAccountRUT().toUpperCase();
        Document myDoc = collectionAccounts.find(Filters.eq("documentNumber", rut)).first();
        if(myDoc == null){
            LOG.error("can not load user by rut: {}", rut);
            return null;
        }
        user.setMachId(myDoc.getString("machId"));
        return user;
    }
    
    /**
     * return true if the documentNumber exist
     * @param documentNumber
     * @return 
     */
    public boolean isDocumentNumberExist(String documentNumber) {
    	Document myDoc = collectionAccounts.find(Filters.eq("documentNumber", documentNumber.toUpperCase())).first();
    	return myDoc != null;
    }

}
