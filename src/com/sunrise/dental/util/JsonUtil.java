package com.sunrise.dental.util;

/**
 * JsonUtil – simple JSON string builder utility (since no external libs like Jackson/Gson are used).
 * Helps format JSON responses for AJAX calls.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class JsonUtil {

    private JsonUtil() {}

    /**
     * Creates a simple JSON success response.
     */
    public static String createSuccessResponse(String message) {
        return "{\"status\":\"success\", \"message\":\"" + escape(message) + "\"}";
    }

    /**
     * Creates a simple JSON error response.
     */
    public static String createErrorResponse(String message) {
        return "{\"status\":\"error\", \"message\":\"" + escape(message) + "\"}";
    }

    /**
     * Escapes basic JSON characters to prevent invalid format.
     */
    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
