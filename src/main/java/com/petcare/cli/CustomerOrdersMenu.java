package com.petcare.cli;

import com.petcare.model.Order;
import com.petcare.model.OrderItem;
import com.petcare.model.Product;
import com.petcare.service.OrderService;
import com.petcare.service.ProductService;
import com.petcare.service.impl.OrderServiceImpl;
import com.petcare.service.impl.ProductServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Menu for customer order management.
 */
public class CustomerOrdersMenu implements Menu {
    
    private final AppSession session;
    private final OrderService orderService;
    private final ProductService productService;
    private final SimpleDateFormat dateFormat;
    
    public CustomerOrdersMenu() {
        this.session = AppSession.getInstance();
        this.orderService = new OrderServiceImpl();
        this.productService = new ProductServiceImpl();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public boolean display() {
        if (!session.isLoggedIn()) {
            ConsoleUtil.pressEnterToContinue("Please log in to access this feature.");
            return true;
        }
        
        String[] options = {
            "View My Orders",
            "Place New Order",
            "View Order Details",
            "Cancel Order"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewMyOrders();
                break;
            case 2:
                placeNewOrder();
                break;
            case 3:
                viewOrderDetails();
                break;
            case 4:
                cancelOrder();
                break;
        }
        
        return true;
    }
    
    private void viewMyOrders() {
        Long userId = session.getCurrentUser().getId();
        List<Order> orders = orderService.getOrdersByUser(userId);
        
        if (orders.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You don't have any orders yet.");
            return;
        }
        
        System.out.println("\n===== MY ORDERS =====");
        System.out.printf("%-5s %-20s %-12s %-10s %s%n", "ID", "DATE", "TOTAL", "STATUS", "ITEMS");
        
        for (Order order : orders) {
            // Get the number of items in the order
            Order orderWithItems = orderService.getOrderWithItems(order.getId());
            int itemCount = orderWithItems != null && orderWithItems.getItems() != null ? 
                    orderWithItems.getItems().size() : 0;
            
            System.out.printf("%-5d %-20s $%-11.2f %-10s %d%n", 
                    order.getId(), 
                    dateFormat.format(order.getCreatedAt()), 
                    order.getTotalPrice(), 
                    order.getStatus(),
                    itemCount);
        }
        
        ConsoleUtil.pressEnterToContinue("\nPress Enter to continue...");
    }
    
    private void placeNewOrder() {
        List<Product> allProducts = productService.getAllProducts();
        
        if (allProducts.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no products available to order.");
            return;
        }
        
        List<OrderItem> orderItems = new ArrayList<>();
        boolean addingItems = true;
        
        while (addingItems) {
            System.out.println("\n===== AVAILABLE PRODUCTS =====");
            System.out.printf("%-5s %-30s %-10s %s%n", "ID", "NAME", "PRICE", "TYPE");
            
            for (Product product : allProducts) {
                System.out.printf("%-5d %-30s $%-9.2f %s%n", 
                        product.getId(), 
                        product.getName(), 
                        product.getPrice(), 
                        product.getType());
            }
            
            int productId = ConsoleUtil.readInt("\nEnter product ID to add to cart (0 to finish): ", 0, Integer.MAX_VALUE);
            
            if (productId == 0) {
                // Finished adding items
                if (orderItems.isEmpty()) {
                    System.out.println("Order must contain at least one item.");
                    continue;
                }
                addingItems = false;
                continue;
            }
            
            // Find the product
            Optional<Product> productOpt = productService.getProductById((long) productId);
            if (!productOpt.isPresent()) {
                System.out.println("Invalid product ID. Please try again.");
                continue;
            }
            
            Product product = productOpt.get();
            
            // Ask for quantity
            int quantity = ConsoleUtil.readInt("Enter quantity: ", 1, 100);
            
            // Calculate subtotal
            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(quantity));
            
            // Create order item
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(quantity);
            item.setSubtotal(subtotal);
            item.setProduct(product); // Set product for easy access
            
            orderItems.add(item);
            System.out.println("Added " + quantity + " x " + product.getName() + " to cart.");
        }
        
        // Calculate total price
        BigDecimal totalPrice = orderService.calculateOrderTotal(orderItems);
        
        // Show order summary
        System.out.println("\n===== ORDER SUMMARY =====");
        System.out.printf("%-30s %-10s %-10s %s%n", "PRODUCT", "PRICE", "QTY", "SUBTOTAL");
        
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            System.out.printf("%-30s $%-9.2f %-10d $%.2f%n", 
                    product.getName(), 
                    product.getPrice(), 
                    item.getQuantity(),
                    item.getSubtotal());
        }
        
        System.out.println("\nTotal: $" + totalPrice);
        
        // Confirm order
        boolean confirm = ConsoleUtil.readYesNo("Confirm order?");
        
        if (confirm) {
            Long userId = session.getCurrentUser().getId();
            Order createdOrder = orderService.createOrder(userId, orderItems);
            
            if (createdOrder != null) {
                ConsoleUtil.pressEnterToContinue("Order placed successfully! Your order ID is: " + createdOrder.getId());
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to place order. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Order cancelled.");
        }
    }
    
    private void viewOrderDetails() {
        Long userId = session.getCurrentUser().getId();
        List<Order> orders = orderService.getOrdersByUser(userId);
        
        if (orders.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You don't have any orders yet.");
            return;
        }
        
        System.out.println("\n===== MY ORDERS =====");
        System.out.printf("%-5s %-20s %-12s %s%n", "ID", "DATE", "TOTAL", "STATUS");
        
        for (Order order : orders) {
            System.out.printf("%-5d %-20s $%-11.2f %s%n", 
                    order.getId(), 
                    dateFormat.format(order.getCreatedAt()), 
                    order.getTotalPrice(), 
                    order.getStatus());
        }
        
        int orderId = ConsoleUtil.readInt("\nEnter order ID to view details (0 to cancel): ", 0, Integer.MAX_VALUE);
        
        if (orderId == 0) {
            return;
        }
        
        // Get order with items
        Order order = orderService.getOrderWithItems((long) orderId);
        
        if (order == null) {
            ConsoleUtil.pressEnterToContinue("Order not found.");
            return;
        }
        
        // Verify that this order belongs to the current user
        if (!order.getUserId().equals(userId)) {
            ConsoleUtil.pressEnterToContinue("You don't have permission to view this order.");
            return;
        }
        
        System.out.println("\n===== ORDER #" + order.getId() + " DETAILS =====");
        System.out.println("Date: " + dateFormat.format(order.getCreatedAt()));
        System.out.println("Status: " + order.getStatus());
        System.out.println("Total: $" + order.getTotalPrice());
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            System.out.println("\nNo items in this order.");
        } else {
            System.out.println("\nItems:");
            System.out.printf("%-30s %-10s %-10s %s%n", "PRODUCT", "PRICE", "QTY", "SUBTOTAL");
            
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    System.out.printf("%-30s $%-9.2f %-10d $%.2f%n", 
                            product.getName(), 
                            product.getPrice(), 
                            item.getQuantity(),
                            item.getSubtotal());
                } else {
                    System.out.printf("%-30s %-10s %-10d $%.2f%n", 
                            "Unknown Product", 
                            "N/A", 
                            item.getQuantity(),
                            item.getSubtotal());
                }
            }
        }
        
        ConsoleUtil.pressEnterToContinue("\nPress Enter to continue...");
    }
    
    private void cancelOrder() {
        Long userId = session.getCurrentUser().getId();
        List<Order> orders = orderService.getOrdersByUser(userId);
        
        if (orders.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You don't have any orders yet.");
            return;
        }
        
        System.out.println("\n===== MY ORDERS =====");
        System.out.printf("%-5s %-20s %-12s %s%n", "ID", "DATE", "TOTAL", "STATUS");
        
        for (Order order : orders) {
            // Only show orders that can be cancelled (not DELIVERED or CANCELLED)
            if (!order.getStatus().equals("DELIVERED") && !order.getStatus().equals("CANCELLED")) {
                System.out.printf("%-5d %-20s $%-11.2f %s%n", 
                        order.getId(), 
                        dateFormat.format(order.getCreatedAt()), 
                        order.getTotalPrice(), 
                        order.getStatus());
            }
        }
        
        int orderId = ConsoleUtil.readInt("\nEnter order ID to cancel (0 to cancel operation): ", 0, Integer.MAX_VALUE);
        
        if (orderId == 0) {
            return;
        }
        
        // Get order
        Optional<Order> orderOpt = orderService.getOrderById((long) orderId);
        
        if (!orderOpt.isPresent()) {
            ConsoleUtil.pressEnterToContinue("Order not found.");
            return;
        }
        
        Order order = orderOpt.get();
        
        // Verify that this order belongs to the current user
        if (!order.getUserId().equals(userId)) {
            ConsoleUtil.pressEnterToContinue("You don't have permission to cancel this order.");
            return;
        }
        
        // Check if order can be cancelled
        if (order.getStatus().equals("DELIVERED") || order.getStatus().equals("CANCELLED")) {
            ConsoleUtil.pressEnterToContinue("This order cannot be cancelled because it is already " + order.getStatus() + ".");
            return;
        }
        
        // Confirm cancellation
        boolean confirm = ConsoleUtil.readYesNo("Are you sure you want to cancel this order?");
        
        if (confirm) {
            boolean success = orderService.cancelOrder((long) orderId);
            
            if (success) {
                ConsoleUtil.pressEnterToContinue("Order cancelled successfully.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to cancel order. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Operation cancelled.");
        }
    }
    
    @Override
    public String getTitle() {
        return "MY ORDERS";
    }
}