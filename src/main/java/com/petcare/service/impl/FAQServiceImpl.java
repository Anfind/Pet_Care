package com.petcare.service.impl;

import com.petcare.dao.FAQDAO;
import com.petcare.dao.impl.FAQDAOImpl;
import com.petcare.model.FAQ;
import com.petcare.service.FAQService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the FAQService interface.
 */
public class FAQServiceImpl implements FAQService {
    
    private final FAQDAO faqDAO;
    
    public FAQServiceImpl() {
        this.faqDAO = new FAQDAOImpl();
    }
    
    @Override
    public FAQ findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid FAQ ID");
        }
        
        Optional<FAQ> faqOpt = faqDAO.findById(id);
        return faqOpt.orElse(null);
    }
    
    @Override
    public List<FAQ> findAll() {
        return faqDAO.findAll();
    }
    
    @Override
    public FAQ create(String question, String answer) {
        validateFAQData(question, answer);
        
        FAQ faq = new FAQ();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        
        return faqDAO.save(faq);
    }
    
    @Override
    public FAQ update(Long id, String question, String answer) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid FAQ ID");
        }
        
        validateFAQData(question, answer);
        
        Optional<FAQ> faqOpt = faqDAO.findById(id);
        if (!faqOpt.isPresent()) {
            return null;
        }
        
        FAQ faq = faqOpt.get();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        
        return faqDAO.update(faq);
    }
    
    @Override
    public boolean delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid FAQ ID");
        }
        
        return faqDAO.deleteById(id);
    }
    
    @Override
    public List<FAQ> searchByQuestionOrAnswer(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }
        
        return faqDAO.searchByQuestionOrAnswer(searchText.trim());
    }
    
    @Override
    public List<FAQ> findByTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        
        return faqDAO.findByTopic(topic.trim());
    }
    
    /**
     * Validate FAQ data.
     */
    private void validateFAQData(String question, String answer) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ question cannot be empty");
        }
        
        if (question.length() > 255) {
            throw new IllegalArgumentException("FAQ question cannot exceed 255 characters");
        }
        
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ answer cannot be empty");
        }
        
        if (answer.length() > 1000) {
            throw new IllegalArgumentException("FAQ answer cannot exceed 1000 characters");
        }
    }
}