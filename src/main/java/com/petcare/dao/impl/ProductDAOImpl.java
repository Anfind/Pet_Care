package com.petcare.dao.impl;

import com.petcare.dao.ProductDAO;
import com.petcare.model.Product;
import com.petcare.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProductDAO interface for database operations.
 */
public class ProductDAOImpl implements ProductDAO {

    @Override
    public Product save(Product product) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (product.getId() == null) {
                // Insert new product
                String sql = "INSERT INTO products (name, description, price, type) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, product.getName());
                stmt.setString(2, product.getDescription());
                stmt.setBigDecimal(3, product.getPrice());
                stmt.setString(4, product.getType());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating product failed, no rows affected.");
                }
                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    product.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            } else {
                // Update existing product
                return update(product);
            }
            
            return product;
        } catch (SQLException e) {
            System.err.println("Error saving product: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM products WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setType(rs.getString("type"));
                
                return Optional.of(product);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding product by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Product> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            // Modified query to ensure only distinct products are returned
            String sql = "SELECT DISTINCT id, name, description, price, type FROM products ORDER BY id";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setType(rs.getString("type"));
                
                products.add(product);
            }
            
            return products;
        } catch (SQLException e) {
            System.err.println("Error finding all products: " + e.getMessage());
            return products;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM products WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product by ID: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public Product update(Product product) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE products SET name = ?, description = ?, price = ?, type = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setString(4, product.getType());
            stmt.setLong(5, product.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating product failed, no rows affected.");
            }
            
            return product;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<Product> findByType(String type) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM products WHERE type = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, type);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setType(rs.getString("type"));
                
                products.add(product);
            }
            
            return products;
        } catch (SQLException e) {
            System.err.println("Error finding products by type: " + e.getMessage());
            return products;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Product> searchByNameOrDescription(String searchText) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";
            stmt = conn.prepareStatement(sql);
            
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setType(rs.getString("type"));
                
                products.add(product);
            }
            
            return products;
        } catch (SQLException e) {
            System.err.println("Error searching products by name or description: " + e.getMessage());
            return products;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}