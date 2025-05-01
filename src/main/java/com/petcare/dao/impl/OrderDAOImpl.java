package com.petcare.dao.impl;

import com.petcare.dao.OrderDAO;
import com.petcare.dao.OrderItemDAO;
import com.petcare.model.Order;
import com.petcare.model.OrderItem;
import com.petcare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OrderDAO interface for database operations.
 */
public class OrderDAOImpl implements OrderDAO {
    
    private final OrderItemDAO orderItemDAO;
    
    public OrderDAOImpl() {
        this.orderItemDAO = new OrderItemDAOImpl();
    }

    @Override
    public Order save(Order order) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (order.getId() == null) {
                // Insert new order
                String sql = "INSERT INTO orders (user_id, created_at, total_price, status) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setLong(1, order.getUserId());
                stmt.setTimestamp(2, order.getCreatedAt());
                stmt.setBigDecimal(3, order.getTotalPrice());
                stmt.setString(4, order.getStatus());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating order failed, no rows affected.");
                }
                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            } else {
                // Update existing order
                return update(order);
            }
            
            return order;
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getLong("user_id"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                
                return Optional.of(order);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Order> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getLong("user_id"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                
                orders.add(order);
            }
            
            return orders;
        } catch (SQLException e) {
            System.err.println("Error finding all orders: " + e.getMessage());
            return orders;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Order update(Order order) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE orders SET user_id = ?, created_at = ?, total_price = ?, status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, order.getUserId());
            stmt.setTimestamp(2, order.getCreatedAt());
            stmt.setBigDecimal(3, order.getTotalPrice());
            stmt.setString(4, order.getStatus());
            stmt.setLong(5, order.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating order failed, no rows affected.");
            }
            
            return order;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // First delete all order items
            orderItemDAO.deleteByOrderId(id);
            
            // Then delete the order
            String sql = "DELETE FROM orders WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order by ID: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getLong("user_id"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                
                orders.add(order);
            }
            
            return orders;
        } catch (SQLException e) {
            System.err.println("Error finding orders by user ID: " + e.getMessage());
            return orders;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Order> findByStatus(String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getLong("user_id"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                
                orders.add(order);
            }
            
            return orders;
        } catch (SQLException e) {
            System.err.println("Error finding orders by status: " + e.getMessage());
            return orders;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Order> findByDateRange(Timestamp startDate, Timestamp endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getLong("user_id"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                
                orders.add(order);
            }
            
            return orders;
        } catch (SQLException e) {
            System.err.println("Error finding orders by date range: " + e.getMessage());
            return orders;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Order getOrderWithItems(Long orderId) {
        Optional<Order> orderOpt = findById(orderId);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order order = orderOpt.get();
        
        // Load order items with product details
        List<OrderItem> items = orderItemDAO.findByOrderIdWithProduct(orderId);
        order.setItems(items);
        
        return order;
    }

    @Override
    public boolean updateStatus(Long orderId, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE orders SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setLong(2, orderId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }
}