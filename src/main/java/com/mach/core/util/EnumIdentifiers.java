package com.mach.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum EnumIdentifiers {

    RANDOM_TRANSACTION("random-transaction"),
    REGISTERED_TRANSACTION("registered-transaction"),
    RANDOM_USER("random-user"),
    REGISTERED_USER("registered-user"),
    RANDOM_VALIDATED_USER("random-validated-user");

    private String identifier;

    EnumIdentifiers(String identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return identifier;
    }

    public static Collection<String> getValues() {
        return Arrays.stream(EnumIdentifiers.values()).map(EnumIdentifiers::getValue).collect(Collectors.toList());
    }
}
