package com.sunrise.dental;

import com.sunrise.dental.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidationUtilTest – JUnit tests for input validation logic.
 * Covers Valid, Invalid, and Boundary test data.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class ValidationUtilTest {

    @Test
    public void testValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("patient@sunrisedental.lk"));
        assertTrue(ValidationUtil.isValidEmail("test.name123@domain.com"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(ValidationUtil.isValidEmail("patient@"));
        assertFalse(ValidationUtil.isValidEmail("patient.com"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    public void testValidContact() {
        assertTrue(ValidationUtil.isValidContact("0771234567")); // 10 digits
        assertTrue(ValidationUtil.isValidContact("077-123-4567")); // With hyphens
        assertTrue(ValidationUtil.isValidContact("077 123 4567")); // With spaces
    }

    @Test
    public void testInvalidContact() {
        assertFalse(ValidationUtil.isValidContact("077123456")); // 9 digits (Boundary)
        assertFalse(ValidationUtil.isValidContact("07712345678")); // 11 digits (Boundary)
        assertFalse(ValidationUtil.isValidContact("077abcdefg")); // Letters
        assertFalse(ValidationUtil.isValidContact(""));
    }

    @Test
    public void testPasswordStrength() {
        assertTrue(ValidationUtil.isStrongPassword("admin123")); // 8 chars (Boundary)
        assertTrue(ValidationUtil.isStrongPassword("SecurePass2026"));
        assertFalse(ValidationUtil.isStrongPassword("pass123")); // 7 chars (Boundary)
        assertFalse(ValidationUtil.isStrongPassword(null));
    }
}
