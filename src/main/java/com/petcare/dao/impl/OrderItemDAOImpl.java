package com.petcare.dao.impl;

import com.petcare.dao.OrderItemDAO;
import com.petcare.dao.ProductDAO;
import com.petcare.model.OrderItem;
import com.petcare.model.Product;
import com.petcare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OrderItemDAO interface for database operations.
 */
public class OrderItemDAOImpl implements OrderItemDAO {
    
    private final ProductDAO productDAO;
    
    public OrderItemDAOImpl() {
        this.productDAO = new ProductDAOImpl();
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (orderItem.getId() == null) {
                // Insert new order item
                String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setLong(1, orderItem.getOrderId());
                stmt.setLong(2, orderItem.getProductId());
                stmt.setInt(3, orderItem.getQuantity());
                stmt.setBigDecimal(4, orderItem.getPrice());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating order item failed, no rows affected.");
                }
                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    orderItem.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating order item failed, no ID obtained.");
                }
            } else {
                // Update existing order item
                return update(orderItem);
            }
            
            return orderItem;
        } catch (SQLException e) {
            System.err.println("Error saving order item: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM order_items WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                OrderItem orderItem = extractOrderItemFromResultSet(rs);
                return Optional.of(orderItem);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding order item by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<OrderItem> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<OrderItem> orderItems = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM order_items";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderItem orderItem = extractOrderItemFromResultSet(rs);
                orderItems.add(orderItem);
            }
            
            return orderItems;
        } catch (SQLException e) {
            System.err.println("Error finding all order items: " + e.getMessage());
            return orderItems;
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
            String sql = "DELETE FROM order_items WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order item by ID: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public OrderItem update(OrderItem orderItem) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ?, price = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, orderItem.getOrderId());
            stmt.setLong(2, orderItem.getProductId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getPrice());
            stmt.setLong(5, orderItem.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating order item failed, no rows affected.");
            }
            
            return orderItem;
        } catch (SQLException e) {
            System.err.println("Error updating order item: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<OrderItem> orderItems = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM order_items WHERE order_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, orderId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderItem orderItem = extractOrderItemFromResultSet(rs);
                orderItems.add(orderItem);
            }
            
            return orderItems;
        } catch (SQLException e) {
            System.err.println("Error finding order items by order ID: " + e.getMessage());
            return orderItems;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<OrderItem> findByProductId(Long productId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<OrderItem> orderItems = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM order_items WHERE product_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, productId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderItem orderItem = extractOrderItemFromResultSet(rs);
                orderItems.add(orderItem);
            }
            
            return orderItems;
        } catch (SQLException e) {
            System.err.println("Error finding order items by product ID: " + e.getMessage());
            return orderItems;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<OrderItem> findByOrderIdWithProduct(Long orderId) {
        List<OrderItem> orderItems = findByOrderId(orderId);
        
        // Load product details for each order item
        for (OrderItem item : orderItems) {
            Optional<Product> productOpt = productDAO.findById(item.getProductId());
            if (productOpt.isPresent()) {
                item.setProduct(productOpt.get());
            }
        }
        
        return orderItems;
    }

    @Override
    public int deleteByOrderId(Long orderId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM order_items WHERE order_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, orderId);
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting order items by order ID: " + e.getMessage());
            return 0;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }
    
    /**
     * Helper method to extract an OrderItem object from a ResultSet.
     */
    private OrderItem extractOrderItemFromResultSet(ResultSet rs) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getLong("id"));
        orderItem.setOrderId(rs.getLong("order_id"));
        orderItem.setProductId(rs.getLong("product_id"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setPrice(rs.getBigDecimal("price"));
        return orderItem;
    }
}