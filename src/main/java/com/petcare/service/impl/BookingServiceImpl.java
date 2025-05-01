package com.petcare.service.impl;

import com.petcare.dao.BookingDAO;
import com.petcare.dao.impl.BookingDAOImpl;
import com.petcare.model.Booking;
import com.petcare.service.BookingService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the BookingService interface.
 */
public class BookingServiceImpl implements BookingService {
    
    private final BookingDAO bookingDAO;
    
    public BookingServiceImpl() {
        this.bookingDAO = new BookingDAOImpl();
    }
    
    @Override
    public Booking findById(Long id) {
        Optional<Booking> bookingOpt = bookingDAO.findById(id);
        return bookingOpt.orElse(null);
    }
    
    @Override
    public List<Booking> findAll() {
        return bookingDAO.findAll();
    }
    
    @Override
    public Booking create(Booking booking) {
        return bookingDAO.save(booking);
    }
    
    @Override
    public Booking update(Booking booking) {
        return bookingDAO.update(booking);
    }
    
    @Override
    public boolean delete(Long id) {
        return bookingDAO.deleteById(id);
    }
    
    @Override
    public List<Booking> findByUserId(Long userId) {
        return bookingDAO.findByUserId(userId);
    }
    
    @Override
    public List<Booking> findByServiceId(Long serviceId) {
        return bookingDAO.findByServiceId(serviceId);
    }
    
    @Override
    public List<Booking> findByStatus(String status) {
        return bookingDAO.findByStatus(status);
    }
    
    @Override
    public List<Booking> findByDateRange(Timestamp startDate, Timestamp endDate) {
        return bookingDAO.findByDateRange(startDate, endDate);
    }
    
    @Override
    public Booking cancelBooking(Long id) {
        Optional<Booking> bookingOpt = bookingDAO.findById(id);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Only allow cancellation if not already completed or cancelled
            if (!booking.getStatus().equals("COMPLETED") && !booking.getStatus().equals("CANCELLED")) {
                booking.setStatus("CANCELLED");
                return bookingDAO.update(booking);
            }
        }
        
        return null;
    }
    
    @Override
    public Booking updateStatus(Long id, String status) {
        Optional<Booking> bookingOpt = bookingDAO.findById(id);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Don't update if status is the same
            if (!booking.getStatus().equals(status)) {
                booking.setStatus(status);
                return bookingDAO.update(booking);
            } else {
                return booking; // Return existing booking if status is the same
            }
        }
        
        return null;
    }
    
    @Override
    public Booking rescheduleBooking(Long id, Timestamp newTime) {
        Optional<Booking> bookingOpt = bookingDAO.findById(id);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Only allow rescheduling if not completed or cancelled
            if (!booking.getStatus().equals("COMPLETED") && !booking.getStatus().equals("CANCELLED")) {
                booking.setScheduledTime(newTime);
                return bookingDAO.update(booking);
            }
        }
        
        return null;
    }

    @Override
    public List<Booking> findCompletedBookingsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        List<Booking> allUserBookings = findByUserIdWithService(userId);
        List<Booking> completedBookings = new ArrayList<>();
        
        for (Booking booking : allUserBookings) {
            if ("COMPLETED".equals(booking.getStatus())) {
                completedBookings.add(booking);
            }
        }
        
        return completedBookings;
    }

    /**
     * Finds bookings by user ID with associated service details.
     * 
     * @param userId the user ID
     * @return a list of bookings with service details
     */
    private List<Booking> findByUserIdWithService(Long userId) {
        // Assuming this method fetches bookings by user ID and includes service details.
        // Replace this with actual implementation as per your application's requirements.
        return bookingDAO.findByUserId(userId);
    }
}