package com.sunrise.dental.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordUtil – SHA-256 hashing utility.
 * Matches MySQL's SHA2('text', 256) output exactly.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class PasswordUtil {

    private PasswordUtil() {} // Utility class – no instances needed

    /**
     * Hashes a plain-text password using SHA-256 and returns a 64-char hex string.
     * This is compatible with MySQL's SHA2(password, 256) function.
     *
     * @param plainPassword the plain-text password
     * @return lowercase 64-character hex string
     * @throws RuntimeException if SHA-256 algorithm is unavailable
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(
                plainPassword.getBytes(StandardCharsets.UTF_8)
            );
            // Convert byte array to 64-character lowercase hex
            return String.format("%064x", new BigInteger(1, hashBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored SHA-256 hash.
     *
     * @param plainPassword  the entered password
     * @param storedHash     the stored SHA-256 hash from the database
     * @return true if the password matches the stored hash
     */
    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        return hash(plainPassword).equalsIgnoreCase(storedHash);
    }
}
