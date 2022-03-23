package com.mach.core.util;

/**
 * @author carmelo.buelvas
 */
public enum EnumTime {

    DMHM("d MMM HH:mm"),
    HM("HH:mm"),
    YMDHMS("yyyy-MM-dd'T'HH:mm:ss.SSS"),
    EDMHMS("EEE, d MMM yyyy HH:mm:ss Z"),
    YMDHMSS("yyyyMMddHHmmss"),
    DDMMMMYYYY("dd 'de' MMMM 'de' yyyy");

    private String format;

    EnumTime(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
