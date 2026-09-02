package com.sunrise.dental.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * DateUtil – utility for date parsing and formatting.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class DateUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";

    private DateUtil() {}

    /**
     * Parses a string (yyyy-MM-dd) into a java.sql.Date.
     */
    public static Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Formats a sql Date for display.
     */
    public static String formatForDisplay(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        return sdf.format(date);
    }
}
