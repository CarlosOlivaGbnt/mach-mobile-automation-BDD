package com.mach.core.util;

import java.security.SecureRandom;

public class RandomValues {

    private static final SecureRandom random = new SecureRandom();

    private RandomValues (){
    }

    /**
     * return a random positive number from zero to the number
     * @param to
     * @return
     */
    public static int getValue(int to){
        return getValue(0,to);
    }

    /**
     * return a random positive number from number to number including both numbers
     * @param from min value
     * @param to max value
     * @return
     */
    public static int getValue(int from, int to){
        return random.nextInt(to-from+1)+from;
    }
}
