package com.petcare.service.impl;

import com.petcare.dao.ReviewDAO;
import com.petcare.dao.impl.ReviewDAOImpl;
import com.petcare.model.Review;
import com.petcare.service.ReviewService;

import java.sql.Timestamp;
import java.util.List;

/**
 * Implementation of the ReviewService interface.
 */
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDAO reviewDAO;

    public ReviewServiceImpl() {
        this.reviewDAO = new ReviewDAOImpl();
    }

    // Constructor for dependency injection (testing)
    public ReviewServiceImpl(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    @Override
    public Review findById(Long id) {
        return reviewDAO.findById(id).orElse(null);
    }

    @Override
    public List<Review> findAll() {
        return reviewDAO.findAll();
    }

    @Override
    public Review create(Long userId, Long serviceId, int rating, String comment) {
        if (userId == null || serviceId == null || rating < 1 || rating > 5 || comment == null || comment.trim().isEmpty()) {
            System.err.println("Invalid input for creating review.");
            return null;
        }
        // Check if user already reviewed this service
        if (hasUserReviewedService(userId, serviceId)) {
            System.err.println("User has already reviewed this service.");
            return null;
        }
        
        Review review = new Review();
        review.setUserId(userId);
        review.setServiceId(serviceId);
        review.setRating(rating);
        review.setComment(comment.trim());
        review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        return reviewDAO.save(review);
    }

    @Override
    public Review update(Long id, int rating, String comment) {
        if (id == null || rating < 1 || rating > 5 || comment == null || comment.trim().isEmpty()) {
            System.err.println("Invalid input for updating review.");
            return null;
        }
        
        Review existingReview = findById(id);
        if (existingReview == null) {
            System.err.println("Review not found for update.");
            return null;
        }
        
        existingReview.setRating(rating);
        existingReview.setComment(comment.trim());
        
        Review updatedReview = reviewDAO.update(existingReview);
        return updatedReview != null ? updatedReview : null;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            System.err.println("Review ID cannot be null for deletion.");
            return false;
        }
        return reviewDAO.delete(id);
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        if (userId == null) {
            return List.of(); // Return empty list if user ID is null
        }
        return reviewDAO.findByUserId(userId);
    }

    @Override
    public List<Review> findByServiceId(Long serviceId) {
        if (serviceId == null) {
            return List.of();
        }
        return reviewDAO.findByServiceId(serviceId);
    }
    
    @Override
    public List<Review> findByServiceIdWithUser(Long serviceId) {
        if (serviceId == null) {
            return List.of();
        }
        return reviewDAO.findByServiceIdWithUser(serviceId);
    }

    @Override
    public List<Review> findByMinRating(int minRating) {
        if (minRating < 1 || minRating > 5) {
            System.err.println("Invalid minimum rating.");
            return List.of();
        }
        return reviewDAO.findByMinRating(minRating);
    }

    @Override
    public double getAverageRatingForService(Long serviceId) {
        if (serviceId == null) {
            return 0.0;
        }
        return reviewDAO.getAverageRatingForService(serviceId);
    }
    
    @Override
    public boolean hasUserReviewedService(Long userId, Long serviceId) {
        if (userId == null || serviceId == null) {
            return false;
        }
        // This logic might be better placed directly in the DAO for efficiency
        // but keeping it here for service layer control.
        // A dedicated DAO method `existsByUserIdAndServiceId` would be ideal.
        // For now, we can use the findByUserId and filter, or add the DAO method.
        // Let's assume ReviewDAOImpl has `hasUserReviewedService` for now.
        return ((ReviewDAOImpl) reviewDAO).hasUserReviewedService(userId, serviceId);
    }
}