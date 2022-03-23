package com.mach.core.util.user;

import com.mach.core.db.AccountServiceDAO;
import com.mach.core.util.RandomValues;


public class RutValues {

    private RutValues() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * This method allow generate a valid aleatory RUT
     * Ruts between 48000000 - 48999999 and 46000000 - 46999999 are restricted ruts
     * @return A RUT valid
     */
    public static String getValidRut() {
        String randomNum = String.valueOf(RandomValues.getValue(10000000, 45999999 + 1));
        String randomNumReverse = reverseString(randomNum);
        return getUniqueRUT(randomNum + getDigitVerificator(randomNumReverse));
    }

    private static String getUniqueRUT(String randomRUT) {
        AccountServiceDAO accountServiceDAO = new AccountServiceDAO();
        if (accountServiceDAO.isDocumentNumberExist(randomRUT)) {
            randomRUT = getValidRut();
        }
        return randomRUT;
    }

    private static String reverseString(String word) {
        if (word.length() == 1)
            return word;
        else
            return reverseString(word.substring(1)) + word.charAt(0);
    }

    private static String getDigitVerificator(String chain) {
        int a = 2;
        int rutSumado = 0;
        for (char aNumber : chain.toCharArray()) {
            rutSumado += (Character.getNumericValue(aNumber) * a);
            if (a == 7) {
                a = 1;
            }
            a++;
        }
        int digito = (11 - rutSumado % 11);
        if (digito == 11) return "0";
        else return (digito == 10) ? "K" : String.valueOf(digito);
    }
}
