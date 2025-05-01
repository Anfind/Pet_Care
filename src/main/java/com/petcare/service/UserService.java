package com.petcare.service;

import com.petcare.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for User-related business operations.
 */
public interface UserService {
    
    /**
     * Register a new user.
     * 
     * @param name User's name
     * @param email User's email
     * @param password User's password (will be hashed)
     * @param role User's role ("ADMIN" or "CUSTOMER")
     * @return The registered user if successful, null if failed (e.g., email already exists)
     */
    User register(String name, String email, String password, String role);
    
    /**
     * Authenticate a user with email and password.
     * 
     * @param email User's email
     * @param password User's password
     * @return An Optional containing the authenticated user if successful, or empty if authentication failed
     */
    Optional<User> login(String email, String password);
    
    /**
     * Change a user's password.
     * 
     * @param userId The user's ID
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if password was changed successfully, false otherwise
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * Get a user by ID.
     * 
     * @param userId The user's ID
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> getUserById(Long userId);
    
    /**
     * Get a user by email.
     * 
     * @param email The user's email
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Get all users.
     * 
     * @return A list of all users
     */
    List<User> getAllUsers();
    
    /**
     * Update user information.
     * 
     * @param user The user to update
     * @return The updated user if successful, null otherwise
     */
    User updateUser(User user);
    
    /**
     * Delete a user.
     * 
     * @param userId The ID of the user to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteUser(Long userId);
}
