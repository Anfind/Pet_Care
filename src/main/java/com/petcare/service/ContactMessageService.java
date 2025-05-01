package com.petcare.service;

import com.petcare.model.ContactMessage;

import java.util.List;

/**
 * Interface for ContactMessage service operations.
 */
public interface ContactMessageService {
    
    /**
     * Find a contact message by ID.
     *
     * @param id the ID of the contact message
     * @return the contact message if found, null otherwise
     */
    ContactMessage findById(Long id);
    
    /**
     * Find all contact messages.
     *
     * @return a list of all contact messages
     */
    List<ContactMessage> findAll();
    
    /**
     * Create a new contact message.
     *
     * @param name    the name of the sender
     * @param email   the email of the sender
     * @param subject the subject of the message
     * @param message the message content
     * @return the created contact message
     */
    ContactMessage create(String name, String email, String subject, String message);
    
    /**
     * Update a contact message.
     *
     * @param id      the ID of the contact message to update
     * @param name    the updated name
     * @param email   the updated email
     * @param subject the updated subject
     * @param message the updated message content
     * @return the updated contact message if found, null otherwise
     */
    ContactMessage update(Long id, String name, String email, String subject, String message);
    
    /**
     * Delete a contact message by ID.
     *
     * @param id the ID of the contact message to delete
     * @return true if the contact message was deleted, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Update the status of a contact message.
     *
     * @param id     the ID of the contact message
     * @param status the new status (e.g., "NEW", "READ", "REPLIED", "CLOSED")
     * @return true if the status was updated, false otherwise
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * Find contact messages by status.
     *
     * @param status the status to search for
     * @return a list of contact messages with the given status
     */
    List<ContactMessage> findByStatus(String status);
    
    /**
     * Find contact messages by email.
     *
     * @param email the email to search for
     * @return a list of contact messages from the given email
     */
    List<ContactMessage> findByEmail(String email);
}