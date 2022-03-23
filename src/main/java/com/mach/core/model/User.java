package com.mach.core.model;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import data.TestData;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
public class User {

    @Id
    @SerializedName("_id")
    private ObjectId id;

    private String accountRUT;
    private String accountSerialNumber;
    private String name;
    private String lastName;
    private String zoneCode;
    private String cellNumber;
    private String email;
    private String unconfirmedEmail;
    private String machId;
    private String pin;
    private String passwordCard;

    private List<String> connectedAccounts;

    public String getFullName() {
        if (getLastName() == null) {
            return name;
        }
        return new StringBuffer()
                .append(name)
                .append(" ")
                .append(lastName)
                .toString().trim();
    }

    public String getPhoneNumber() {
        return new StringBuffer()
                .append(zoneCode)
                .append(cellNumber)
                .toString();
    }

    public String getAccountRUT() {
        return accountRUT;
    }

    public void setAccountRUT(String accountRUT) {
        this.accountRUT = accountRUT;
    }

    public String getAccountSerialNumber() {
        return accountSerialNumber;
    }

    public void setAccountSerialNumber(String accountSerialNumber) {
        this.accountSerialNumber = accountSerialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
    	if (pin == null || pin.isEmpty()) {
    		pin = TestData.getPINAsString(TestData.getValidPIN());
    	}
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPasswordCard() {
        return passwordCard;
    }

    public void setPasswordCard(String passwordCard) {
        this.passwordCard = passwordCard;
    }

    public String getInitials() {
        return name.substring(0, 1) + lastName.substring(0, 1);
    }

    @Override
    public String toString() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create()
                .toJson(this);
    }

    public String getMachId() {
        return machId;
    }

    public void setMachId(String machId) {
        this.machId = machId;
    }
    
    public String getId() {
        return id != null? id.toHexString() : null;
    }

    public void setId(String id) {
        this.id = new ObjectId(id);
    }

    public List<String> getConnectedAccounts() {
        return connectedAccounts;
    }

    public void setConnectedAccounts(List<String> connectedAccounts) {
        this.connectedAccounts = connectedAccounts;
    }

    public String getUnconfirmedEmail() {
        return unconfirmedEmail;
    }

    public void setUnconfirmedEmail(String unconfirmedEmail) {
        this.unconfirmedEmail = unconfirmedEmail;
    }
}
