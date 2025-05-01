package com.petcare.service.impl;

import com.petcare.dao.UserDAO;
import com.petcare.dao.impl.UserDAOImpl;
import com.petcare.model.User;
import com.petcare.service.UserService;
import com.petcare.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService interface.
 */
public class UserServiceImpl implements UserService {
    
    private final UserDAO userDAO;
    
    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }
    
    @Override
    public User register(String name, String email, String password, String role) {
        // Check if user with email already exists
        Optional<User> existingUser = userDAO.findByEmail(email);
        if (existingUser.isPresent()) {
            System.out.println("A user with this email already exists.");
            return null;
        }
        
        // Create and save new user
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        
        // Hash the password before storing
        String hashedPassword = PasswordUtil.createPasswordHash(password);
        newUser.setPassword(hashedPassword);
        
        // Validate role
        if (!"ADMIN".equals(role) && !"CUSTOMER".equals(role)) {
            role = "CUSTOMER"; // Default to CUSTOMER if invalid role
        }
        newUser.setRole(role);
        
        return userDAO.save(newUser);
    }
    
    @Override
    public Optional<User> login(String email, String password) {
        return userDAO.authenticate(email, password);
    }
    
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // Verify the old password first
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }
        
        // Change to new password
        return userDAO.changePassword(userId, newPassword);
    }
    
    @Override
    public Optional<User> getUserById(Long userId) {
        return userDAO.findById(userId);
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    @Override
    public User updateUser(User user) {
        // If we're updating the password, it should already be hashed
        return userDAO.update(user);
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        return userDAO.deleteById(userId);
    }
}