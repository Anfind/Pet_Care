package com.petcare.cli;

import com.petcare.model.Order;
import com.petcare.model.OrderItem;
import com.petcare.model.Product;
import com.petcare.model.User;
import com.petcare.service.OrderService;
import com.petcare.service.UserService;
import com.petcare.service.impl.OrderServiceImpl;
import com.petcare.service.impl.UserServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Menu for order management.
 */
public class OrderManagementMenu implements Menu {
    
    private final AppSession session;
    private final OrderService orderService;
    private final UserService userService;
    private final SimpleDateFormat dateFormat;
    
    public OrderManagementMenu() {
        this.session = AppSession.getInstance();
        this.orderService = new OrderServiceImpl();
        this.userService = new UserServiceImpl();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public boolean display() {
        if (!session.isAdmin()) {
            ConsoleUtil.pressEnterToContinue("Access denied. Admin privileges required.");
            return true;
        }
        
        String[] options = {
            "View All Orders",
            "View Orders by Status",
            "View Orders by Date Range",
            "View Order Details",
            "Update Order Status",
            "Cancel Order"
        };
        
        ConsoleUtil.clearScreen();
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewAllOrders();
                break;
            case 2:
                viewOrdersByStatus();
                break;
            case 3:
                viewOrdersByDateRange();
                break;
            case 4:
                viewOrderDetails();
                break;
            case 5:
                updateOrderStatus();
                break;
            case 6:
                cancelOrder();
                break;
        }
        
        return true;
    }
    
    private void viewAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        
        if (orders.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no orders in the system.");
            return;
        }
        
        displayOrderList(orders, "ALL ORDERS");
    }
    
    private void viewOrdersByStatus() {
        String[] statuses = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"};
        
        ConsoleUtil.displayTitle("Select Order Status");
        System.out.println(ConsoleUtil.BOLD + "Available statuses:" + ConsoleUtil.RESET);
        for (int i = 0; i < statuses.length; i++) {
            System.out.println(ConsoleUtil.CYAN + (i + 1) + ". " + 
                    ConsoleUtil.colorizeStatus(statuses[i]) + ConsoleUtil.RESET);
        }
        
        int statusChoice = ConsoleUtil.readInt("\nSelect status", 1, statuses.length);
        String selectedStatus = statuses[statusChoice - 1];
        
        List<Order> orders = orderService.getOrdersByStatus(selectedStatus);
        
        if (orders.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no orders with status '" + 
                    ConsoleUtil.colorizeStatus(selectedStatus) + "'.");
            return;
        }
        
        displayOrderList(orders, "ORDERS WITH STATUS: " + selectedStatus);
    }
    
    private void viewOrdersByDateRange() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat.setLenient(false);
        
        ConsoleUtil.displayTitle("Search Orders by Date Range");
        
        Date startDate = null;
        while (startDate == null) {
            try {
                String startDateStr = ConsoleUtil.readString("Enter start date (YYYY-MM-DD)");
                startDate = inputFormat.parse(startDateStr);
            } catch (ParseException e) {
                System.out.println(ConsoleUtil.YELLOW + "Invalid date format. Please use YYYY-MM-DD format." + ConsoleUtil.RESET);
            }
        }
        
        Date endDate = null;
        while (endDate == null) {
            try {
                String endDateStr = ConsoleUtil.readString("Enter end date (YYYY-MM-DD)");
                endDate = inputFormat.parse(endDateStr);
                
                // Add one day to include the end date fully
                endDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000);
                
                if (endDate.before(startDate)) {
                    System.out.println(ConsoleUtil.YELLOW + "End date must be after start date." + ConsoleUtil.RESET);
                    endDate = null;
                }
            } catch (ParseException e) {
                System.out.println(ConsoleUtil.YELLOW + "Invalid date format. Please use YYYY-MM-DD format." + ConsoleUtil.RESET);
            }
        }
        
        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime());
        
        List<Order> orders = orderService.getOrdersByDateRange(startTimestamp, endTimestamp);
        
        if (orders.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no orders in the specified date range.");
            return;
        }
        
        displayOrderList(orders, "ORDERS BETWEEN " + 
                ConsoleUtil.GREEN + inputFormat.format(startDate) + ConsoleUtil.RESET + 
                " AND " + 
                ConsoleUtil.GREEN + inputFormat.format(new Date(endDate.getTime() - 24 * 60 * 60 * 1000)) + ConsoleUtil.RESET);
    }
    
    private void displayOrderList(List<Order> orders, String title) {
        ConsoleUtil.displayTitle(title);
        
        // Define column headers and widths
        String[] headers = {"ID", "DATE", "CUSTOMER", "TOTAL", "STATUS", "ITEMS"};
        int[] widths = {5, 20, 20, 10, 15, 5};
        
        // Draw table header
        ConsoleUtil.drawTableHeader(headers, widths);
        
        // Draw table rows
        for (Order order : orders) {
            // Get the number of items in the order
            Order orderWithItems = orderService.getOrderWithItems(order.getId());
            int itemCount = orderWithItems != null && orderWithItems.getItems() != null ? 
                    orderWithItems.getItems().size() : 0;
            
            // Get customer name
            Optional<User> userOpt = userService.getUserById(order.getUserId());
            String customerName = userOpt.isPresent() ? userOpt.get().getName() : "Unknown";
            
            // Format the data for displaying
            String[] rowData = {
                String.valueOf(order.getId()),
                dateFormat.format(order.getCreatedAt()),
                customerName,
                "$" + order.getTotalPrice(),
                ConsoleUtil.colorizeStatus(order.getStatus()),
                String.valueOf(itemCount)
            };
            
            ConsoleUtil.drawTableRow(rowData, widths);
        }
        
        // Draw table footer
        ConsoleUtil.drawTableFooter(widths);
        
        // Display summary
        System.out.println(ConsoleUtil.BOLD + "\nTotal: " + ConsoleUtil.CYAN + 
                orders.size() + ConsoleUtil.RESET + ConsoleUtil.BOLD + " order(s)" + ConsoleUtil.RESET);
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void viewOrderDetails() {
        int orderId = ConsoleUtil.readInt("Enter order ID (0 to cancel)", 0, Integer.MAX_VALUE);
        
        if (orderId == 0) {
            return;
        }
        
        // Get order with items
        Order order = orderService.getOrderWithItems((long) orderId);
        
        if (order == null) {
            ConsoleUtil.pressEnterToContinue("Order not found.");
            return;
        }
        
        // Get customer name
        Optional<User> userOpt = userService.getUserById(order.getUserId());
        String customerName = userOpt.isPresent() ? userOpt.get().getName() : "Unknown";
        
        ConsoleUtil.displayTitle("ORDER #" + orderId + " DETAILS");
        
        int width = 50;
        ConsoleUtil.printBoxTop(width);
        
        // Order details
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Order ID:    " + 
                ConsoleUtil.RESET + order.getId() + 
                " ".repeat(width - 20) + ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Date:        " + 
                ConsoleUtil.RESET + dateFormat.format(order.getCreatedAt()) + 
                " ".repeat(width - dateFormat.format(order.getCreatedAt()).length() - 13) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Customer:    " + 
                ConsoleUtil.RESET + customerName + 
                " ".repeat(width - customerName.length() - 13) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Status:      " + 
                ConsoleUtil.colorizeStatus(order.getStatus()) + 
                " ".repeat(width - order.getStatus().length() - 13) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Total:       " + 
                ConsoleUtil.GREEN + "$" + order.getTotalPrice() + ConsoleUtil.RESET + 
                " ".repeat(width - String.valueOf(order.getTotalPrice()).length() - 14) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        ConsoleUtil.printBoxBottom(width);
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            System.out.println(ConsoleUtil.YELLOW + "\nNo items in this order." + ConsoleUtil.RESET);
        } else {
            System.out.println(ConsoleUtil.BOLD + "\nORDER ITEMS:" + ConsoleUtil.RESET);
            
            // Define column headers and widths for items
            String[] headers = {"PRODUCT", "PRICE", "QTY", "SUBTOTAL"};
            int[] widths = {30, 10, 5, 10};
            
            // Draw table header
            ConsoleUtil.drawTableHeader(headers, widths);
            
            // Draw table rows
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                String productName = product != null ? product.getName() : "Unknown Product";
                String price = product != null ? "$" + product.getPrice() : "N/A";
                
                // Format the data for displaying
                String[] rowData = {
                    productName,
                    price,
                    String.valueOf(item.getQuantity()),
                    "$" + item.getSubtotal()
                };
                
                ConsoleUtil.drawTableRow(rowData, widths);
            }
            
            // Draw table footer
            ConsoleUtil.drawTableFooter(widths);
        }
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void updateOrderStatus() {
        int orderId = ConsoleUtil.readInt("Enter order ID (0 to cancel)", 0, Integer.MAX_VALUE);
        
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
        
        // Get customer name
        Optional<User> userOpt = userService.getUserById(order.getUserId());
        String customerName = userOpt.isPresent() ? userOpt.get().getName() : "Unknown";
        
        ConsoleUtil.displayTitle("UPDATE ORDER #" + orderId + " STATUS");
        
        int width = 50;
        ConsoleUtil.printBoxTop(width);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Date:        " + 
                ConsoleUtil.RESET + dateFormat.format(order.getCreatedAt()) + 
                " ".repeat(width - dateFormat.format(order.getCreatedAt()).length() - 13) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Customer:    " + 
                ConsoleUtil.RESET + customerName + 
                " ".repeat(width - customerName.length() - 13) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Current Status: " + 
                ConsoleUtil.colorizeStatus(order.getStatus()) + 
                " ".repeat(width - order.getStatus().length() - 16) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Total:       " + 
                ConsoleUtil.GREEN + "$" + order.getTotalPrice() + ConsoleUtil.RESET + 
                " ".repeat(width - String.valueOf(order.getTotalPrice()).length() - 14) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        ConsoleUtil.printBoxBottom(width);
        
        // Show status options
        String[] statuses = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"};
        
        System.out.println(ConsoleUtil.BOLD + "\nAvailable statuses:" + ConsoleUtil.RESET);
        int validOptions = 0;
        int[] statusIndices = new int[statuses.length];
        
        for (int i = 0; i < statuses.length; i++) {
            // Don't show the current status as an option
            if (!statuses[i].equals(order.getStatus())) {
                validOptions++;
                statusIndices[validOptions-1] = i;
                System.out.println(ConsoleUtil.CYAN + validOptions + ". " + 
                        ConsoleUtil.colorizeStatus(statuses[i]) + ConsoleUtil.RESET);
            }
        }
        
        int statusChoice = ConsoleUtil.readInt("\nSelect new status", 0, validOptions);
        
        if (statusChoice == 0) {
            return;
        }
        
        String newStatus = statuses[statusIndices[statusChoice-1]];
        
        // Confirm update
        boolean confirm = ConsoleUtil.readYesNo("Update status from '" + 
                ConsoleUtil.colorizeStatus(order.getStatus()) + "' to '" + 
                ConsoleUtil.colorizeStatus(newStatus) + "'?");
        
        if (confirm) {
            boolean success = orderService.updateOrderStatus((long) orderId, newStatus);
            
            if (success) {
                ConsoleUtil.pressEnterToContinue(ConsoleUtil.GREEN + "Order status updated successfully." + ConsoleUtil.RESET);
            } else {
                ConsoleUtil.pressEnterToContinue(ConsoleUtil.RED + "Failed to update order status. Please try again later." + ConsoleUtil.RESET);
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Operation cancelled.");
        }
    }
    
    private void cancelOrder() {
        int orderId = ConsoleUtil.readInt("Enter order ID to cancel (0 to cancel operation)", 0, Integer.MAX_VALUE);
        
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
        
        // Check if order can be cancelled
        if (order.getStatus().equals("DELIVERED") || order.getStatus().equals("CANCELLED")) {
            ConsoleUtil.pressEnterToContinue(ConsoleUtil.RED + "This order cannot be cancelled because it is already " + 
                    ConsoleUtil.colorizeStatus(order.getStatus()) + "." + ConsoleUtil.RESET);
            return;
        }
        
        // Get customer name
        Optional<User> userOpt = userService.getUserById(order.getUserId());
        String customerName = userOpt.isPresent() ? userOpt.get().getName() : "Unknown";
        
        ConsoleUtil.displayTitle("CANCEL ORDER #" + orderId);
        
        int width = 50;
        ConsoleUtil.printBoxTop(width);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Date:     " + 
                ConsoleUtil.RESET + dateFormat.format(order.getCreatedAt()) + 
                " ".repeat(width - dateFormat.format(order.getCreatedAt()).length() - 10) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Customer: " + 
                ConsoleUtil.RESET + customerName + 
                " ".repeat(width - customerName.length() - 10) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Status:   " + 
                ConsoleUtil.colorizeStatus(order.getStatus()) + 
                " ".repeat(width - order.getStatus().length() - 10) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        System.out.println(ConsoleUtil.BOLD + "│ " + ConsoleUtil.CYAN + "Total:    " + 
                ConsoleUtil.GREEN + "$" + order.getTotalPrice() + ConsoleUtil.RESET + 
                " ".repeat(width - String.valueOf(order.getTotalPrice()).length() - 11) + 
                ConsoleUtil.BOLD + "│" + ConsoleUtil.RESET);
        
        ConsoleUtil.printBoxBottom(width);
        
        // Confirm cancellation with warning
        System.out.println(ConsoleUtil.YELLOW + "\n⚠️  WARNING: This action cannot be undone!" + ConsoleUtil.RESET);
        boolean confirm = ConsoleUtil.readYesNo("Are you sure you want to cancel this order?");
        
        if (confirm) {
            boolean success = orderService.cancelOrder((long) orderId);
            
            if (success) {
                ConsoleUtil.pressEnterToContinue(ConsoleUtil.GREEN + "Order cancelled successfully." + ConsoleUtil.RESET);
            } else {
                ConsoleUtil.pressEnterToContinue(ConsoleUtil.RED + "Failed to cancel order. Please try again later." + ConsoleUtil.RESET);
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Operation cancelled.");
        }
    }
    
    @Override
    public String getTitle() {
        return "ORDER MANAGEMENT";
    }
}