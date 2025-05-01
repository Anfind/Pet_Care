package com.petcare.dao;

import com.petcare.model.Message;
import java.util.List;
import java.sql.Timestamp;

/**
 * DAO interface for Message entity operations.
 */
public interface MessageDAO extends BaseDAO<Message, Long> {
    
    /**
     * Find messages by user ID.
     * 
     * @param userId The user ID
     * @return A list of messages for the given user
     */
    List<Message> findByUserId(Long userId);
    
    /**
     * Find messages by admin ID.
     * 
     * @param adminId The admin ID
     * @return A list of messages for the given admin
     */
    List<Message> findByAdminId(Long adminId);
    
    /**
     * Find conversation history between a user and admin.
     * 
     * @param userId The user ID
     * @param adminId The admin ID
     * @return A list of messages between the user and admin
     */
    List<Message> findConversation(Long userId, Long adminId);
    
    /**
     * Find messages by date range.
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of messages within the date range
     */
    List<Message> findByDateRange(Timestamp startDate, Timestamp endDate);
    
    /**
     * Find latest messages for a user.
     * 
     * @param userId The user ID
     * @param limit The maximum number of messages to return
     * @return A list of the latest messages for the user
     */
    List<Message> findLatestForUser(Long userId, int limit);
}