package com.mach.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum EnumTypeTransaction {

    MACHCARD("Tarjeta MACH"),
    MACHPHYSICALCARD("Tarjeta física MACH"),
    MACHCARDREFUND("Tarjeta MACH (reembolso)"),
    MACHPHYSICALCARDREFUND("Tarjeta física MACH (reembolso)"),
    MACHCARDCANCELED("Tarjeta MACH (Cargo anulado)"),
    MACHPHYSICALCARDCANCELED("Tarjeta física MACH (Cargo anulado)"),
    PAYMENTRECEIVED("Pago de"),
    PAYMENTMADE("Pago a"),
    CASHOUTTEF("Retiro"),
    CASHOUTATM("Cajero Bci"),
    CASHOUTATMOPTIONAL("Cajero Bci (reversa)"),
    ADJUSTMENTMACH("Ajuste MACH"),
    CASHIN("Recarga");

    private String type;

    EnumTypeTransaction(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static Collection<String> getTypes() {
        return Arrays.stream(EnumTypeTransaction.values()).map(EnumTypeTransaction::getType).collect(Collectors.toList());
    }
}
