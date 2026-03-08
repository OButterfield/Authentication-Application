package com.authentication.authentication_application.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for hashing strings using bcrypt with salt and pepper.
 * Provides methods to hash and verify hashed values.
 * Pepper is a server-side secret added to all inputs before hashing.
 */
public class HashUtil {

    private final BCryptPasswordEncoder encoder;

    private final String pepper;

    /**
     * Constructs a HashUtil with a pepper secret.
     *
     * @param pepper the server-side secret to append to all inputs
     * @throws IllegalArgumentException if pepper is null or empty
     */
    public HashUtil(String pepper) {
        if (pepper == null || pepper.isEmpty()) {
            throw new IllegalArgumentException("Pepper cannot be null or empty");
        }
        this.pepper = pepper;
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * Hashes a plain text string using bcrypt with salt and pepper.
     * The pepper is appended to the input before hashing.
     *
     * @param plainText the string to hash
     * @return the bcrypt hashed string
     * @throws IllegalArgumentException if plainText is null
     */
    public String hash(String plainText) {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }

        // Append pepper to the plain text before hashing
        String textWithPepper = plainText + pepper;

        // BCrypt generates a new salt automatically and includes it in the hash
        return encoder.encode(textWithPepper);
    }

    /**
     * Verifies that a plain text string matches a hashed value.
     * The pepper is appended to the plainText before comparison.
     *
     * @param plainText the plain text string to verify
     * @param hash the hashed string to compare against
     * @return true if the plainText matches the hash, false otherwise
     * @throws IllegalArgumentException if plainText or hash is null
     */
    public boolean matches(String plainText, String hash) {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }

        if (hash == null) {
            throw new IllegalArgumentException("Hash cannot be null");
        }

        // Append pepper to the plain text before verification
        String textWithPepper = plainText + pepper;

        // BCrypt automatically extracts the salt from the hash and compares
        return encoder.matches(textWithPepper, hash);
    }
}

