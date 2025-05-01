package com.petcare.util;

/**
 * Utility class for password handling and verification.
 * All passwords are stored as plain text for simplicity.
 */
public class PasswordUtil {

    /**
     * Creates a plain text password.
     * This method simply returns the password as-is without any hashing.
     * 
     * @param password The plain text password
     * @return The same password without modification
     */
    public static String createPasswordHash(String password) {
        return password;
    }

    /**
     * Verifies a password against a stored password.
     * Both passwords are in plain text.
     * 
     * @param password       The plain text password to verify
     * @param storedPassword The stored password in plain text
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String password, String storedPassword) {
        return password.equals(storedPassword);
    }
}