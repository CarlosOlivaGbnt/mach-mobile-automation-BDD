package com.mach.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum EnumStateOfTransaction {

    PENDING("PENDIENTE"),
    DISCARDED("DESCARTADO"),
    DISCARD("DESCARTAR"),
    REJECTED("RECHAZADO"),
    REJECT("RECHAZAR"),
    PAID("PAGADO"),
    PAY("PAGAR"),
    REMEMBER("RECORDAR");

    private String state;

    EnumStateOfTransaction(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public static Collection<String> getStates() {
        return Arrays.stream(EnumStateOfTransaction.values()).map(EnumStateOfTransaction::getState).collect(Collectors.toList());
    }

    public static EnumStateOfTransaction findState(final String stateValue) {
        for (EnumStateOfTransaction value : values()) {
            if (value.state.equals(stateValue)) {
                return value;
            }
        }
        return null;
    }
}