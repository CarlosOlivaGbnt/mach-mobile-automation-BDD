package com.mach.core.model;

import com.google.gson.Gson;

public class TransactionNotification {

    private boolean isIconDisplayed;
    private int notificationCount;
    private Transaction transaction;

    public TransactionNotification() {
        // Default constructor
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public boolean isIconDisplayed() {
        return isIconDisplayed;
    }

    public void setIconDisplayed(boolean iconDisplayed) {
        isIconDisplayed = iconDisplayed;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
