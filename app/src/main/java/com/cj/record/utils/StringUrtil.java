package com.cj.record.utils;

/**
 * yyyy-M-d
 */
public class StringUrtil {
    private static String[] format(String date) {
        return date.split("-");
    }

    public static String getYear(String date) {
        return format(date)[0];
    }
    public static String getMonth(String date) {
        return format(date)[1];
    }
    public static String getDay(String date) {
        return format(date)[2];
    }
}
