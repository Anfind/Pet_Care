package com.petcare.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class to create an admin user directly in the database.
 */
public class CreateAdminUtil {

    public static void main(String[] args) {
        System.out.println("Creating admin user with email: admin@petcare.com and password: admin123");

        // Generate hashed password
        String hashedPassword = PasswordUtil.createPasswordHash("admin123");

        // Connect to database and insert user
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();

            // Delete existing admin if exists
            String deleteSql = "DELETE FROM users WHERE email = 'admin@petcare.com'";
            stmt = conn.prepareStatement(deleteSql);
            stmt.executeUpdate();
            DBConnection.close(stmt, null);

            // Insert new admin
            String insertSql = "INSERT INTO users (name, email, password, role) VALUES ('Administrator', 'admin@petcare.com', ?, 'ADMIN')";
            stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, hashedPassword);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Admin user created successfully.");
                System.out.println("Email: admin@petcare.com");
                System.out.println("Password: admin123");
            } else {
                System.out.println("Failed to create admin user.");
            }

        } catch (SQLException e) {
            System.err.println("Error creating admin user: " + e.getMessage());
        } finally {
            DBConnection.close(stmt, conn);
        }
    }
}