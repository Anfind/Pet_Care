package com.petcare.service;

import com.petcare.model.Order;
import com.petcare.model.OrderItem;
import com.petcare.model.Product;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Order business operations.
 */
public interface OrderService {
    
    /**
     * Create a new order.
     * 
     * @param userId User ID
     * @param items List of order items
     * @return The created order
     */
    Order createOrder(Long userId, List<OrderItem> items);
    
    /**
     * Add an item to an existing order.
     * 
     * @param orderId Order ID
     * @param productId Product ID
     * @param quantity Quantity
     * @return The updated order
     */
    Order addOrderItem(Long orderId, Long productId, int quantity);
    
    /**
     * Get an order by ID.
     * 
     * @param orderId Order ID
     * @return Optional containing the order if found
     */
    Optional<Order> getOrderById(Long orderId);
    
    /**
     * Get all orders.
     * 
     * @return List of all orders
     */
    List<Order> getAllOrders();
    
    /**
     * Get orders by user ID.
     * 
     * @param userId User ID
     * @return List of orders for the given user
     */
    List<Order> getOrdersByUser(Long userId);
    
    /**
     * Get orders by status.
     * 
     * @param status Order status
     * @return List of orders with the given status
     */
    List<Order> getOrdersByStatus(String status);
    
    /**
     * Get orders created within a date range.
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of orders created within the date range
     */
    List<Order> getOrdersByDateRange(Timestamp startDate, Timestamp endDate);
    
    /**
     * Update the status of an order.
     * 
     * @param orderId Order ID
     * @param status New status
     * @return true if updated successfully, false otherwise
     */
    boolean updateOrderStatus(Long orderId, String status);
    
    /**
     * Cancel an order.
     * 
     * @param orderId Order ID
     * @return true if cancelled successfully, false otherwise
     */
    boolean cancelOrder(Long orderId);
    
    /**
     * Get an order with all its items loaded.
     * 
     * @param orderId Order ID
     * @return The order with its items loaded, or null if not found
     */
    Order getOrderWithItems(Long orderId);
    
    /**
     * Calculate the total price of an order.
     * 
     * @param items List of order items
     * @return The total price
     */
    BigDecimal calculateOrderTotal(List<OrderItem> items);
}