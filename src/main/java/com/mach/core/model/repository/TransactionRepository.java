package com.mach.core.model.repository;

import com.mach.core.model.Transaction;
import com.mach.core.util.EnumData;
import com.mach.core.util.EnumIdentifiers;
import com.mach.core.util.transaction.TrasactionFactory;
import data.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {

    private Map<String, Transaction> transactionMap;

    @Autowired
    private TrasactionFactory trasactionDataFactory;

    public TransactionRepository() {
        transactionMap = new HashMap<>();
    }
    
    /**
     * Get or create a stored (suite-persistant) {@link Transaction}.
     * @param transactionIdentifier must start with either of {@link EnumIdentifiers}
     * @param transactionPredicate can be obtained from by... methods on this class.
     * @return
     */
    public synchronized Transaction getStoredTransaction(String transactionIdentifier, Predicate<Transaction> transactionPredicate) {
        if (transactionMap.containsKey(transactionIdentifier)) {
            return transactionMap.get(transactionIdentifier);
        }
        
        if (transactionIdentifier.startsWith(EnumIdentifiers.RANDOM_TRANSACTION.getValue())) {
            transactionMap.put(transactionIdentifier, trasactionDataFactory.getRandomTransaction());
        } else {
            transactionMap.put(transactionIdentifier, getTransaction(transactionPredicate));
        }
        
        return transactionMap.get(transactionIdentifier);
    }
    
    /**
     * Get a specific transaction from the Transactions.json file.
     *
     * @return A transaction of {@link Transaction}
     */
    public Transaction getTransaction(Predicate<Transaction> predicate) {
    	List<Transaction> transactions = TestData.getObjects(EnumData.TRANSACTIONS); 
        Optional<Transaction> optTransaction = transactions.stream()
                .filter(predicate)
                .findAny();
        return optTransaction.isPresent() ? optTransaction.get() : null;
    }

    /**
     * Get a list of transactions from the Transactions.json file.
     *
     * @return A collection of transaction of {@link List<Transaction>}
     */
    public List<Transaction> getTransactions(Predicate<Transaction> predicate) {
    	List<Transaction> transactions = TestData.getObjects(EnumData.TRANSACTIONS); 
        return transactions.stream().filter(predicate).collect(Collectors.toList());
    }

    public Predicate<Transaction> byMessage(String message) {
        return transaction -> message.equals(transaction.getMessage());
    }

    public Predicate<Transaction> byType(String type) {
        return transaction -> transaction.getType().equals(type);
    }

    public Predicate<Transaction> byTypeAndComment(String type, String comment) {
        return transaction -> (transaction.getType().equals(type) && transaction.getMessage().equals(comment));
    }

}
