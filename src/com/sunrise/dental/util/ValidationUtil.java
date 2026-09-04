package com.sunrise.dental.util;

import java.util.regex.Pattern;


public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^[0-9]{10}$"); // Simple 10-digit check for demo

    private ValidationUtil() {}

    /**
     * Validates if a string is null or empty.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates an email address format.
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates a contact number (10 digits).
     */
    public static boolean isValidContact(String contact) {
        if (isNullOrEmpty(contact)) return false;
        // Strip hyphens and spaces for basic check
        String clean = contact.replaceAll("[\\s-]", "");
        return CONTACT_PATTERN.matcher(clean).matches();
    }
    
    /**
     * Basic password strength check.
     */
    public static boolean isStrongPassword(String password) {
        if (isNullOrEmpty(password)) return false;
        return password.length() >= 8;
    }
}
