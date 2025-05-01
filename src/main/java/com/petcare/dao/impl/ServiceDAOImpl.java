package com.petcare.dao.impl;

import com.petcare.dao.ServiceDAO;
import com.petcare.model.Service;
import com.petcare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ServiceDAO interface for database operations.
 */
public class ServiceDAOImpl implements ServiceDAO {

    @Override
    public Service save(Service service) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (service.getId() == null) {
                // Insert new service
                String sql = "INSERT INTO services (name, description, price, duration_minutes) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, service.getName());
                stmt.setString(2, service.getDescription());
                stmt.setBigDecimal(3, service.getPrice());
                stmt.setInt(4, service.getDurationMinutes());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating service failed, no rows affected.");
                }
                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    service.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating service failed, no ID obtained.");
                }
            } else {
                // Update existing service
                return update(service);
            }
            
            return service;
        } catch (SQLException e) {
            System.err.println("Error saving service: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Service> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM services WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Service service = new Service();
                service.setId(rs.getLong("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("price"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                
                return Optional.of(service);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding service by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Service> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Service> services = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            // Modified query to ensure only distinct services are returned
            String sql = "SELECT DISTINCT id, name, description, price, duration_minutes FROM services ORDER BY id";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getLong("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("price"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                
                services.add(service);
            }
            
            return services;
        } catch (SQLException e) {
            System.err.println("Error finding all services: " + e.getMessage());
            return services;
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
            String sql = "DELETE FROM services WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting service by ID: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public Service update(Service service) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE services SET name = ?, description = ?, price = ?, duration_minutes = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setBigDecimal(3, service.getPrice());
            stmt.setInt(4, service.getDurationMinutes());
            stmt.setLong(5, service.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating service failed, no rows affected.");
            }
            
            return service;
        } catch (SQLException e) {
            System.err.println("Error updating service: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<Service> findByMaxDuration(int maxDuration) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Service> services = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM services WHERE duration_minutes <= ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maxDuration);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getLong("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("price"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                
                services.add(service);
            }
            
            return services;
        } catch (SQLException e) {
            System.err.println("Error finding services by max duration: " + e.getMessage());
            return services;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Service> searchByNameOrDescription(String searchText) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Service> services = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM services WHERE name LIKE ? OR description LIKE ?";
            stmt = conn.prepareStatement(sql);
            
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getLong("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("price"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                
                services.add(service);
            }
            
            return services;
        } catch (SQLException e) {
            System.err.println("Error searching services by name or description: " + e.getMessage());
            return services;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Service> findAllSortedByPrice(boolean ascending) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Service> services = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            // Modified query to ensure only distinct services are returned, sorted by price
            String sql = "SELECT DISTINCT id, name, description, price, duration_minutes FROM services ORDER BY price " + (ascending ? "ASC" : "DESC");
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getLong("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("price"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                
                services.add(service);
            }
            
            return services;
        } catch (SQLException e) {
            System.err.println("Error finding services sorted by price: " + e.getMessage());
            return services;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}