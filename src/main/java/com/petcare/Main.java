package com.petcare;

import com.petcare.cli.AppSession;
import com.petcare.cli.MainMenu;
import com.petcare.model.User;
import com.petcare.service.UserService;
import com.petcare.service.impl.UserServiceImpl;
import com.petcare.util.ConsoleUtil;
import com.petcare.util.DBConnection;
import com.petcare.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Main entry point for the Pet Care CLI application.
 */
public class Main {
    
    public static void main(String[] args) {
        // Initialize database schema
        initializeDatabase();
        
        // Welcome message
        ConsoleUtil.displayTitle("PET CARE SERVICE & E-COMMERCE SYSTEM");
        System.out.println("A complete solution for pet care services and product management.");
        ConsoleUtil.pressEnterToContinue("");
        
        // Start main menu
        MainMenu mainMenu = new MainMenu();
        mainMenu.display();
        
        // Exit message
        System.out.println("\nThank you for using Pet Care Service & E-Commerce System. Goodbye!");
    }
    
    /**
     * Initialize database schema and seed initial data.
     */
    private static void initializeDatabase() {
        System.out.println("Initializing database...");
        
        // Initialize schema
        DBConnection.initializeSchema();
        
        // Seed admin user if not exists
        seedAdminUser();
        
        // Seed sample data
        seedSampleData();
    }
    
    /**
     * Create an admin user if one does not already exist.
     */
    private static void seedAdminUser() {
        UserService userService = new UserServiceImpl();
        
        // Check if admin user exists
        if (userService.login("admin@petcare.com", "admin123").isEmpty()) {
            // Create admin user
            User adminUser = userService.register("Administrator", "admin@petcare.com", "admin123", "ADMIN");
            
            if (adminUser != null) {
                System.out.println("Admin user created successfully.");
            }
        }
    }
    
    /**
     * Seed sample data for demonstration purposes.
     */
    private static void seedSampleData() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Load SQL from data.sql file
            String sql = loadSqlFromFile("data.sql");
            
            // Split and execute each SQL statement
            if (sql != null && !sql.trim().isEmpty()) {
                for (String statement : sql.split(";")) {
                    if (!statement.trim().isEmpty()) {
                        stmt.execute(statement);
                    }
                }
                System.out.println("Sample data loaded successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding sample data: " + e.getMessage());
        } finally {
            DBConnection.close(stmt, conn);
        }
    }
    
    /**
     * Load SQL statements from a file in the resources directory.
     * 
     * @param filename The name of the SQL file
     * @return The SQL statements as a string
     */
    private static String loadSqlFromFile(String filename) {
        StringBuilder sql = new StringBuilder();
        
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                System.err.println("Could not find " + filename);
                return null;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip comments
                    if (!line.trim().startsWith("--")) {
                        sql.append(line).append("\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading SQL file: " + e.getMessage());
            return null;
        }
        
        return sql.toString();
    }
}