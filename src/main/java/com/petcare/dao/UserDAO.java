package com.petcare.dao;

import com.petcare.model.User;

import java.util.Optional;

/**
 * DAO interface for User entity operations.
 */
public interface UserDAO extends BaseDAO<User, Long> {
    
    /**
     * Find a user by their email address.
     * 
     * @param email The email address to search for
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Authenticate a user with their email and password.
     * 
     * @param email The user's email
     * @param password The user's password (will be hashed before comparison)
     * @return An Optional containing the authenticated user if successful, or empty if authentication failed
     */
    Optional<User> authenticate(String email, String password);
    
    /**
     * Change a user's password.
     * 
     * @param userId The user's ID
     * @param newPassword The new password (should be hashed before storage)
     * @return true if password was changed successfully, false otherwise
     */
    boolean changePassword(Long userId, String newPassword);
}