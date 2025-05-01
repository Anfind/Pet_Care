package com.petcare.cli;

import com.petcare.model.User;
import com.petcare.service.UserService;
import com.petcare.service.impl.UserServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.util.Optional;

/**
 * Menu for authentication operations (login, register, logout, change password).
 */
public class AuthMenu implements Menu {
    
    private final AppSession session;
    private final UserService userService;
    
    public AuthMenu() {
        this.session = AppSession.getInstance();
        this.userService = new UserServiceImpl();
    }
    
    @Override
    public boolean display() {
        String[] options;
        
        if (session.isLoggedIn()) {
            options = new String[] {
                "Logout",
                "Change Password"
            };
        } else {
            options = new String[] {
                "Login",
                "Register"
            };
        }
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        if (session.isLoggedIn()) {
            switch (choice) {
                case 1:
                    logout();
                    break;
                case 2:
                    changePassword();
                    break;
            }
        } else {
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
            }
        }
        
        return true;
    }
    
    private void login() {
        ConsoleUtil.displayTitle("Login");
        String email = ConsoleUtil.readString("Enter email");
        String password = ConsoleUtil.readPassword("Enter password");
        
        Optional<User> userOpt = userService.login(email, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setCurrentUser(user);
            ConsoleUtil.pressEnterToContinue("Login successful! Welcome, " + user.getName() + ".");
        } else {
            ConsoleUtil.pressEnterToContinue("Invalid email or password. Please try again.");
        }
    }
    
    private void register() {
        ConsoleUtil.displayTitle("Register");
        String name = ConsoleUtil.readString("Enter your name");
        String email = ConsoleUtil.readString("Enter your email");
        String password = ConsoleUtil.readPassword("Enter password");
        String confirmPassword = ConsoleUtil.readPassword("Confirm password");
        
        if (!password.equals(confirmPassword)) {
            ConsoleUtil.pressEnterToContinue("Passwords do not match. Please try again.");
            return;
        }
        
        // Default role for new registrations is CUSTOMER
        User newUser = userService.register(name, email, password, "CUSTOMER");
        
        if (newUser != null) {
            ConsoleUtil.pressEnterToContinue("Registration successful! You can now login.");
        } else {
            ConsoleUtil.pressEnterToContinue("Registration failed. Email might already be in use.");
        }
    }
    
    private void logout() {
        session.logout();
        ConsoleUtil.pressEnterToContinue("You have been logged out.");
    }
    
    private void changePassword() {
        ConsoleUtil.displayTitle("Change Password");
        String oldPassword = ConsoleUtil.readPassword("Enter your current password");
        String newPassword = ConsoleUtil.readPassword("Enter new password");
        String confirmPassword = ConsoleUtil.readPassword("Confirm new password");
        
        if (!newPassword.equals(confirmPassword)) {
            ConsoleUtil.pressEnterToContinue("New passwords do not match. Please try again.");
            return;
        }
        
        boolean success = userService.changePassword(
                session.getCurrentUser().getId(),
                oldPassword,
                newPassword
        );
        
        if (success) {
            ConsoleUtil.pressEnterToContinue("Password changed successfully!");
        } else {
            ConsoleUtil.pressEnterToContinue("Failed to change password. Please check your current password.");
        }
    }
    
    @Override
    public String getTitle() {
        return session.isLoggedIn() ? "ACCOUNT MENU" : "AUTHENTICATION MENU";
    }
}