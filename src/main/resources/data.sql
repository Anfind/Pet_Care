-- SQL Script for Pet Care Application
-- This script creates all necessary tables and populates them with sample data

-- Drop existing tables (if any) to ensure clean setup
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS contact_messages CASCADE;
DROP TABLE IF EXISTS faqs CASCADE;
DROP TABLE IF EXISTS blog_posts CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS services CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Services table
CREATE TABLE IF NOT EXISTS services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration_minutes INT NOT NULL
);

-- Create Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    stock_quantity INT DEFAULT 0
);

-- Create Blog Posts table
CREATE TABLE IF NOT EXISTS blog_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create FAQs table
CREATE TABLE IF NOT EXISTS faqs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question VARCHAR(255) NOT NULL,
    answer TEXT NOT NULL
);

-- Create Contact Messages table
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_responded BOOLEAN DEFAULT FALSE
);

-- Create Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    scheduled_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Create Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create Order Items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Create Messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);

-- Create Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Check if users table is empty before inserting data
SET @user_count = (SELECT COUNT(*) FROM users);
SET @insert_data = IF(@user_count = 0, 1, 0);

-- Insert data only if tables are empty (@insert_data = 1)
-- Insert plain text Admin User (for easier login)
INSERT INTO users (name, email, password, role)
SELECT 'Admin', 'admin', 'admin', 'ADMIN'
WHERE @insert_data = 1;

-- Insert hashed Admin User (original) with SHA-256 format
INSERT INTO users (name, email, password, role)
SELECT 'Admin', 'admin@petcare.com', 'ImV/VWwWGV0WVXoTFNtjDNMH5otBRn2OhZGJIKQ0tLI=:FXWPj/mTfykLECOVLwxRuGMWMNHiU8SqiDNL3CHqXbI=', 'ADMIN'
WHERE @insert_data = 1;

-- Insert Regular User with SHA-256 format
INSERT INTO users (name, email, password, role)
SELECT 'John Doe', 'john@example.com', 'wLDlhb/uFYQbDf73FdOUj6eVNhpGGRJxs6COSMrcgSc=:y6QaWkuRjLT/xnfR9qLw6v6lD+JK4DkgKAJFTjHnHPA=', 'CUSTOMER'
WHERE @insert_data = 1;

-- Only insert sample data if tables are empty
-- Products
INSERT INTO products (name, description, price, type, stock_quantity)
SELECT 'Premium Dog Food', 'High-quality nutrition for adult dogs', 29.99, 'FOOD', 50 WHERE @insert_data = 1;

INSERT INTO products (name, description, price, type, stock_quantity)
SELECT 'Cat Scratching Post', 'Multi-level cat tree with scratching posts', 49.99, 'TOY', 20 WHERE @insert_data = 1;

INSERT INTO products (name, description, price, type, stock_quantity)
SELECT 'Pet Shampoo', 'Gentle, tearless formula for all pets', 12.99, 'OTHER', 100 WHERE @insert_data = 1;

INSERT INTO products (name, description, price, type, stock_quantity)
SELECT 'Interactive Dog Toy', 'Treat-dispensing toy for mental stimulation', 15.99, 'TOY', 35 WHERE @insert_data = 1;

INSERT INTO products (name, description, price, type, stock_quantity)
SELECT 'Organic Cat Food', 'Natural, grain-free nutrition for adult cats', 24.99, 'FOOD', 45 WHERE @insert_data = 1;

-- Insert other products only if tables are empty

-- Services
INSERT INTO services (name, description, price, duration_minutes)
SELECT 'Basic Grooming', 'Bath, brush, and nail trim', 40.00, 60 WHERE @insert_data = 1;

INSERT INTO services (name, description, price, duration_minutes)
SELECT 'Deluxe Grooming', 'Bath, brush, nail trim, ear cleaning, and styling', 65.00, 90 WHERE @insert_data = 1;

INSERT INTO services (name, description, price, duration_minutes)
SELECT 'Pet Sitting', 'In-home care for your pet (1 visit)', 30.00, 45 WHERE @insert_data = 1;

INSERT INTO services (name, description, price, duration_minutes)
SELECT 'Dog Walking', 'Individual dog walk', 20.00, 30 WHERE @insert_data = 1;

INSERT INTO services (name, description, price, duration_minutes)
SELECT 'Veterinary Checkup', 'Basic health check and consultation', 50.00, 45 WHERE @insert_data = 1;

-- Insert other services only if tables are empty

-- FAQs
INSERT INTO faqs (question, answer)
SELECT 'How often should I groom my pet?', 'It depends on the breed and coat type. Long-haired breeds typically need grooming every 4-6 weeks, while short-haired breeds may need less frequent grooming.' WHERE @insert_data = 1;

INSERT INTO faqs (question, answer)
SELECT 'What vaccination does my pet need?', 'Core vaccinations for dogs include rabies, distemper, parvovirus, and adenovirus. For cats, core vaccinations include rabies, feline viral rhinotracheitis, calicivirus, and panleukopenia. Consult with your veterinarian for a personalized vaccination schedule.' WHERE @insert_data = 1;

-- Insert other FAQs only if tables are empty

-- Blog posts
INSERT INTO blog_posts (title, content, created_at)
SELECT 'Top 10 Tips for New Pet Owners', 'Bringing a new pet home is exciting but can also be overwhelming. Here are our top tips to help you prepare...\n\n1. Pet-proof your home\n2. Find a veterinarian before bringing your pet home\n3. Stock up on supplies\n4. Establish a routine\n5. Be patient during the adjustment period\n6. Start training early\n7. Socialize your pet\n8. Consider pet insurance\n9. Groom regularly\n10. Make time for play and exercise', CURRENT_TIMESTAMP() WHERE @insert_data = 1;

INSERT INTO blog_posts (title, content, created_at)
SELECT 'Understanding Pet Nutrition', 'Proper nutrition is essential for your pet''s health and longevity. This guide will help you understand pet food labels, nutritional requirements for different life stages, and how to choose the best diet for your furry friend...', CURRENT_TIMESTAMP() WHERE @insert_data = 1;

-- Insert other blog posts only if tables are empty

-- Sample bookings
INSERT INTO bookings (user_id, service_id, scheduled_time, status)
SELECT 2, 1, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), 'SCHEDULED' WHERE @insert_data = 1;

INSERT INTO bookings (user_id, service_id, scheduled_time, status)
SELECT 2, 3, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -5 DAY), 'COMPLETED' WHERE @insert_data = 1;

-- Insert other sample data only if tables are empty