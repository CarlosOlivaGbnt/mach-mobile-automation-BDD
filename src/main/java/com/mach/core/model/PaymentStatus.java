package com.mach.core.model;

public enum PaymentStatus {
    ERROR("Error"), SUCCESS("Pagado"), PROCESSING("");
    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
