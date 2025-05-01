package com.petcare.dao;

import com.petcare.model.ContactMessage;
import java.util.List;
import java.sql.Timestamp;

/**
 * DAO interface for ContactMessage entity operations.
 */
public interface ContactMessageDAO extends BaseDAO<ContactMessage, Long> {
    
    /**
     * Find contact messages by user ID.
     * 
     * @param userId The user ID
     * @return A list of contact messages from the given user
     */
    List<ContactMessage> findByUserId(Long userId);
    
    /**
     * Find contact messages by date range.
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of contact messages within the date range
     */
    List<ContactMessage> findByDateRange(Timestamp startDate, Timestamp endDate);
    
    /**
     * Search for contact messages containing the given text in subject or message.
     * 
     * @param searchText The text to search for
     * @return A list of contact messages matching the search criteria
     */
    List<ContactMessage> searchBySubjectOrMessage(String searchText);
    
    /**
     * Find anonymous contact messages (where userId is null).
     * 
     * @return A list of anonymous contact messages
     */
    List<ContactMessage> findAnonymous();
}