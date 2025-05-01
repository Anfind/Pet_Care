package com.petcare.cli;

import com.petcare.model.Booking;
import com.petcare.service.BookingService;
import com.petcare.service.impl.BookingServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Menu for customer booking management.
 */
public class CustomerBookingsMenu implements Menu {
    
    private final AppSession session;
    private final BookingService bookingService;
    private final SimpleDateFormat dateFormat;
    
    public CustomerBookingsMenu() {
        this.session = AppSession.getInstance();
        this.bookingService = new BookingServiceImpl();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public boolean display() {
        if (!session.isLoggedIn()) {
            ConsoleUtil.pressEnterToContinue("Please log in to access this feature.");
            return true;
        }
        
        String[] options = {
            "View My Bookings",
            "Book New Service",
            "Reschedule Booking",
            "Cancel Booking"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewMyBookings();
                break;
            case 2:
                bookNewService();
                break;
            case 3:
                rescheduleBooking();
                break;
            case 4:
                cancelBooking();
                break;
        }
        
        return true;
    }
    
    private void viewMyBookings() {
        Long userId = session.getCurrentUser().getId();
        List<Booking> bookings = bookingService.findByUserId(userId);
        
        if (bookings.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You don't have any bookings yet.");
            return;
        }
        
        System.out.println("\n===== MY BOOKINGS =====");
        System.out.printf("%-5s %-20s %-15s %-10s%n", "ID", "DATE/TIME", "SERVICE ID", "STATUS");
        
        for (Booking booking : bookings) {
            System.out.printf("%-5d %-20s %-15d %-10s%n", 
                    booking.getId(), 
                    dateFormat.format(booking.getScheduledTime()), 
                    booking.getServiceId(),
                    booking.getStatus());
        }
        
        ConsoleUtil.pressEnterToContinue("\nPress Enter to continue...");
    }
    
    private void bookNewService() {
        System.out.println("\n===== BOOK NEW SERVICE =====");
        
        // Get service ID from user
        Long serviceId = ConsoleUtil.readLong("Enter service ID to book (0 to cancel): ");
        if (serviceId == 0) {
            return;
        }
        
        // Get booking time from user
        Timestamp scheduledTime = getBookingTimeInput();
        if (scheduledTime == null) {
            return; // User cancelled
        }
        
        // Confirm booking
        System.out.println("\nBooking Details:");
        System.out.println("Service ID: " + serviceId);
        System.out.println("Date/Time: " + dateFormat.format(scheduledTime));
        
        boolean confirm = ConsoleUtil.readYesNo("\nConfirm booking?");
        
        if (confirm) {
            // Create booking
            Booking newBooking = new Booking();
            newBooking.setUserId(session.getCurrentUser().getId());
            newBooking.setServiceId(serviceId);
            newBooking.setScheduledTime(scheduledTime);
            newBooking.setStatus("SCHEDULED");
            
            Booking createdBooking = bookingService.create(newBooking);
            
            if (createdBooking != null) {
                ConsoleUtil.pressEnterToContinue("Booking confirmed! Your booking ID is: " + createdBooking.getId());
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to create booking. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Booking cancelled.");
        }
    }
    
    private void rescheduleBooking() {
        Long userId = session.getCurrentUser().getId();
        List<Booking> bookings = bookingService.findByUserId(userId);
        
        if (bookings.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You don't have any bookings to reschedule.");
            return;
        }
        
        // Show only bookings that can be rescheduled (not COMPLETED or CANCELLED)
        System.out.println("\n===== MY BOOKINGS =====");
        System.out.printf("%-5s %-20s %-15s %-10s%n", "ID", "DATE/TIME", "SERVICE ID", "STATUS");
        
        int availableCount = 0;
        for (Booking booking : bookings) {
            if (!booking.getStatus().equals("COMPLETED") && !booking.getStatus().equals("CANCELLED")) {
                System.out.printf("%-5d %-20s %-15d %-10s%n", 
                        booking.getId(), 
                        dateFormat.format(booking.getScheduledTime()), 
                        booking.getServiceId(),
                        booking.getStatus());
                availableCount++;
            }
        }
        
        if (availableCount == 0) {
            ConsoleUtil.pressEnterToContinue("You don't have any bookings that can be rescheduled.");
            return;
        }
        
        Long bookingId = ConsoleUtil.readLong("\nEnter booking ID to reschedule (0 to cancel): ");
        if (bookingId == 0) {
            return;
        }
        
        // Find booking and verify it belongs to this user
        Booking booking = bookingService.findById(bookingId);
        
        if (booking == null) {
            ConsoleUtil.pressEnterToContinue("Booking not found.");
            return;
        }
        
        if (!booking.getUserId().equals(userId)) {
            ConsoleUtil.pressEnterToContinue("You don't have permission to reschedule this booking.");
            return;
        }
        
        if (booking.getStatus().equals("COMPLETED") || booking.getStatus().equals("CANCELLED")) {
            ConsoleUtil.pressEnterToContinue("This booking cannot be rescheduled because it is " + booking.getStatus() + ".");
            return;
        }
        
        System.out.println("\nCurrent Booking Details:");
        System.out.println("Service ID: " + booking.getServiceId());
        System.out.println("Current Date/Time: " + dateFormat.format(booking.getScheduledTime()));
        
        // Get new booking time
        Timestamp newScheduledTime = getBookingTimeInput();
        if (newScheduledTime == null) {
            return; // User cancelled
        }
        
        // Confirm rescheduling
        System.out.println("\nNew Date/Time: " + dateFormat.format(newScheduledTime));
        boolean confirm = ConsoleUtil.readYesNo("Confirm rescheduling?");
        
        if (confirm) {
            Booking rescheduledBooking = bookingService.rescheduleBooking(bookingId, newScheduledTime);
            
            if (rescheduledBooking != null) {
                ConsoleUtil.pressEnterToContinue("Booking rescheduled successfully.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to reschedule booking. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Rescheduling cancelled.");
        }
    }
    
    private void cancelBooking() {
        Long userId = session.getCurrentUser().getId();
        List<Booking> bookings = bookingService.findByUserId(userId);
        
        if (bookings.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You don't have any bookings to cancel.");
            return;
        }
        
        // Show only bookings that can be cancelled (not COMPLETED or CANCELLED)
        System.out.println("\n===== MY BOOKINGS =====");
        System.out.printf("%-5s %-20s %-15s %-10s%n", "ID", "DATE/TIME", "SERVICE ID", "STATUS");
        
        int availableCount = 0;
        for (Booking booking : bookings) {
            if (!booking.getStatus().equals("COMPLETED") && !booking.getStatus().equals("CANCELLED")) {
                System.out.printf("%-5d %-20s %-15d %-10s%n", 
                        booking.getId(), 
                        dateFormat.format(booking.getScheduledTime()), 
                        booking.getServiceId(),
                        booking.getStatus());
                availableCount++;
            }
        }
        
        if (availableCount == 0) {
            ConsoleUtil.pressEnterToContinue("You don't have any bookings that can be cancelled.");
            return;
        }
        
        Long bookingId = ConsoleUtil.readLong("\nEnter booking ID to cancel (0 to cancel operation): ");
        if (bookingId == 0) {
            return;
        }
        
        // Find booking and verify it belongs to this user
        Booking booking = bookingService.findById(bookingId);
        
        if (booking == null) {
            ConsoleUtil.pressEnterToContinue("Booking not found.");
            return;
        }
        
        if (!booking.getUserId().equals(userId)) {
            ConsoleUtil.pressEnterToContinue("You don't have permission to cancel this booking.");
            return;
        }
        
        if (booking.getStatus().equals("COMPLETED") || booking.getStatus().equals("CANCELLED")) {
            ConsoleUtil.pressEnterToContinue("This booking cannot be cancelled because it is already " + booking.getStatus() + ".");
            return;
        }
        
        System.out.println("\nBooking Details:");
        System.out.println("Service ID: " + booking.getServiceId());
        System.out.println("Date/Time: " + dateFormat.format(booking.getScheduledTime()));
        System.out.println("Status: " + booking.getStatus());
        
        boolean confirm = ConsoleUtil.readYesNo("Are you sure you want to cancel this booking?");
        
        if (confirm) {
            Booking cancelledBooking = bookingService.cancelBooking(bookingId);
            
            if (cancelledBooking != null) {
                ConsoleUtil.pressEnterToContinue("Booking cancelled successfully.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to cancel booking. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Operation cancelled.");
        }
    }
    
    private Timestamp getBookingTimeInput() {
        SimpleDateFormat dateInputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeInputFormat = new SimpleDateFormat("HH:mm");
        dateInputFormat.setLenient(false);
        timeInputFormat.setLenient(false);
        
        Date bookingDate = null;
        while (bookingDate == null) {
            try {
                String dateStr = ConsoleUtil.readString("Enter date (YYYY-MM-DD, 0 to cancel): ");
                if (dateStr.equals("0")) {
                    return null;
                }
                
                bookingDate = dateInputFormat.parse(dateStr);
                
                // Check if date is in the future
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                
                if (bookingDate.before(cal.getTime())) {
                    System.out.println("You cannot book for a date in the past. Please enter a future date.");
                    bookingDate = null;
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
        
        Date bookingTime = null;
        while (bookingTime == null) {
            try {
                String timeStr = ConsoleUtil.readString("Enter time (HH:MM 24-hour format, 0 to cancel): ");
                if (timeStr.equals("0")) {
                    return null;
                }
                
                bookingTime = timeInputFormat.parse(timeStr);
                
                // If booking is for today, check if time is in the future
                Calendar today = Calendar.getInstance();
                Calendar bookingCal = Calendar.getInstance();
                bookingCal.setTime(bookingDate);
                
                if (today.get(Calendar.YEAR) == bookingCal.get(Calendar.YEAR) &&
                    today.get(Calendar.MONTH) == bookingCal.get(Calendar.MONTH) &&
                    today.get(Calendar.DAY_OF_MONTH) == bookingCal.get(Calendar.DAY_OF_MONTH)) {
                    
                    Calendar timeCal = Calendar.getInstance();
                    timeCal.setTime(bookingTime);
                    
                    Calendar currentTime = Calendar.getInstance();
                    if (timeCal.get(Calendar.HOUR_OF_DAY) < currentTime.get(Calendar.HOUR_OF_DAY) ||
                        (timeCal.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY) &&
                         timeCal.get(Calendar.MINUTE) <= currentTime.get(Calendar.MINUTE))) {
                        System.out.println("You cannot book for a time in the past. Please enter a future time.");
                        bookingTime = null;
                    }
                }
                
                // Check if time is within business hours (9 AM to 6 PM)
                Calendar timeCal = Calendar.getInstance();
                timeCal.setTime(bookingTime);
                int hour = timeCal.get(Calendar.HOUR_OF_DAY);
                
                if (hour < 9 || hour >= 18) {
                    System.out.println("Bookings are only available between 9:00 AM and 6:00 PM.");
                    bookingTime = null;
                }
            } catch (ParseException e) {
                System.out.println("Invalid time format. Please use HH:MM format.");
            }
        }
        
        // Combine date and time
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(bookingDate);
        
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(bookingTime);
        
        Calendar combinedCal = Calendar.getInstance();
        combinedCal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
        combinedCal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
        combinedCal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));
        combinedCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        combinedCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        combinedCal.set(Calendar.SECOND, 0);
        combinedCal.set(Calendar.MILLISECOND, 0);
        
        return new Timestamp(combinedCal.getTimeInMillis());
    }
    
    @Override
    public String getTitle() {
        return "MY BOOKINGS";
    }
}