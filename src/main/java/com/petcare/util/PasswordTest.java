package com.petcare.util;

/**
 * Test utility to verify plain text password verification.
 */
public class PasswordTest {

    public static void main(String[] args) {
        System.out.println("=== PASSWORD VERIFICATION TEST ===");

        // Test createPasswordHash - should return plain text now
        String password = "admin123";
        String storedPassword = PasswordUtil.createPasswordHash(password);
        System.out.println("Original password: " + password);
        System.out.println("Stored password: " + storedPassword);
        System.out.println("Are they identical? " + password.equals(storedPassword));

        // Test verifyPassword with plain text passwords
        boolean adminVerification = PasswordUtil.verifyPassword("admin", "admin");
        System.out.println("\nPassword 'admin' verification: " + adminVerification);

        boolean admin123Verification = PasswordUtil.verifyPassword("admin123", "admin123");
        System.out.println("Password 'admin123' verification: " + admin123Verification);

        boolean failedVerification = PasswordUtil.verifyPassword("admin123", "wrongpassword");
        System.out.println("Password with wrong value verification: " + failedVerification);

        // Test with user accounts from data.sql
        System.out.println("\n=== TESTING USER ACCOUNTS ===");
        System.out.println("admin/admin: " + PasswordUtil.verifyPassword("admin", "admin"));
        System.out.println("admin@petcare.com/admin123: " + PasswordUtil.verifyPassword("admin123", "admin123"));
        System.out.println("john@example.com/123456: " + PasswordUtil.verifyPassword("123456", "123456"));
    }
}