package com.petcare.service;

import com.petcare.model.FAQ;
import java.util.List;

/**
 * Service interface for FAQ operations.
 */
public interface FAQService {
    
    /**
     * Find a FAQ by its ID.
     * 
     * @param id The FAQ ID
     * @return The FAQ if found, null otherwise
     */
    FAQ findById(Long id);
    
    /**
     * Get all FAQs.
     * 
     * @return A list of all FAQs
     */
    List<FAQ> findAll();
    
    /**
     * Create a new FAQ.
     * 
     * @param question The question
     * @param answer The answer
     * @return The created FAQ with its generated ID
     */
    FAQ create(String question, String answer);
    
    /**
     * Update an existing FAQ.
     * 
     * @param id The FAQ ID
     * @param question The new question
     * @param answer The new answer
     * @return The updated FAQ, or null if not found
     */
    FAQ update(Long id, String question, String answer);
    
    /**
     * Delete a FAQ by its ID.
     * 
     * @param id The FAQ ID to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Search for FAQs containing the given text in question or answer.
     * 
     * @param searchText The text to search for
     * @return A list of FAQs matching the search criteria
     */
    List<FAQ> searchByQuestionOrAnswer(String searchText);
    
    /**
     * Find FAQs related to a specific topic or tag.
     * This is a simplified version as we don't have a direct topic field.
     * Instead, it searches for keywords in the question.
     * 
     * @param topic The topic or keyword to search for
     * @return A list of FAQs related to the topic
     */
    List<FAQ> findByTopic(String topic);
}