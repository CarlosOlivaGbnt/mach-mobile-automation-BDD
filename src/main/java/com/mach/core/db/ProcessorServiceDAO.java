package com.mach.core.db;

import com.mach.core.model.repository.ContractsRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class ProcessorServiceDAO extends AutomationMongoDao {

    private MongoCollection<Document> collection;
    private static final String MACH_ID = "machId";
    private static final String CONTRACT = "contract";

    public ProcessorServiceDAO() {
        super("processor-service");
        collection = database.getCollection("contracts");
    }

    /**
     * Assign a user a specific contract which is not repeated in any other user
     * @param machId
     * @param type use this {@link ContractsRepository.ContractsType }
     * @param platform
     * @return
     */
    public boolean updateContract(final String machId, final ContractsRepository.ContractsType type, final ContractsRepository.Platform platform) {
        ContractsRepository contractsRepository = new ContractsRepository();
        String contract = contractsRepository.getContract(type, platform);
        String machIdUsedContract = getMachIdByContract(contract);

        // se itera sobre los usuarios que tengan el mismo contract que quiere utilizar, y se eliminan
        // esto con el fin de asegurar de que solo un unico usuario indicado tenga el contract enviado
        while(machIdUsedContract != null){
            deleteContract(machIdUsedContract);
            machIdUsedContract = getMachIdByContract(contract);
        }

        return replaceContract(machId,contract);
    }

    private boolean deleteContract(String machId) {
        Document myDoc = collection.find(Filters.eq(MACH_ID, machId)).first();
        myDoc.remove(CONTRACT);
        return collection.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
    }

    /**
     * Return the MachId if the user have the contract, otherwise return null
     * @param contract
     * @return
     */
    private String getMachIdByContract(String contract) {

        if(contract.isEmpty()){
            return null;
        }
        Document myDoc = collection.find(Filters.eq(CONTRACT, contract)).first();
        return myDoc == null ? null : myDoc.getString(MACH_ID);
    }

    /**
     * Assign to the user a specific contract
     * @param machId
     * @param contract
     * @return
     */
    private boolean replaceContract(final String machId, final String contract){
        Document myDoc = collection.find(Filters.eq(MACH_ID, machId)).first();
        myDoc.remove(CONTRACT);
        myDoc.append(CONTRACT, contract);
        return collection.replaceOne(Filters.eq("_id", myDoc.get("_id")), myDoc).getModifiedCount() == 1;
    }

}
