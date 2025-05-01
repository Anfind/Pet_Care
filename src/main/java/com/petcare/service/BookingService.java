package com.petcare.service;

import com.petcare.model.Booking;
import java.sql.Timestamp;
import java.util.List;

/**
 * Service interface for Booking operations.
 */
public interface BookingService {
    
    /**
     * Find a booking by its ID.
     * 
     * @param id The booking ID
     * @return The booking if found, null otherwise
     */
    Booking findById(Long id);
    
    /**
     * Get all bookings.
     * 
     * @return A list of all bookings
     */
    List<Booking> findAll();
    
    /**
     * Create a new booking.
     * 
     * @param booking The booking to create
     * @return The created booking with its generated ID
     */
    Booking create(Booking booking);
    
    /**
     * Update an existing booking.
     * 
     * @param booking The booking to update
     * @return The updated booking
     */
    Booking update(Booking booking);
    
    /**
     * Delete a booking by its ID.
     * 
     * @param id The booking ID to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean delete(Long id);
    
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
     * Cancel a booking.
     * 
     * @param id The booking ID to cancel
     * @return The updated booking with canceled status
     */
    Booking cancelBooking(Long id);
    
    /**
     * Update the status of a booking.
     * 
     * @param id The booking ID
     * @param status The new status
     * @return The updated booking
     */
    Booking updateStatus(Long id, String status);
    
    /**
     * Reschedule a booking.
     * 
     * @param id The booking ID
     * @param newTime The new scheduled time
     * @return The updated booking
     */
    Booking rescheduleBooking(Long id, Timestamp newTime);
    
    /**
     * Find completed bookings for a user with service details.
     * 
     * @param userId The user ID
     * @return A list of completed bookings with service details for the given user
     */
    List<Booking> findCompletedBookingsByUser(Long userId);
}