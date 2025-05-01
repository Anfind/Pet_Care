package com.petcare.cli;

import com.petcare.model.Review;
import com.petcare.model.Service;
import com.petcare.service.ReviewService;
import com.petcare.service.ServiceService;
import com.petcare.service.impl.ReviewServiceImpl;
import com.petcare.service.impl.ServiceServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * Menu for review management.
 */
public class ReviewManagementMenu implements Menu {
    
    private final AppSession session;
    private final ReviewService reviewService;
    private final ServiceService serviceService;
    private final SimpleDateFormat dateFormat;
    
    public ReviewManagementMenu() {
        this.session = AppSession.getInstance();
        this.reviewService = new ReviewServiceImpl();
        this.serviceService = new ServiceServiceImpl();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public boolean display() {
        if (!session.isAdmin()) {
            ConsoleUtil.pressEnterToContinue("Access denied. Admin privileges required.");
            return true;
        }
        
        String[] options = {
            "View All Reviews",
            "View Reviews by Service",
            "View Reviews by Rating",
            "View Review Details",
            "Delete Review"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewAllReviews();
                break;
            case 2:
                viewReviewsByService();
                break;
            case 3:
                viewReviewsByRating();
                break;
            case 4:
                viewReviewDetails();
                break;
            case 5:
                deleteReview();
                break;
        }
        
        return true;
    }
    
    private void viewAllReviews() {
        List<Review> reviews = reviewService.findAll();
        
        if (reviews.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no reviews in the system.");
            return;
        }
        
        displayReviewsList(reviews, "ALL REVIEWS");
    }
    
    private void viewReviewsByService() {
        // First display all services
        List<Service> services = serviceService.getAllServices();
        
        if (services.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no services in the system.");
            return;
        }
        
        System.out.println("\n===== ALL SERVICES =====");
        System.out.printf("%-5s %-30s %-10s %-10s%n", "ID", "Name", "Price", "Duration");
        System.out.println("-------------------------------------------------------");
        
        for (Service service : services) {
            System.out.printf("%-5d %-30s $%-9.2f %-10d min%n",
                    service.getId(),
                    (service.getName().length() > 28 ? service.getName().substring(0, 25) + "..." : service.getName()),
                    service.getPrice(),
                    service.getDurationMinutes());
        }
        
        Long serviceId = ConsoleUtil.readLong("\nEnter service ID to view reviews (0 to cancel): ");
        if (serviceId == 0) {
            return;
        }
        
        // Get service info
        Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
        if (!serviceOpt.isPresent()) {
            ConsoleUtil.pressEnterToContinue("Service not found.");
            return;
        }
        
        Service service = serviceOpt.get();
        
        // Get reviews for this service
        List<Review> reviews = reviewService.findByServiceIdWithUser(serviceId);
        
        if (reviews.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no reviews for service: " + service.getName());
            return;
        }
        
        double avgRating = reviewService.getAverageRatingForService(serviceId);
        
        displayReviewsList(reviews, "REVIEWS FOR SERVICE: " + service.getName() + " (Avg: " + String.format("%.1f", avgRating) + "/5)");
    }
    
    private void viewReviewsByRating() {
        int minRating = ConsoleUtil.readInt("Enter minimum rating (1-5): ", 1, 5);
        
        List<Review> reviews = reviewService.findByMinRating(minRating);
        
        if (reviews.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no reviews with rating " + minRating + " or higher.");
            return;
        }
        
        displayReviewsList(reviews, "REVIEWS WITH RATING " + minRating + "+ STARS");
    }
    
    private void viewReviewDetails() {
        Long reviewId = ConsoleUtil.readLong("Enter review ID (0 to cancel): ");
        if (reviewId == 0) {
            return;
        }
        
        Review review = reviewService.findById(reviewId);
        
        if (review == null) {
            ConsoleUtil.pressEnterToContinue("Review not found.");
            return;
        }
        
        displayReviewDetail(review);
    }
    
    private void deleteReview() {
        Long reviewId = ConsoleUtil.readLong("Enter review ID to delete (0 to cancel): ");
        if (reviewId == 0) {
            return;
        }
        
        Review review = reviewService.findById(reviewId);
        
        if (review == null) {
            ConsoleUtil.pressEnterToContinue("Review not found.");
            return;
        }
        
        // Show review details before deletion
        System.out.println("\n===== REVIEW DETAILS =====");
        System.out.println("ID: " + review.getId());
        System.out.println("User: " + (review.getUser() != null ? review.getUser().getName() : "Unknown"));
        System.out.println("Service: " + (review.getService() != null ? review.getService().getName() : "Unknown Service"));
        System.out.println("Rating: " + getStarRating(review.getRating()));
        System.out.println("Comment: " + review.getComment());
        System.out.println("Date: " + dateFormat.format(review.getCreatedAt()));
        
        boolean confirm = ConsoleUtil.readYesNo("\nAre you sure you want to delete this review?");
        
        if (confirm) {
            boolean success = reviewService.delete(reviewId);
            
            if (success) {
                ConsoleUtil.pressEnterToContinue("Review deleted successfully.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to delete review. Please try again later.");
            }
        } else {
            ConsoleUtil.pressEnterToContinue("Deletion cancelled.");
        }
    }
    
    private void displayReviewsList(List<Review> reviews, String title) {
        System.out.println("\n===== " + title + " =====");
        System.out.printf("%-5s %-20s %-20s %-10s %-30s%n", "ID", "DATE", "USER", "RATING", "SERVICE");
        System.out.println("-------------------------------------------------------------------------");
        
        for (Review review : reviews) {
            String userName = review.getUser() != null ? review.getUser().getName() : "Unknown";
            String serviceName = review.getService() != null ? review.getService().getName() : "Unknown Service";
            
            System.out.printf("%-5d %-20s %-20s %-10s %-30s%n", 
                    review.getId(), 
                    dateFormat.format(review.getCreatedAt()), 
                    userName.length() > 18 ? userName.substring(0, 15) + "..." : userName,
                    getStarRating(review.getRating()),
                    serviceName.length() > 28 ? serviceName.substring(0, 25) + "..." : serviceName);
        }
        
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Total: " + reviews.size() + " review(s)");
        
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void displayReviewDetail(Review review) {
        String userName = review.getUser() != null ? review.getUser().getName() : "Unknown";
        String serviceName = review.getService() != null ? review.getService().getName() : "Unknown Service";
        
        ConsoleUtil.displayTitle("REVIEW #" + review.getId() + " DETAILS");
        System.out.println("User: " + userName);
        System.out.println("Service: " + serviceName);
        System.out.println("Rating: " + getStarRating(review.getRating()));
        System.out.println("Date: " + dateFormat.format(review.getCreatedAt()));
        System.out.println("\nComment:");
        System.out.println(review.getComment());
        
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
        return "REVIEW MANAGEMENT";
    }
}