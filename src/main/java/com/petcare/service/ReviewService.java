package com.petcare.service;

import com.petcare.model.Review;
import java.util.List;

/**
 * Service interface for Review operations.
 */
public interface ReviewService {
    
    /**
     * Find a review by its ID.
     * 
     * @param id The review ID
     * @return The review if found, null otherwise
     */
    Review findById(Long id);
    
    /**
     * Get all reviews.
     * 
     * @return A list of all reviews
     */
    List<Review> findAll();
    
    /**
     * Create a new review.
     * 
     * @param userId The user ID
     * @param serviceId The service ID
     * @param rating The rating (1-5)
     * @param comment The review comment
     * @return The created review with its generated ID
     */
    Review create(Long userId, Long serviceId, int rating, String comment);
    
    /**
     * Update an existing review.
     * 
     * @param id The review ID
     * @param rating The new rating
     * @param comment The new comment
     * @return The updated review, or null if not found
     */
    Review update(Long id, int rating, String comment);
    
    /**
     * Delete a review by its ID.
     * 
     * @param id The review ID to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Find reviews by user ID.
     * 
     * @param userId The user ID
     * @return A list of reviews by the given user
     */
    List<Review> findByUserId(Long userId);
    
    /**
     * Find reviews by service ID.
     * 
     * @param serviceId The service ID
     * @return A list of reviews for the given service
     */
    List<Review> findByServiceId(Long serviceId);
    
    /**
     * Find reviews by minimum rating.
     * 
     * @param minRating The minimum rating (inclusive)
     * @return A list of reviews with rating greater than or equal to minRating
     */
    List<Review> findByMinRating(int minRating);
    
    /**
     * Calculate average rating for a service.
     * 
     * @param serviceId The service ID
     * @return The average rating for the service
     */
    double getAverageRatingForService(Long serviceId);
    
    /**
     * Find reviews by service ID with user details loaded.
     * 
     * @param serviceId The service ID
     * @return A list of reviews with user details for the given service
     */
    List<Review> findByServiceIdWithUser(Long serviceId);
    
    /**
     * Check if a user has already reviewed a service.
     * 
     * @param userId The user ID
     * @param serviceId The service ID
     * @return true if the user has already reviewed the service, false otherwise
     */
    boolean hasUserReviewedService(Long userId, Long serviceId);
}