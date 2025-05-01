package com.petcare.cli;

import com.petcare.model.Booking;
import com.petcare.service.BookingService;
import com.petcare.service.impl.BookingServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Menu for booking management.
 */
public class BookingManagementMenu implements Menu {
    
    private final AppSession session;
    private final BookingService bookingService;
    private final SimpleDateFormat dateFormat;
    
    public BookingManagementMenu() {
        this.session = AppSession.getInstance();
        this.bookingService = new BookingServiceImpl();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public boolean display() {
        if (!session.isAdmin()) {
            ConsoleUtil.pressEnterToContinue("Access denied. Admin privileges required.");
            return true;
        }
        
        String[] options = {
            "View All Bookings",
            "View Bookings by Status",
            "View Bookings by Date Range",
            "View Booking Details",
            "Update Booking Status",
            "Cancel Booking"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewAllBookings();
                break;
            case 2:
                viewBookingsByStatus();
                break;
            case 3:
                viewBookingsByDateRange();
                break;
            case 4:
                viewBookingDetails();
                break;
            case 5:
                updateBookingStatus();
                break;
            case 6:
                cancelBooking();
                break;
        }
        
        return true;
    }
    
    private void viewAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        
        if (bookings.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no bookings in the system.");
            return;
        }
        
        displayBookingList(bookings, "ALL BOOKINGS");
    }
    
    private void viewBookingsByStatus() {
        String[] statuses = {"SCHEDULED", "COMPLETED", "CANCELLED"};
        
        System.out.println("\nBooking statuses:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }
        
        int statusChoice = ConsoleUtil.readInt("\nSelect status (1-" + statuses.length + "): ", 1, statuses.length);
        String selectedStatus = statuses[statusChoice - 1];
        
        List<Booking> bookings = bookingService.findByStatus(selectedStatus);
        
        if (bookings.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no bookings with status '" + selectedStatus + "'.");
            return;
        }
        
        displayBookingList(bookings, "BOOKINGS WITH STATUS: " + selectedStatus);
    }
    
    private void viewBookingsByDateRange() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat.setLenient(false);
        
        Date startDate = null;
        while (startDate == null) {
            try {
                String startDateStr = ConsoleUtil.readString("Enter start date (YYYY-MM-DD): ");
                startDate = inputFormat.parse(startDateStr);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
        
        Date endDate = null;
        while (endDate == null) {
            try {
                String endDateStr = ConsoleUtil.readString("Enter end date (YYYY-MM-DD): ");
                endDate = inputFormat.parse(endDateStr);
                
                // Add one day to include the end date fully
                endDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000);
                
                if (endDate.before(startDate)) {
                    System.out.println("End date must be after start date.");
                    endDate = null;
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
        
        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime());
        
        List<Booking> bookings = bookingService.findByDateRange(startTimestamp, endTimestamp);
        
        if (bookings.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no bookings in the specified date range.");
            return;
        }
        
        displayBookingList(bookings, "BOOKINGS BETWEEN " + inputFormat.format(startDate) + " AND " + inputFormat.format(new Date(endDate.getTime() - 24 * 60 * 60 * 1000)));
    }
    
    private void displayBookingList(List<Booking> bookings, String title) {
        System.out.println("\n===== " + title + " =====");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s%n", "ID", "DATE/TIME", "USER ID", "SERVICE ID", "STATUS");
        
        for (Booking booking : bookings) {
            System.out.printf("%-5d %-20s %-15d %-15d %-10s%n", 
                    booking.getId(), 
                    dateFormat.format(booking.getScheduledTime()), 
                    booking.getUserId(),
                    booking.getServiceId(),
                    booking.getStatus());
        }
        
        ConsoleUtil.pressEnterToContinue("\nPress Enter to continue...");
    }
    
    private void viewBookingDetails() {
        Long bookingId = ConsoleUtil.readLong("Enter booking ID (0 to cancel): ");
        if (bookingId == 0) {
            return;
        }
        
        Booking booking = bookingService.findById(bookingId);
        
        if (booking == null) {
            ConsoleUtil.pressEnterToContinue("Booking not found.");
            return;
        }
        
        ConsoleUtil.displayTitle("BOOKING #" + booking.getId() + " DETAILS");
        System.out.println("User ID: " + booking.getUserId());
        System.out.println("Service ID: " + booking.getServiceId());
        System.out.println("Scheduled Time: " + dateFormat.format(booking.getScheduledTime()));
        System.out.println("Status: " + booking.getStatus());
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void updateBookingStatus() {
        Long bookingId = ConsoleUtil.readLong("Enter booking ID (0 to cancel): ");
        if (bookingId == 0) {
            return;
        }
        
        Booking booking = bookingService.findById(bookingId);
        
        if (booking == null) {
            ConsoleUtil.pressEnterToContinue("Booking not found.");
            return;
        }
        
        System.out.println("\n===== BOOKING #" + booking.getId() + " =====");
        System.out.println("User ID: " + booking.getUserId());
        System.out.println("Service ID: " + booking.getServiceId());
        System.out.println("Scheduled Time: " + dateFormat.format(booking.getScheduledTime()));
        System.out.println("Current Status: " + booking.getStatus());
        
        // Show status options
        String[] statuses = {"SCHEDULED", "COMPLETED", "CANCELLED"};
        
        System.out.println("\nAvailable statuses:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }
        
        int statusChoice = ConsoleUtil.readInt("\nSelect new status (1-" + statuses.length + ", 0 to cancel): ", 0, statuses.length);
        
        if (statusChoice == 0) {
            return;
        }
        
        String newStatus = statuses[statusChoice - 1];
        
        // Skip if the selected status is the same as the current status
        if (newStatus.equals(booking.getStatus())) {
            ConsoleUtil.pressEnterToContinue("The booking already has status '" + newStatus + "'.");
            return;
        }
        
        // Confirm update
        boolean confirm = ConsoleUtil.readYesNo("Update status from '" + booking.getStatus() + "' to '" + newStatus + "'?");
        
        if (confirm) {
            Booking updatedBooking = bookingService.updateStatus(bookingId, newStatus);
            
            if (updatedBooking != null) {
                ConsoleUtil.pressEnterToContinue("Booking status updated successfully.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to update booking status. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Operation cancelled.");
        }
    }
    
    private void cancelBooking() {
        Long bookingId = ConsoleUtil.readLong("Enter booking ID to cancel (0 to cancel operation): ");
        if (bookingId == 0) {
            return;
        }
        
        Booking booking = bookingService.findById(bookingId);
        
        if (booking == null) {
            ConsoleUtil.pressEnterToContinue("Booking not found.");
            return;
        }
        
        // Check if booking can be cancelled
        if (booking.getStatus().equals("COMPLETED") || booking.getStatus().equals("CANCELLED")) {
            ConsoleUtil.pressEnterToContinue("This booking cannot be cancelled because it is already " + booking.getStatus() + ".");
            return;
        }
        
        System.out.println("\n===== BOOKING #" + booking.getId() + " =====");
        System.out.println("User ID: " + booking.getUserId());
        System.out.println("Service ID: " + booking.getServiceId());
        System.out.println("Scheduled Time: " + dateFormat.format(booking.getScheduledTime()));
        System.out.println("Status: " + booking.getStatus());
        
        // Confirm cancellation
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
    
    @Override
    public String getTitle() {
        return "BOOKING MANAGEMENT";
    }
}