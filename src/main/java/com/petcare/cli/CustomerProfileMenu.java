package com.petcare.cli;

import com.petcare.model.User;
import com.petcare.service.UserService;
import com.petcare.service.impl.UserServiceImpl;
import com.petcare.util.ConsoleUtil;

/**
 * Menu for customer profile management.
 */
public class CustomerProfileMenu implements Menu {
    
    private final AppSession session;
    private final UserService userService;
    
    public CustomerProfileMenu() {
        this.session = AppSession.getInstance();
        this.userService = new UserServiceImpl();
    }
    
    @Override
    public boolean display() {
        if (!session.isLoggedIn()) {
            ConsoleUtil.pressEnterToContinue("You need to log in to access this feature.");
            return true;
        }
        
        User currentUser = session.getCurrentUser();
        
        String[] options = {
            "View Profile",
            "Update Profile"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                updateProfile();
                break;
        }
        
        return true;
    }
    
    private void viewProfile() {
        User user = session.getCurrentUser();
        
        ConsoleUtil.displayTitle("Your Profile");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Role: " + user.getRole());
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void updateProfile() {
        User user = session.getCurrentUser();
        
        ConsoleUtil.displayTitle("Update Profile");
        System.out.println("Current name: " + user.getName());
        String newName = ConsoleUtil.readString("Enter new name (or press Enter to keep current)");
        
        if (!newName.isEmpty()) {
            user.setName(newName);
            User updatedUser = userService.updateUser(user);
            
            if (updatedUser != null) {
                session.setCurrentUser(updatedUser);
                ConsoleUtil.pressEnterToContinue("Profile updated successfully!");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to update profile. Please try again.");
            }
        }
    }
    
    @Override
    public String getTitle() {
        return "PROFILE MENU";
    }
}