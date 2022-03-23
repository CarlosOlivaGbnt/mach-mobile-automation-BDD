package com.mach.core.util.transaction;

import com.mach.core.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TrasactionFactory {

    public Transaction getRandomTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount("10000");
        transaction.setMessage(getRandomTransactionMessage());
        return transaction;
    }
    
	public static String getRandomTransactionMessage() {
		return "Automation " + UUID.randomUUID().toString().substring(0, 10);
	}
	
	public static List<String> getCashOutTEFValidAmounts() {
		List<String> amounts = new ArrayList<>();
        amounts.add("5000");
        amounts.add("10000");
        amounts.add("20000");
        amounts.add("40000");
        amounts.add("60000");
        amounts.add("80000");
        amounts.add("100000");
        return amounts;
	}
	
}
