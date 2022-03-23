package com.mach.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum EnumTransactionTitles {

    SENT_CHARGE("Enviaste un cobro por"),
    SENT_PAY("Pagaste"),
    RECEIVED_CHARGE("Recibiste un cobro por"),
    RECEIVED_PAY("Recibiste un pago por");

    private String title;

    EnumTransactionTitles(String state) {
        this.title = state;
    }

    public String getTitle() {
        return title;
    }

    public static Collection<String> getTitles() {
        return Arrays.stream(EnumTransactionTitles.values()).map(EnumTransactionTitles::getTitle).collect(Collectors.toList());
    }

}
