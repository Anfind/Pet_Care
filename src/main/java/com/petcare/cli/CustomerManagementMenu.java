package com.petcare.cli;

import com.petcare.model.User;
import com.petcare.service.UserService;
import com.petcare.service.impl.UserServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.util.List;
import java.util.Optional;

/**
 * Menu for customer management (admin only).
 */
public class CustomerManagementMenu implements Menu {
    
    private final AppSession session;
    private final UserService userService;
    
    public CustomerManagementMenu() {
        this.session = AppSession.getInstance();
        this.userService = new UserServiceImpl();
    }
    
    @Override
    public boolean display() {
        if (!session.isAdmin()) {
            ConsoleUtil.pressEnterToContinue("Access denied. Admin privileges required.");
            return true;
        }
        
        String[] options = {
            "View All Customers",
            "Search Customer by Email",
            "Create New Customer",
            "Delete Customer"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewAllCustomers();
                break;
            case 2:
                searchCustomerByEmail();
                break;
            case 3:
                createNewCustomer();
                break;
            case 4:
                deleteCustomer();
                break;
        }
        
        return true;
    }
    
    private void viewAllCustomers() {
        ConsoleUtil.displayTitle("All Customers");
        
        List<User> customers = userService.getAllUsers();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            System.out.println(String.format("%-5s %-20s %-30s %-10s", "ID", "Name", "Email", "Role"));
            System.out.println("-".repeat(70));
            
            for (User user : customers) {
                System.out.println(String.format("%-5d %-20s %-30s %-10s", 
                    user.getId(), 
                    user.getName(), 
                    user.getEmail(), 
                    user.getRole()));
            }
        }
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void searchCustomerByEmail() {
        ConsoleUtil.displayTitle("Search Customer by Email");
        
        String email = ConsoleUtil.readString("Enter customer email");
        
        // Using findByEmail through userService
        Optional<User> userOpt = ((UserServiceImpl)userService).getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(String.format("%-5s %-20s %-30s %-10s", "ID", "Name", "Email", "Role"));
            System.out.println("-".repeat(70));
            System.out.println(String.format("%-5d %-20s %-30s %-10s", 
                user.getId(), 
                user.getName(), 
                user.getEmail(), 
                user.getRole()));
        } else {
            System.out.println("No customer found with that email.");
        }
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void createNewCustomer() {
        ConsoleUtil.displayTitle("Create New Customer");
        
        String name = ConsoleUtil.readString("Enter customer name");
        String email = ConsoleUtil.readString("Enter customer email");
        String password = ConsoleUtil.readPassword("Enter customer password");
        
        User newUser = userService.register(name, email, password, "CUSTOMER");
        
        if (newUser != null) {
            System.out.println("Customer created successfully with ID: " + newUser.getId());
        } else {
            System.out.println("Failed to create customer. Email might already be in use.");
        }
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void deleteCustomer() {
        ConsoleUtil.displayTitle("Delete Customer");
        
        long customerId = ConsoleUtil.readInt("Enter customer ID to delete");
        
        if (customerId == session.getCurrentUser().getId()) {
            System.out.println("You cannot delete your own account.");
            ConsoleUtil.pressEnterToContinue("");
            return;
        }
        
        Optional<User> userOpt = userService.getUserById(customerId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            System.out.println("Customer details:");
            System.out.println("ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getRole());
            
            boolean confirm = ConsoleUtil.readYesNo("Are you sure you want to delete this customer?");
            
            if (confirm) {
                boolean success = userService.deleteUser(customerId);
                
                if (success) {
                    System.out.println("Customer deleted successfully.");
                } else {
                    System.out.println("Failed to delete customer.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } else {
            System.out.println("No customer found with that ID.");
        }
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    @Override
    public String getTitle() {
        return "CUSTOMER MANAGEMENT";
    }
}
