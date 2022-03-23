package com.mach.core.model;

import com.google.gson.GsonBuilder;

import java.util.List;


public class Transaction {

    private String creationTime;
    private String lastInteractionTime;
    private String amount;
    private User sender;
    private List<User> receivers;
    private String type;
    private String message;
    private String bank;
    private String accountNumber;
    private String reaction;
    private String qrCodeNumber;

    public Transaction() {
        super();
    }

    public Transaction(String amount, String type) {
        this.amount = amount;
        this.type = type;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getLastInteractionTime() {
        return lastInteractionTime;
    }

    public void setLastInteractionTime(String lastInteractionTime) {
        this.lastInteractionTime = lastInteractionTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public List<User> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<User> receivers) {
        this.receivers = receivers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
    
	public String getQrCodeNumber() {
		return qrCodeNumber;
	}

	public void setQrCodeNumber(String qrCodeNumber) {
		this.qrCodeNumber = qrCodeNumber;
	}

    @Override
    public String toString() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create()
                .toJson(this);
    }

}
