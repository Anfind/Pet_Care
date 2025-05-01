package com.petcare.dao;

import com.petcare.model.OrderItem;
import java.util.List;

/**
 * DAO interface for OrderItem entity operations.
 */
public interface OrderItemDAO extends BaseDAO<OrderItem, Long> {
    
    /**
     * Find order items by order ID.
     * 
     * @param orderId The order ID
     * @return A list of order items for the given order
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * Find order items by product ID.
     * 
     * @param productId The product ID
     * @return A list of order items for the given product
     */
    List<OrderItem> findByProductId(Long productId);
    
    /**
     * Find order items by order ID with product details loaded.
     * 
     * @param orderId The order ID
     * @return A list of order items with product details for the given order
     */
    List<OrderItem> findByOrderIdWithProduct(Long orderId);
    
    /**
     * Delete all items for a specific order.
     * 
     * @param orderId The order ID
     * @return The number of items deleted
     */
    int deleteByOrderId(Long orderId);
}
