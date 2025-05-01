package com.petcare.dao;

import com.petcare.model.Order;
import java.util.List;
import java.sql.Timestamp;

/**
 * DAO interface for Order entity operations.
 */
public interface OrderDAO extends BaseDAO<Order, Long> {
    
    /**
     * Find orders by user ID.
     * 
     * @param userId The user ID
     * @return A list of orders for the given user
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Find orders by their status.
     * 
     * @param status The order status ("PENDING", "COMPLETED", "CANCELLED")
     * @return A list of orders with the given status
     */
    List<Order> findByStatus(String status);
    
    /**
     * Find orders created within a date range.
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of orders created within the date range
     */
    List<Order> findByDateRange(Timestamp startDate, Timestamp endDate);
    
    /**
     * Get order with all its items loaded.
     * 
     * @param orderId The order ID
     * @return The order with its items loaded
     */
    Order getOrderWithItems(Long orderId);
    
    /**
     * Update the status of an order.
     * 
     * @param orderId The order ID
     * @param status The new status
     * @return true if updated successfully, false otherwise
     */
    boolean updateStatus(Long orderId, String status);
}