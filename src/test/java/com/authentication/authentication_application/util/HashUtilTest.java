package com.authentication.authentication_application.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HashUtil.
 * Tests bcrypt hashing functionality with salt and pepper.
 */
@DisplayName("HashUtil Tests")
class HashUtilTest {

    private HashUtil hashUtil;
    private static final String TEST_PEPPER = "test-pepper-secret";

    @BeforeEach
    void setUp() {
        hashUtil = new HashUtil(TEST_PEPPER);
    }

    // Hash Generation Tests
    @Test
    @DisplayName("Should hash a plain text string successfully")
    void shouldHashPlainTextStringSuccessfully() {
        // GIVEN
        String plainText = "SecurePassword123!";

        // WHEN
        String hash = hashUtil.hash(plainText);

        // THEN
        assertNotNull(hash);
        assertNotEquals(plainText, hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    @DisplayName("Should generate different hashes for the same input (due to salt)")
    void shouldGenerateDifferentHashesForSameInput() {
        // GIVEN
        String plainText = "SecurePassword123!";

        // WHEN
        String hash1 = hashUtil.hash(plainText);
        String hash2 = hashUtil.hash(plainText);

        // THEN
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Should handle null input safely")
    void shouldHandleNullInputSafely() {
        // GIVEN & WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> hashUtil.hash(null));
    }

    @Test
    @DisplayName("Should handle empty string input")
    void shouldHandleEmptyStringInput() {
        // GIVEN
        String plainText = "";

        // WHEN
        String hash = hashUtil.hash(plainText);

        // THEN
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    @DisplayName("Should handle input at bcrypt 72-byte limit")
    void shouldHandleInputAtBcrypt72ByteLimit() {
        // GIVEN - 72 bytes is the bcrypt maximum for password + pepper combined
        String plainText = "a".repeat(50);

        // WHEN
        String hash = hashUtil.hash(plainText);

        // THEN
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertTrue(hashUtil.matches(plainText, hash));
    }

    @Test
    @DisplayName("Should handle special characters in input")
    void shouldHandleSpecialCharactersInInput() {
        // GIVEN
        String plainText = "P@ssw0rd!#$%^&*()_+-=[]{}|;:',.<>?/~`";

        // WHEN
        String hash = hashUtil.hash(plainText);

        // THEN
        assertNotNull(hash);
        assertNotEquals(plainText, hash);
    }

    @Test
    @DisplayName("Should generate valid bcrypt hash")
    void shouldGenerateValidBcryptHash() {
        // GIVEN
        String plainText = "TestPassword123";

        // WHEN
        String hash = hashUtil.hash(plainText);

        // THEN - Verify it's a valid bcrypt hash (starts with $2a$, $2b$, or $2y$)
        assertTrue(
                hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"),
                "Hash should be a valid bcrypt hash"
        );
    }

    // Hash Verification Tests
    @Test
    @DisplayName("Should verify matching password and hash")
    void shouldVerifyMatchingPasswordAndHash() {
        // GIVEN
        String plainText = "SecurePassword123!";
        String hash = hashUtil.hash(plainText);

        // WHEN
        boolean matches = hashUtil.matches(plainText, hash);

        // THEN
        assertTrue(matches);
    }

    @Test
    @DisplayName("Should reject non-matching password and hash")
    void shouldRejectNonMatchingPasswordAndHash() {
        // GIVEN
        String plainText = "SecurePassword123!";
        String differentText = "DifferentPassword456!";
        String hash = hashUtil.hash(plainText);

        // WHEN
        boolean matches = hashUtil.matches(differentText, hash);

        // THEN
        assertFalse(matches);
    }

    @Test
    @DisplayName("Should handle null plainText in matches method")
    void shouldHandleNullPlainTextInMatches() {
        // GIVEN
        String hash = hashUtil.hash("SomePassword");

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> hashUtil.matches(null, hash));
    }

    @Test
    @DisplayName("Should handle null hash in matches method")
    void shouldHandleNullHashInMatches() {
        // GIVEN & WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> hashUtil.matches("SomePassword", null));
    }

    @Test
    @DisplayName("Should return false for empty string match")
    void shouldReturnFalseForEmptyStringMatch() {
        // GIVEN
        String hash = hashUtil.hash("SomePassword");

        // WHEN
        boolean matches = hashUtil.matches("", hash);

        // THEN
        assertFalse(matches);
    }

    @Test
    @DisplayName("Should verify case-sensitive password matching")
    void shouldVerifyCaseSensitivePasswordMatching() {
        // GIVEN
        String plainText = "SecurePassword123!";
        String hash = hashUtil.hash(plainText);

        // WHEN - Try with different case
        boolean matches = hashUtil.matches("securepassword123!", hash);

        // THEN
        assertFalse(matches);
    }

    // Pepper Tests
    @Test
    @DisplayName("Should produce different hashes with different peppers")
    void shouldProduceDifferentHashesWithDifferentPeppers() {
        // GIVEN
        String plainText = "TestPassword123";
        HashUtil hashUtil1 = new HashUtil("pepper1");
        HashUtil hashUtil2 = new HashUtil("pepper2");

        // WHEN
        String hash1 = hashUtil1.hash(plainText);
        String hash2 = hashUtil2.hash(plainText);

        // THEN
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Should verify password with correct pepper")
    void shouldVerifyPasswordWithCorrectPepper() {
        // GIVEN
        String plainText = "TestPassword123";
        String correctPepper = "correct-pepper";
        HashUtil hashUtil1 = new HashUtil(correctPepper);

        // WHEN
        String hash = hashUtil1.hash(plainText);
        boolean matches = hashUtil1.matches(plainText, hash);

        // THEN
        assertTrue(matches);
    }

    @Test
    @DisplayName("Should fail to verify password with wrong pepper")
    void shouldFailToVerifyPasswordWithWrongPepper() {
        // GIVEN
        String plainText = "TestPassword123";
        HashUtil hashUtil1 = new HashUtil("pepper1");
        HashUtil hashUtil2 = new HashUtil("pepper2");
        String hash = hashUtil1.hash(plainText);

        // WHEN
        boolean matches = hashUtil2.matches(plainText, hash);

        // THEN
        assertFalse(matches);
    }

    @Test
    @DisplayName("Should handle null pepper in constructor")
    void shouldHandleNullPepperInConstructor() {
        // GIVEN & WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> new HashUtil(null));
    }

    @Test
    @DisplayName("Should handle empty pepper in constructor")
    void shouldHandleEmptyPepperInConstructor() {
        // GIVEN & WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> new HashUtil(""));
    }

    @Test
    @DisplayName("Should hash multiple different inputs successfully")
    void shouldHashMultipleDifferentInputsSuccessfully() {
        // GIVEN
        String[] inputs = {
                "Password1",
                "TestString123",
                "AnotherInput!@#",
                "123numeric",
                "special!@#$%^&*()"
        };

        // WHEN & THEN
        for (String input : inputs) {
            String hash = hashUtil.hash(input);
            assertNotNull(hash);
            assertTrue(hashUtil.matches(input, hash));
        }
    }
}

