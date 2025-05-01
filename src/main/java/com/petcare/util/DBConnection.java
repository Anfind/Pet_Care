package com.petcare.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class for database connection management.
 */
public class DBConnection {
    private static final String CONFIG_FILE = "application.properties";
    private static String dbType;
    private static String dbDriver;
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static boolean initialized = false;

    /**
     * Initializes database configuration from properties file.
     * This is called automatically when getting a connection.
     */
    private static synchronized void initialize() {
        if (initialized) {
            return;
        }

        Properties props = new Properties();
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new IOException("Could not find " + CONFIG_FILE);
            }
            props.load(is);

            dbType = props.getProperty("db.type", "h2");
            
            if ("h2".equalsIgnoreCase(dbType)) {
                dbDriver = props.getProperty("h2.driver");
                dbUrl = props.getProperty("h2.url");
                dbUsername = props.getProperty("h2.username");
                dbPassword = props.getProperty("h2.password");
            } else if ("mysql".equalsIgnoreCase(dbType)) {
                dbDriver = props.getProperty("mysql.driver");
                dbUrl = props.getProperty("mysql.url");
                dbUsername = props.getProperty("mysql.username");
                dbPassword = props.getProperty("mysql.password");
            } else {
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
            }

            // Load the database driver
            Class.forName(dbDriver);
            initialized = true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error initializing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection to the database.
     * 
     * @return A database connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    /**
     * Initializes database schema if it doesn't exist.
     */
    public static void initializeSchema() {
        if (!initialized) {
            initialize();
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "name VARCHAR(100) NOT NULL," +
                         "email VARCHAR(100) NOT NULL UNIQUE," +
                         "password VARCHAR(255) NOT NULL," +
                         "role VARCHAR(20) NOT NULL" +
                         ")");
            
            // Products table
            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "name VARCHAR(100) NOT NULL," +
                         "description TEXT," +
                         "price DECIMAL(10,2) NOT NULL," +
                         "type VARCHAR(20) NOT NULL" +
                         ")");
            
            // Services table
            stmt.execute("CREATE TABLE IF NOT EXISTS services (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "name VARCHAR(100) NOT NULL," +
                         "description TEXT," +
                         "price DECIMAL(10,2) NOT NULL," +
                         "duration_minutes INT NOT NULL" +
                         ")");
            
            // Orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "user_id BIGINT NOT NULL," +
                         "created_at TIMESTAMP NOT NULL," +
                         "total_price DECIMAL(10,2) NOT NULL," +
                         "status VARCHAR(20) NOT NULL," +
                         "FOREIGN KEY (user_id) REFERENCES users(id)" +
                         ")");
            
            // OrderItems table
            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "order_id BIGINT NOT NULL," +
                         "product_id BIGINT NOT NULL," +
                         "quantity INT NOT NULL," +
                         "subtotal DECIMAL(10,2) NOT NULL," +
                         "FOREIGN KEY (order_id) REFERENCES orders(id)," +
                         "FOREIGN KEY (product_id) REFERENCES products(id)" +
                         ")");
            
            // Bookings table
            stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "user_id BIGINT NOT NULL," +
                         "service_id BIGINT NOT NULL," +
                         "scheduled_time TIMESTAMP NOT NULL," +
                         "status VARCHAR(20) NOT NULL," +
                         "FOREIGN KEY (user_id) REFERENCES users(id)," +
                         "FOREIGN KEY (service_id) REFERENCES services(id)" +
                         ")");
            
            // Reviews table
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "user_id BIGINT NOT NULL," +
                         "service_id BIGINT NOT NULL," +
                         "rating INT NOT NULL," +
                         "comment TEXT," +
                         "created_at TIMESTAMP NOT NULL," +
                         "FOREIGN KEY (user_id) REFERENCES users(id)," +
                         "FOREIGN KEY (service_id) REFERENCES services(id)" +
                         ")");
            
            // BlogPosts table
            stmt.execute("CREATE TABLE IF NOT EXISTS blog_posts (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "title VARCHAR(200) NOT NULL," +
                         "content TEXT NOT NULL," +
                         "created_at TIMESTAMP NOT NULL" +
                         ")");
            
            // Messages table
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "user_id BIGINT NOT NULL," +
                         "admin_id BIGINT NOT NULL," +
                         "message_text TEXT NOT NULL," +
                         "timestamp TIMESTAMP NOT NULL," +
                         "FOREIGN KEY (user_id) REFERENCES users(id)," +
                         "FOREIGN KEY (admin_id) REFERENCES users(id)" +
                         ")");
            
            // FAQs table
            stmt.execute("CREATE TABLE IF NOT EXISTS faqs (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "question VARCHAR(255) NOT NULL," +
                         "answer TEXT NOT NULL" +
                         ")");
            
            // Contact messages table
            stmt.execute("CREATE TABLE IF NOT EXISTS contact_messages (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                         "user_id BIGINT," +
                         "subject VARCHAR(200) NOT NULL," +
                         "message TEXT NOT NULL," +
                         "timestamp TIMESTAMP NOT NULL," +
                         "FOREIGN KEY (user_id) REFERENCES users(id)" +
                         ")");
            
            System.out.println("Database schema initialized successfully.");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Closes resources safely.
     * 
     * @param resources AutoCloseable resources to close
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}