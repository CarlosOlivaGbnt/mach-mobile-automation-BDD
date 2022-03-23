package com.mach.core.model;

import com.google.gson.GsonBuilder;

import java.util.Map;

public class Bill {
    private String description;
    private String provider;
    private String debt;
    private String expireDate;
    private String[] resultDescription;
    private String identifier;
    private String serviceType;
    private PaymentStatus status;
    private String alias;
    private boolean saveBill;
    private boolean isFirstBill;

    public Bill(Map<String, String> billData) {
        this.serviceType = billData.get("tipoServicio");
        this.provider = billData.get("proveedor");
        this.identifier = billData.get("identificador");
        this.debt = billData.get("saldoEsperado");
        this.expireDate = billData.get("fechaVencimiento");
    }

    public Bill() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String[] getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String[] resultDescription) {
        this.resultDescription = resultDescription;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getAlias() { return alias; }

    public void setAlias(String alias) { this.alias = alias; }

    public boolean isSaveBill() {
        return saveBill;
    }

    public void setSaveBill(boolean saveBill) {
        this.saveBill = saveBill;
    }

    public boolean isFirstBill() {
        return isFirstBill;
    }

    public void setFirstBill(boolean firstBill) {
        isFirstBill = firstBill;
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
