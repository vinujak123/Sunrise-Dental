package com.sunrise.dental.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHash_ValidPassword() {
        String hash = PasswordUtil.hash("admin123");
        assertNotNull(hash);
        assertEquals(64, hash.length());
        // SHA256 of 'admin123' is 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
        assertEquals("240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", hash);
    }

    @Test
    public void testHash_NullPassword_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hash(null);
        });
    }

    @Test
    public void testVerify_ValidPassword() {
        String hash = PasswordUtil.hash("secure123");
        assertTrue(PasswordUtil.verify("secure123", hash));
    }

    @Test
    public void testVerify_InvalidPassword() {
        String hash = PasswordUtil.hash("secure123");
        assertFalse(PasswordUtil.verify("wrong123", hash));
    }
}
