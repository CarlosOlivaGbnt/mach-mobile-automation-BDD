package com.mach.core.util;


public enum EnumPattern {

    CREDITCARD("[0-9]{4}[\\s][0-9]{4}[\\s][0-9]{4}[\\s][0-9]{4}"),
    CREDITCARDHIDDEN("[*]{4}[\\s][*]{4}[\\s][*]{4}[\\s][0-9]{4}"),
    PHONENUMBERASTERISKS("\\+56 9 [0-9*]+"),
    PHONENUMBER("\\+56 9 [0-9]{4} [0-9]{4}"),
    EXPIRATIONDATE("^(0[1-9]{1}|1[0-2]{1})\\/(20[1-2][0-9])$"),
    SECURITYCODE("[0-9]{3}"),
    SECURITYCODEHIDDEN("[*]{3}"),
    SECURITYCODECASHOUTATM("[0-9]{5}"),
    SECURITYCODEHIDDENCASHOUTATM("[*]{5}"),
    SECURITYCODEHIDDENCASHOUTATMIOS("[·]{5}"),
    WITHDRAWALCODE("[0-9]{4}[\\s][0-9]{4}[\\s][0-9]{4}"),
    NAME("^[A-Z\\s]*$"),
    LETTERS("^[a-zA-Z ]*$"),
    RUT("[0-9]{1,2}\\.[0-9]{3}\\.[0-9]{3}-[kK0-9]{1}"),
    RUTNOSPECIALCHARACTERS("^([0-9]{1,2})([0-9]{3}[0-9]{3})([a-zA-Z]{1}$|[0-9]{1}$)"),
    NEWID("[0-9]{9}"), //TODO delete eventually
    OLDID("^[aA]{1}[0-9]{9}"),//TODO delete eventually
    DOCUMENTNUMBER("^[aA]{0,1}[0-9]{9}"),
    LENGTHCOMMENT("^.{0,150}$"),
    MMSS("([0-5]{1}[0-9]{1}):([0-5]{1}[0-9]{1})"),
    HHMMSS("([0-2]{1}[0-9]{1}):([0-5]{1}[0-9]{1}):([0-5]{1}[0-9]{1})"),
    CL("es-CL"),
    NUMBERS("\\d+"),
    NONUMBER("[\\D+]"),
    NONUMBERSALLOWED("^([^0-9]*)$"),
    NOLETTERSALLOWED("^([^aA-zZ]*)$"),
    NONUMBEREXCEPTK("[^0-9 | kK]"),
    NUMBERS_OR_ASTERISKS("[0-9]|[*]"),
    CURRENCYCL("(?=.*\\d)^\\$?[0-9][0-9.]*[0-9]\\,?[0-9]{0,2}$"),
    CURRENCYNEGATIVECL("(?=.*\\d)^\\-\\$?[0-9][0-9.]*[0-9]\\,?[0-9]{0,2}$"),
    EXCLAMATIONSMARK("[!¡]"),
    EMAIL("([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$"),
    PHONE("^([^aA-zZ]{8})$"),
    NEGATIVE_AMOUNT_REGEX(Constants.AMOUNT_REGEX),
    POSITIVE_AMOUNT_REGEX(Constants.AMOUNT_REGEX),
	INVALID_AMOUNT_FORMAT_MESSAGE(": Amount format is invalid"),
    ANY_AMOUNT_REGEX(Constants.AMOUNT_REGEX),
    CODE("^([^aA-zZ]{4})$");

    private String pattern;

    EnumPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    private static class Constants {
        public static final String AMOUNT_REGEX = "(-\\$|\\$)\\d{1,3}(\\.\\d{3}|$)";
    }
}