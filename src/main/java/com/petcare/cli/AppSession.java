package com.petcare.cli;

import com.petcare.model.User;

/**
 * Singleton class to manage application session state.
 */
public class AppSession {
    
    private static AppSession instance;
    private User currentUser;
    private boolean running = true; // Added running flag
    
    private AppSession() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance of AppSession.
     * 
     * @return The AppSession instance
     */
    public static AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }
    
    /**
     * Check if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if the current user has admin privileges.
     * 
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return isLoggedIn() && "ADMIN".equals(currentUser.getRole());
    }
    
    /**
     * Get the currently logged-in user.
     * 
     * @return The current User object, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Set the current logged-in user.
     * 
     * @param user The User to set as current
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Log out the current user.
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Check if the application is still running.
     * 
     * @return true if the application is running, false if it should exit
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Set the running state of the application.
     * 
     * @param running true to keep running, false to exit
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    /**
     * Request application exit.
     */
    public void exit() {
        this.running = false;
    }
}