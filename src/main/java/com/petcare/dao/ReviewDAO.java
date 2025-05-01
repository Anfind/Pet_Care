package com.petcare.dao;

import com.petcare.model.Review;
import java.util.List;

/**
 * DAO interface for Review entity operations.
 */
public interface ReviewDAO extends BaseDAO<Review, Long> {
    
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
     * Delete a review by ID.
     * 
     * @param id The review ID to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Check if a user has already reviewed a specific service.
     * 
     * @param userId The user ID
     * @param serviceId The service ID
     * @return true if the user has already reviewed the service, false otherwise
     */
    boolean hasUserReviewedService(Long userId, Long serviceId);
}