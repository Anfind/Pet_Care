package com.petcare.dao;

import com.petcare.model.FAQ;
import java.util.List;

/**
 * DAO interface for FAQ entity operations.
 */
public interface FAQDAO extends BaseDAO<FAQ, Long> {
    
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