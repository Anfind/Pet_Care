package com.petcare.dao;

import com.petcare.model.Booking;
import java.util.List;
import java.sql.Timestamp;

/**
 * DAO interface for Booking entity operations.
 */
public interface BookingDAO extends BaseDAO<Booking, Long> {
    
    /**
     * Find bookings by user ID.
     * 
     * @param userId The user ID
     * @return A list of bookings for the given user
     */
    List<Booking> findByUserId(Long userId);
    
    /**
     * Find bookings by service ID.
     * 
     * @param serviceId The service ID
     * @return A list of bookings for the given service
     */
    List<Booking> findByServiceId(Long serviceId);
    
    /**
     * Find bookings by their status.
     * 
     * @param status The booking status ("SCHEDULED", "COMPLETED", "CANCELLED")
     * @return A list of bookings with the given status
     */
    List<Booking> findByStatus(String status);
    
    /**
     * Find bookings scheduled within a date range.
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of bookings scheduled within the date range
     */
    List<Booking> findByDateRange(Timestamp startDate, Timestamp endDate);
    
    /**
     * Find bookings with service details loaded.
     * 
     * @param userId The user ID
     * @return A list of bookings with service details for the given user
     */
    List<Booking> findByUserIdWithService(Long userId);
    
    /**
     * Update the status of a booking.
     * 
     * @param bookingId The booking ID
     * @param status The new status
     * @return true if updated successfully, false otherwise
     */
    boolean updateStatus(Long bookingId, String status);
}