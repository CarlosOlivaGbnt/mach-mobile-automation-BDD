package com.mach.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class UtilFormat {

    private static final Logger logger = LoggerFactory.getLogger(UtilFormat.class);

    private UtilFormat() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceFirst("(\\d{4})(\\d{4})", "+569 $1 $2");
    }

    public static Boolean validateFormat(EnumPattern pattern, String value) {
        if(value.isEmpty()) {
            logger.error("Attempted to validate format of an empty string");
            return false;
        }
        return Pattern.compile(pattern.getPattern()).matcher(value).matches();
    }

    public static String getRutFormat(String rut) {
        String rutFormat = null;
        if (String.valueOf(rut).length() == 9) {
            rutFormat = new StringBuilder()
                    .append(String.valueOf(rut).substring(0, 2))
                    .append(".")
                    .append(String.valueOf(rut).substring(2, 5))
                    .append(".")
                    .append(String.valueOf(rut).substring(5, 8))
                    .append("-")
                    .append(String.valueOf(rut).substring(8, 9))
                    .toString();
        } else if (String.valueOf(rut).length() == 8) {
            rutFormat = new StringBuilder()
                    .append(String.valueOf(rut).substring(0, 1))
                    .append(".")
                    .append(String.valueOf(rut).substring(1, 4))
                    .append(".")
                    .append(String.valueOf(rut).substring(4, 7))
                    .append("-")
                    .append(String.valueOf(rut).substring(7, 8))
                    .toString();
        }
        return rutFormat;
    }

    public static String getAccountNumberFromRut(String rut) {
        return Optional.ofNullable(rut)
                .filter(accountNumber -> accountNumber.length() != 0)
                .map(accountNumber -> accountNumber.substring(0, accountNumber.length() - 1))
                .orElse(rut);
    }

    /**
     * Normalizes the given string, by deleting \n, \t and extra spaces.
     *
     * @param text - initial string
     * @return - normalized string
     */
    public static String normalizeText(String text) {
        text = text.trim();
        text = text.replace("\n", " ");
        text = text.replace("\t", " ");
        while (text.contains("  ")) {
            text = text.replace("  ", " ");
        }
        return text;
    }

    public static List<String> altPhones(List<String> prefixList, String phoneNumberWithoutPrefix) {
        List<String> resultList = new ArrayList<>();
        prefixList.forEach(p -> resultList.add(p + phoneNumberWithoutPrefix));
        return resultList;
    }

    /**
     * Pad a string with zeros to left.
     *
     * @param inputString - input string
     * @param length      - string length
     * @return - string padded with zeros to left
     */
    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
        return sb.toString();
    }

    public static String formatDate(String dateAsString) {
        String result = null;
        SimpleDateFormat formatterFrom = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat formatterTo = new SimpleDateFormat("dd/mm/yyyy");
        try {
            result = formatterTo.format(formatterFrom.parse(dateAsString));
        } catch (ParseException e) {
            logger.error("ParseException was thrown: ", e);
        }
        return result;
    }

}
