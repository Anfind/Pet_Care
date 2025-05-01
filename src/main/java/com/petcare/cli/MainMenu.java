package com.petcare.cli;

import com.petcare.model.User;
import com.petcare.util.ConsoleUtil;

/**
 * Main menu of the Pet Care application.
 */
public class MainMenu implements Menu {
    
    private final AppSession session;
    
    public MainMenu() {
        this.session = AppSession.getInstance();
    }
    
    @Override
    public boolean display() {
        // Display different options based on authentication status and role
        String[] options;
        
        if (!session.isLoggedIn()) {
            // Not logged in
            options = new String[] {
                "Customer Management",
                "Product & Service Management",
                "Order Management",
                "Booking Management",
                "Review Management",
                "Blog Management",
                "Chat with Admin",
                "FAQ & Contact",
                "Authentication (Login/Register)"
            };
        } else if (session.isAdmin()) {
            // Admin user
            options = new String[] {
                "Customer Management",
                "Product & Service Management",
                "Order Management",
                "Booking Management",
                "Review Management",
                "Blog Management",
                "Chat Management",
                "FAQ & Contact Management",
                "Logout/Change Password"
            };
        } else {
            // Regular customer
            options = new String[] {
                "My Profile",
                "Browse Products & Services",
                "My Orders",
                "My Bookings",
                "My Reviews",
                "Browse Blog",
                "Chat with Admin",
                "FAQ & Contact",
                "Logout/Change Password"
            };
        }
        
        while (session.isRunning()) {
            String greeting = session.isLoggedIn() ? 
                    "Welcome, " + session.getCurrentUser().getName() + "!" : 
                    "Welcome to Pet Care Service!";
            
            ConsoleUtil.displayTitle(greeting);
            int choice = ConsoleUtil.displayMenu(getTitle(), options);
            
            if (choice == 0) {
                return false; // Exit application
            }
            
            handleMenuChoice(choice);
        }
        
        return true;
    }
    
    private void handleMenuChoice(int choice) {
        Menu selectedMenu = null;
        
        if (!session.isLoggedIn()) {
            // Not logged in
            switch (choice) {
                case 9: // Authentication
                    selectedMenu = new AuthMenu();
                    break;
                default:
                    ConsoleUtil.pressEnterToContinue("Please log in to access this feature.");
                    return;
            }
        } else if (session.isAdmin()) {
            // Admin user
            switch (choice) {
                case 1: // Customer Management
                    selectedMenu = new CustomerManagementMenu();
                    break;
                case 2: // Product & Service Management
                    selectedMenu = new ProductServiceManagementMenu();
                    break;
                case 3: // Order Management
                    selectedMenu = new OrderManagementMenu();
                    break;
                case 4: // Booking Management
                    selectedMenu = new BookingManagementMenu();
                    break;
                case 5: // Review Management
                    selectedMenu = new ReviewManagementMenu();
                    break;
                case 6: // Blog Management
                    selectedMenu = new BlogManagementMenu();
                    break;
                case 7: // Chat Management
                    selectedMenu = new ChatManagementMenu();
                    break;
                case 8: // FAQ & Contact Management
                    selectedMenu = new FAQContactManagementMenu();
                    break;
                case 9: // Logout/Change Password
                    selectedMenu = new AuthMenu();
                    break;
            }
        } else {
            // Regular customer
            switch (choice) {
                case 1: // My Profile
                    selectedMenu = new CustomerProfileMenu();
                    break;
                case 2: // Browse Products & Services
                    selectedMenu = new BrowseProductsServicesMenu();
                    break;
                case 3: // My Orders
                    selectedMenu = new CustomerOrdersMenu();
                    break;
                case 4: // My Bookings
                    selectedMenu = new CustomerBookingsMenu();
                    break;
                case 5: // My Reviews
                    selectedMenu = new CustomerReviewsMenu();
                    break;
                case 6: // Browse Blog
                    selectedMenu = new BlogBrowseMenu();
                    break;
                case 7: // Chat with Admin
                    selectedMenu = new CustomerChatMenu();
                    break;
                case 8: // FAQ & Contact
                    selectedMenu = new FAQContactMenu();
                    break;
                case 9: // Logout/Change Password
                    selectedMenu = new AuthMenu();
                    break;
            }
        }
        
        if (selectedMenu != null) {
            selectedMenu.display();
        } else {
            ConsoleUtil.pressEnterToContinue("This feature is not yet implemented.");
        }
    }
    
    @Override
    public String getTitle() {
        return "MAIN MENU";
    }
}