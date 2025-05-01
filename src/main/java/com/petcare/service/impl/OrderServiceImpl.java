package com.petcare.service.impl;

import com.petcare.dao.OrderDAO;
import com.petcare.dao.OrderItemDAO;
import com.petcare.dao.ProductDAO;
import com.petcare.dao.impl.OrderDAOImpl;
import com.petcare.dao.impl.OrderItemDAOImpl;
import com.petcare.dao.impl.ProductDAOImpl;
import com.petcare.model.Order;
import com.petcare.model.OrderItem;
import com.petcare.model.Product;
import com.petcare.service.OrderService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OrderService interface.
 */
public class OrderServiceImpl implements OrderService {
    
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final ProductDAO productDAO;
    
    public OrderServiceImpl() {
        this.orderDAO = new OrderDAOImpl();
        this.orderItemDAO = new OrderItemDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }
    
    @Override
    public Order createOrder(Long userId, List<OrderItem> items) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        // Create new order
        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedAt(new Timestamp(new Date().getTime()));
        order.setStatus("PENDING");
        
        // Calculate total price
        BigDecimal totalPrice = calculateOrderTotal(items);
        order.setTotalPrice(totalPrice);
        
        // Save order
        Order savedOrder = orderDAO.save(order);
        if (savedOrder == null) {
            return null;
        }
        
        // Save order items
        for (OrderItem item : items) {
            item.setOrderId(savedOrder.getId());
            OrderItem savedItem = orderItemDAO.save(item);
            if (savedItem != null) {
                savedOrder.addItem(savedItem);
            }
        }
        
        return savedOrder;
    }
    
    @Override
    public Order addOrderItem(Long orderId, Long productId, int quantity) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }
        
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        // Verify that order exists
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        // Verify that product exists
        Optional<Product> productOpt = productDAO.findById(productId);
        if (!productOpt.isPresent()) {
            return null;
        }
        
        Product product = productOpt.get();
        
        // Calculate subtotal
        BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(quantity));
        
        // Create new order item
        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setSubtotal(subtotal);
        item.setProduct(product); // Set product for easy access
        
        // Save order item
        OrderItem savedItem = orderItemDAO.save(item);
        if (savedItem == null) {
            return null;
        }
        
        // Update order total price
        Order order = orderOpt.get();
        List<OrderItem> items = new ArrayList<>(orderItemDAO.findByOrderId(orderId));
        items.add(savedItem);
        
        BigDecimal newTotal = calculateOrderTotal(items);
        order.setTotalPrice(newTotal);
        
        // Update order
        Order updatedOrder = orderDAO.update(order);
        if (updatedOrder != null) {
            updatedOrder.setItems(items);
        }
        
        return updatedOrder;
    }
    
    @Override
    public Optional<Order> getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }
        
        return orderDAO.findById(orderId);
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }
    
    @Override
    public List<Order> getOrdersByUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        return orderDAO.findByUserId(userId);
    }
    
    @Override
    public List<Order> getOrdersByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        return orderDAO.findByStatus(status);
    }
    
    @Override
    public List<Order> getOrdersByDateRange(Timestamp startDate, Timestamp endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return orderDAO.findByDateRange(startDate, endDate);
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, String status) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        // Validate status
        if (!isValidOrderStatus(status)) {
            throw new IllegalArgumentException("Invalid order status. Valid statuses are: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED");
        }
        
        return orderDAO.updateStatus(orderId, status);
    }
    
    @Override
    public boolean cancelOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }
        
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        
        // Check if order can be cancelled
        String status = order.getStatus();
        if ("DELIVERED".equals(status) || "CANCELLED".equals(status)) {
            return false;
        }
        
        return orderDAO.updateStatus(orderId, "CANCELLED");
    }
    
    @Override
    public Order getOrderWithItems(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }
        
        return orderDAO.getOrderWithItems(orderId);
    }
    
    @Override
    public BigDecimal calculateOrderTotal(List<OrderItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getSubtotal());
        }
        
        return total;
    }
    
    /**
     * Check if the order status is valid.
     */
    private boolean isValidOrderStatus(String status) {
        return "PENDING".equals(status) || 
               "PROCESSING".equals(status) || 
               "SHIPPED".equals(status) || 
               "DELIVERED".equals(status) || 
               "CANCELLED".equals(status);
    }
}