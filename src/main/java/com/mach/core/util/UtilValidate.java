package com.mach.core.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.MaskFormatter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class UtilValidate {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilValidate.class);


    private UtilValidate() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<Integer> getIntegerOptional(String amount) {
        try {
            return Optional.ofNullable(amount.replaceAll(EnumPattern.NONUMBER.getPattern(), ""))
                    .map(Integer::valueOf);
        } catch (NumberFormatException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    public static String currencyFormat(String amount) {
        if (amount == null) {
            return null;
        }
        return new StringBuilder()
                .append("$")
                .append(new DecimalFormat("###,###.##")
                        .format(Double.parseDouble(amount.trim()))
                ).toString().replace(",", ".");
    }

    public static String lucasFormat(String amount) {
        String lucas = currencyFormat(amount);
        if (lucas == null) {
            return null;
        }
        int dotIndex = lucas.indexOf('.');
        return dotIndex < 0 ? lucas : lucas.substring(0, dotIndex).replace(",", ".").concat(lucas.substring(dotIndex));
    }

    public static String phoneFormat(String codeZone, String phone) {
        return new StringBuilder()
                .append("+56")
                .append(codeZone)
                .append(" ")
                .append(phone.subSequence(0, 4))
                .append(" ")
                .append(phone.subSequence(4, 8))
                .toString();
    }

    public static String currencyFormat(String amount, String languagueTag) {
        DecimalFormat numberInstance = (DecimalFormat) NumberFormat.getNumberInstance(Locale.forLanguageTag(languagueTag));
        numberInstance.applyPattern("###,###.##");

        return new StringBuilder()
                .append("$")
                .append(numberInstance.format(Double.parseDouble(amount.trim()))
                ).toString();
    }

    public static String rutFormat(String inputString) {
        String capsString = inputString.toUpperCase();
        String reverseString = new StringBuilder(capsString).reverse().toString();
        String mask = "A-AAA.AAA.AAA";
        try {
            MaskFormatter maskFormatter = new MaskFormatter(mask);
            maskFormatter.setValueContainsLiteralCharacters(false);
            String formattedReversed = maskFormatter.valueToString(reverseString);
            return new StringBuilder(formattedReversed).reverse().toString().trim();
        } catch (ParseException e) {
            return "Could not format the RUT";
        }
    }

    public static String getLastTwoCharacter(String txt) {
        return txt == null ? null : txt.substring(txt.length() - 2);
    }

    public static String extractCreditCardNumbers(String credictCardNumber) {
        return credictCardNumber.substring(12);
    }

    public static String extractPin(List<String> strings) {
        return String.join("", strings).substring(0, 3);
    }

    public static String formatOnlyFirstLetterUpper(String state) {
        return state.substring(0, 1).toUpperCase() + state.substring(1).toLowerCase();
    }

    public static String formatDateAndroid(String originalDate) {
        String[] dateSplit = originalDate.split(" ");
        dateSplit[2] = formatOnlyFirstLetterUpper(dateSplit[2]);
        String date = Arrays.stream(dateSplit).collect(Collectors.joining(" "));
        return new StringBuilder(date.startsWith("0") ? date.replaceFirst("0", "") : date).toString();
    }
}
