package com.mach.core.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class AuthenticationServiceDAO extends AutomationMongoDao {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceDAO.class);

    private static final String COMPLETED_AT = "completedAt";
    private static final String CHALLENGES = "challenges";

	private MongoCollection<Document> collectionAuthentications;
    
    public AuthenticationServiceDAO() {
    	super("authentication-service");
    	collectionAuthentications = database.getCollection("authentications");
    }

    /**
     * Delete the authentication challenge by sending the machId
     * @param machId
     * @return true if the delete was succesful
     * @throws Exception
     */
    public boolean deleteAuthenticationChallenge(String machId){
        return collectionAuthentications.deleteOne(Filters.eq("machId", machId)).getDeletedCount() == 1;
    }
    
    /**
     * Delete all the authentication challenge from today and for the "goal" -> "sign-up" or "recover-account"
     * @return
     */
    public boolean deleteTodaysAuthenticationChallenge(String device){
        if(device.isEmpty()){
            LOG.warn("Device udid is necessary for delete Challenges, device isEmpty");
            return false;
        }
        return collectionAuthentications.deleteMany(
    	        Filters.and(
    	                Filters.eq("deviceId", device),
                        Filters.or(Filters.eq("goal","sign-up"),Filters.eq("goal","recover-account")),
                        Filters.exists(COMPLETED_AT, false)))
                .getDeletedCount() >= 1;
    }

    private Document getLastIncompleteRecord(String rut){
        if(rut == null || rut.isEmpty()){
            LOG.info("rut must be not null or empty");
        }
        return collectionAuthentications.find(
                Filters.and(
                        Filters.eq("rut", rut),
                        Filters.or(
                                Filters.eq("goal","sign-up"),
                                Filters.eq("goal","recover-account")),
                        Filters.exists(CHALLENGES, true)
                )
        ).sort(Filters.eq("createdAt", -1)).first();
    }

    private List<Document> getChallengesArray(Document lastIncompleteRecord){
        return (List<Document>) lastIncompleteRecord.get(CHALLENGES);
    }

    private Document lastIncompleteChallenge(String rut){
        Document lastIncompleteRecord = getLastIncompleteRecord(rut);

        if(lastIncompleteRecord == null) {
            LOG.info("not challenges found");
            return null;
        }

        List<Document> challengesArray = getChallengesArray(lastIncompleteRecord);

        return challengesArray.get(challengesArray.size()-1);
    }

    private Document completeLastChallenge(Document lastChallenge){
        final Date now = Date.from(Instant.now());

        lastChallenge.replace("attemptsRemaining", 2);
        lastChallenge.append(COMPLETED_AT, now);

        return lastChallenge;
    }

    private void addChallenge(Document lastChallenge, String nameChallenge, Document lastIncompleteRecord, List<Document> challengesArray){
        final Date now = Date.from(Instant.now());

        Document newChallenge = new Document();
        newChallenge.append("authenticationLevel", 0);
        newChallenge.append("attemptsRemaining", 2);
        newChallenge.append("_id", lastChallenge.get("_id"));
        newChallenge.append("name", nameChallenge);
        newChallenge.append(COMPLETED_AT, now);

        challengesArray.add(newChallenge);

        lastIncompleteRecord.replace(CHALLENGES, challengesArray);

        collectionAuthentications.replaceOne(Filters.eq("_id", lastIncompleteRecord.get("_id")), lastIncompleteRecord);

    }

    /**
     * @param rut of user
     * this method should only be called:
     *            -* during account creation or recovery, and
     *            -* after entering the RUT, and
     *            -* before creating PIN
     *
     *  values that can be returned:
     *            verify-id-document
     *            request-tef-verification
     *            check-tef-verification
     *            request-new-phone-verification
     *            check-new-phone-verification
     *            request-email-verification
     *            check-email-verification
     *            validate-security-pin
     *            request-automated-selfie
     *            check-automated-selfie
     *            get-result-automated-selfie
     *            request-selfie
     *            await-result-selfie
     *            get-result-selfie
     *            request-phone-verification
     *            check-phone-verification
     * @return name of next challenge.
     */
    public String getNextChallenge(String rut){
        Document lastIncompleteChallenge = lastIncompleteChallenge(rut);

        if(lastIncompleteChallenge == null){
            LOG.info("not incomplete challenges found");
            return null;
        }

        if(lastIncompleteChallenge.containsKey("skippedAt") || lastIncompleteChallenge.containsKey(COMPLETED_AT)){
            //if the challenge has the attributes completedAt or skippedAt then it can not be the next challenge
            LOG.info("there is not incomplete challenge");
            return null;
        }
        LOG.info("Next Challenge: {}", lastIncompleteChallenge.getString("name"));
        return lastIncompleteChallenge.getString("name");
    }

    /**
     * @param rut of user
     *            Update database and mark passed challenge Onfido (video)
     *              request-automated-selfie
     *              check-automated-selfie
     *              get-result-automated-selfie
     */
    public void passOnfido(String rut){
        Document lastIncompleteRecord = getLastIncompleteRecord(rut);

        Document lastIncompleteChallenge = lastIncompleteChallenge(rut);

        if(lastIncompleteChallenge == null){
            LOG.info("not last challenge found");
            return;
        }

        List<Document> challengesArray = getChallengesArray(lastIncompleteRecord);

        challengesArray.set(challengesArray.size()-1, completeLastChallenge(lastIncompleteChallenge));

        addChallenge(lastIncompleteChallenge, "get-result-automated-selfie", lastIncompleteRecord, challengesArray);
        LOG.info("Challenge Onfido marked as passed from database");
    }

    /**
     * @param rut of user
     *            Update database and mark passed challenge Selfie (photo)
     *              request-selfie
     *              await-result-selfie
     *              get-result-selfie
     */
    public void passSelfie(String rut){
        Document lastIncompleteRecord = getLastIncompleteRecord(rut);

        Document lastIncompleteChallenge = lastIncompleteChallenge(rut);

        if(lastIncompleteChallenge == null){
            LOG.info("not last challenge found");
            return;
        }

        List<Document> challengesArray = getChallengesArray(lastIncompleteRecord);

        challengesArray.set(challengesArray.size()-1, completeLastChallenge(lastIncompleteChallenge));

        addChallenge(lastIncompleteChallenge, "await-result-selfie", lastIncompleteRecord, challengesArray);

        addChallenge(lastIncompleteChallenge, "get-result-selfie", lastIncompleteRecord, challengesArray);

        LOG.info("Challenge Selfie marked as passed from database");
    }

}
