package com.petcare.cli;

import com.petcare.model.Booking;
import com.petcare.model.Review;
import com.petcare.model.Service;
import com.petcare.service.BookingService;
import com.petcare.service.ReviewService;
import com.petcare.service.ServiceService;
import com.petcare.service.impl.BookingServiceImpl;
import com.petcare.service.impl.ReviewServiceImpl;
import com.petcare.service.impl.ServiceServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * Menu for customer review management.
 */
public class CustomerReviewsMenu implements Menu {
    
    private final AppSession session;
    private final ReviewService reviewService;
    private final ServiceService serviceService;
    private final BookingService bookingService;
    private final SimpleDateFormat dateFormat;
    
    public CustomerReviewsMenu() {
        this.session = AppSession.getInstance();
        this.reviewService = new ReviewServiceImpl();
        this.serviceService = new ServiceServiceImpl();
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
            "View My Reviews",
            "Write New Review",
            "Edit My Review",
            "Delete My Review"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewMyReviews();
                break;
            case 2:
                writeNewReview();
                break;
            case 3:
                editMyReview();
                break;
            case 4:
                deleteMyReview();
                break;
        }
        
        return true;
    }
    
    private void viewMyReviews() {
        Long userId = session.getCurrentUser().getId();
        List<Review> reviews = reviewService.findByUserId(userId);
        
        if (reviews.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You haven't written any reviews yet.");
            return;
        }
        
        displayReviewsList(reviews, "MY REVIEWS");
    }
    
    private void writeNewReview() {
        Long userId = session.getCurrentUser().getId();
        
        // Get completed bookings for this user to determine which services they can review
        List<Booking> completedBookings = bookingService.findCompletedBookingsByUser(userId);
        
        if (completedBookings.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You need to complete a service booking before you can write a review.");
            return;
        }
        
        // Show completed bookings that can be reviewed
        System.out.println("\n===== SERVICES YOU CAN REVIEW =====");
        System.out.printf("%-5s %-30s %-20s%n", "ID", "SERVICE NAME", "BOOKING DATE");
        System.out.println("-------------------------------------------------------");
        
        for (Booking booking : completedBookings) {
            Service service = booking.getService();
            if (service != null) {
                // Check if user already reviewed this service
                if (!reviewService.hasUserReviewedService(userId, service.getId())) {
                    System.out.printf("%-5d %-30s %-20s%n", 
                            service.getId(), 
                            (service.getName().length() > 28 ? service.getName().substring(0, 25) + "..." : service.getName()),
                            dateFormat.format(booking.getScheduledTime()));
                }
            }
        }
        
        System.out.println("-------------------------------------------------------");
        
        Long serviceId = ConsoleUtil.readLong("\nEnter service ID to review (0 to cancel): ");
        if (serviceId == 0) {
            return;
        }
        
        // Verify that user has used this service and hasn't already reviewed it
        boolean canReview = false;
        for (Booking booking : completedBookings) {
            if (booking.getServiceId().equals(serviceId)) {
                if (!reviewService.hasUserReviewedService(userId, serviceId)) {
                    canReview = true;
                    break;
                } else {
                    ConsoleUtil.pressEnterToContinue("You have already reviewed this service.");
                    return;
                }
            }
        }
        
        if (!canReview) {
            ConsoleUtil.pressEnterToContinue("You cannot review this service, either because you haven't used it or it doesn't exist.");
            return;
        }
        
        // Get service details
        Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
        if (!serviceOpt.isPresent()) {
            ConsoleUtil.pressEnterToContinue("Service not found.");
            return;
        }
        
        Service service = serviceOpt.get();
        
        System.out.println("\n===== WRITE REVIEW FOR: " + service.getName() + " =====");
        
        // Get rating
        int rating = ConsoleUtil.readInt("Rating (1-5 stars): ", 1, 5);
        
        // Get comment
        String comment = ConsoleUtil.readString("Your review (press Enter when done):");
        
        // Confirm submission
        boolean confirm = ConsoleUtil.readYesNo("Submit this review?");
        
        if (confirm) {
            Review newReview = reviewService.create(userId, serviceId, rating, comment);
            
            if (newReview != null) {
                ConsoleUtil.pressEnterToContinue("Thank you! Your review has been submitted.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to submit review. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Review cancelled.");
        }
    }
    
    private void editMyReview() {
        Long userId = session.getCurrentUser().getId();
        List<Review> reviews = reviewService.findByUserId(userId);
        
        if (reviews.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You haven't written any reviews yet.");
            return;
        }
        
        // Show the user's reviews
        System.out.println("\n===== YOUR REVIEWS =====");
        System.out.printf("%-5s %-30s %-10s %-20s%n", "ID", "SERVICE", "RATING", "DATE");
        System.out.println("-------------------------------------------------------");
        
        for (Review review : reviews) {
            String serviceName = review.getService() != null ? review.getService().getName() : "Unknown Service";
            
            System.out.printf("%-5d %-30s %-10s %-20s%n", 
                    review.getId(), 
                    (serviceName.length() > 28 ? serviceName.substring(0, 25) + "..." : serviceName),
                    getStarRating(review.getRating()),
                    dateFormat.format(review.getCreatedAt()));
        }
        
        System.out.println("-------------------------------------------------------");
        
        Long reviewId = ConsoleUtil.readLong("\nEnter review ID to edit (0 to cancel): ");
        if (reviewId == 0) {
            return;
        }
        
        // Find the review in the list
        Review reviewToEdit = null;
        for (Review review : reviews) {
            if (review.getId().equals(reviewId)) {
                reviewToEdit = review;
                break;
            }
        }
        
        if (reviewToEdit == null) {
            ConsoleUtil.pressEnterToContinue("Review not found or you don't have permission to edit it.");
            return;
        }
        
        String serviceName = reviewToEdit.getService() != null ? reviewToEdit.getService().getName() : "Unknown Service";
        
        System.out.println("\n===== EDIT REVIEW FOR: " + serviceName + " =====");
        System.out.println("Current rating: " + getStarRating(reviewToEdit.getRating()));
        System.out.println("Current comment: " + reviewToEdit.getComment());
        
        // Get new rating
        int newRating = ConsoleUtil.readInt("\nNew rating (1-5 stars): ", 1, 5);
        
        // Get new comment
        String newComment = ConsoleUtil.readString("New review (press Enter when done):");
        
        // Confirm update
        boolean confirm = ConsoleUtil.readYesNo("Update this review?");
        
        if (confirm) {
            Review updatedReview = reviewService.update(reviewId, newRating, newComment);
            
            if (updatedReview != null) {
                ConsoleUtil.pressEnterToContinue("Your review has been updated.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to update review. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Update cancelled.");
        }
    }
    
    private void deleteMyReview() {
        Long userId = session.getCurrentUser().getId();
        List<Review> reviews = reviewService.findByUserId(userId);
        
        if (reviews.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("You haven't written any reviews yet.");
            return;
        }
        
        // Show the user's reviews
        System.out.println("\n===== YOUR REVIEWS =====");
        System.out.printf("%-5s %-30s %-10s %-20s%n", "ID", "SERVICE", "RATING", "DATE");
        System.out.println("-------------------------------------------------------");
        
        for (Review review : reviews) {
            String serviceName = review.getService() != null ? review.getService().getName() : "Unknown Service";
            
            System.out.printf("%-5d %-30s %-10s %-20s%n", 
                    review.getId(), 
                    (serviceName.length() > 28 ? serviceName.substring(0, 25) + "..." : serviceName),
                    getStarRating(review.getRating()),
                    dateFormat.format(review.getCreatedAt()));
        }
        
        System.out.println("-------------------------------------------------------");
        
        Long reviewId = ConsoleUtil.readLong("\nEnter review ID to delete (0 to cancel): ");
        if (reviewId == 0) {
            return;
        }
        
        // Find the review in the list
        Review reviewToDelete = null;
        for (Review review : reviews) {
            if (review.getId().equals(reviewId)) {
                reviewToDelete = review;
                break;
            }
        }
        
        if (reviewToDelete == null) {
            ConsoleUtil.pressEnterToContinue("Review not found or you don't have permission to delete it.");
            return;
        }
        
        String serviceName = reviewToDelete.getService() != null ? reviewToDelete.getService().getName() : "Unknown Service";
        
        // Confirm deletion
        System.out.println("\n===== REVIEW DETAILS =====");
        System.out.println("Service: " + serviceName);
        System.out.println("Rating: " + getStarRating(reviewToDelete.getRating()));
        System.out.println("Comment: " + reviewToDelete.getComment());
        System.out.println("Date: " + dateFormat.format(reviewToDelete.getCreatedAt()));
        
        boolean confirm = ConsoleUtil.readYesNo("\nAre you sure you want to delete this review?");
        
        if (confirm) {
            boolean success = reviewService.delete(reviewId);
            
            if (success) {
                ConsoleUtil.pressEnterToContinue("Your review has been deleted.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to delete review. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Deletion cancelled.");
        }
    }
    
    private void displayReviewsList(List<Review> reviews, String title) {
        System.out.println("\n===== " + title + " =====");
        System.out.printf("%-5s %-20s %-30s %-15s%n", "ID", "DATE", "SERVICE", "RATING");
        System.out.println("-------------------------------------------------------------------------");
        
        for (Review review : reviews) {
            String serviceName = review.getService() != null ? review.getService().getName() : "Unknown Service";
            
            System.out.printf("%-5d %-20s %-30s %-15s%n", 
                    review.getId(), 
                    dateFormat.format(review.getCreatedAt()), 
                    (serviceName.length() > 28 ? serviceName.substring(0, 25) + "..." : serviceName),
                    getStarRating(review.getRating()));
            
            // Show a preview of the comment
            String comment = review.getComment();
            if (comment.length() > 80) {
                comment = comment.substring(0, 77) + "...";
            }
            System.out.println("Comment: " + comment);
            System.out.println();
        }
        
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Total: " + reviews.size() + " review(s)");
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private String getStarRating(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString() + " (" + rating + "/5)";
    }
    
    @Override
    public String getTitle() {
        return "MY REVIEWS";
    }
}