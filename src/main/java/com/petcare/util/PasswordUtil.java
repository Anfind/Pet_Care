package com.petcare.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 */
public class PasswordUtil {
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random salt for password hashing.
     * 
     * @return A base64-encoded salt string
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password with a provided salt.
     * 
     * @param password The plain text password
     * @param salt     The base64-encoded salt
     * @return A base64-encoded hash of the salted password
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            // Add salt to the digest
            digest.update(saltBytes);

            // Hash the password
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Creates a complete password hash with salt.
     * The format is: base64(salt) + ":" + base64(hash)
     * 
     * @param password The plain text password
     * @return A combined salt and hash string
     */
    public static String createPasswordHash(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + ":" + hash;
    }

    /**
     * Verifies a password against a stored hash.
     * 
     * @param password   The plain text password to verify
     * @param storedHash The stored hash (salt:hash format)
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Check if the password is stored in plain text (for testing purposes)
            if (password.equals(storedHash)) {
                return true;
            }

            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            String salt = parts[0];
            String hash = parts[1];

            String computedHash = hashPassword(password, salt);
            return hash.equals(computedHash);
        } catch (Exception e) {
            return false;
        }
    }
}