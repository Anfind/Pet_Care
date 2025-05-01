package com.petcare.cli;

import com.petcare.model.BlogPost;
import com.petcare.service.BlogPostService;
import com.petcare.service.impl.BlogPostServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Menu for browsing blog posts.
 */
public class BlogBrowseMenu implements Menu {
    
    private final AppSession session;
    private final BlogPostService blogPostService;
    private final SimpleDateFormat dateFormat;
    
    public BlogBrowseMenu() {
        this.session = AppSession.getInstance();
        this.blogPostService = new BlogPostServiceImpl();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public boolean display() {
        String[] options = {
            "View Latest Posts",
            "Search Posts",
            "View Post Details",
            "Browse by Date Range"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewLatestPosts();
                break;
            case 2:
                searchPosts();
                break;
            case 3:
                viewPostDetails();
                break;
            case 4:
                browseByDateRange();
                break;
        }
        
        return true;
    }
    
    private void viewLatestPosts() {
        int limit = ConsoleUtil.readInt("Enter number of posts to display (default 5): ", 1, 20);
        
        try {
            List<BlogPost> blogPosts = blogPostService.findLatest(limit);
            
            if (blogPosts.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("There are no blog posts in the system.");
                return;
            }
            
            displayBlogPostList(blogPosts, "LATEST " + limit + " BLOG POSTS");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void searchPosts() {
        String searchText = ConsoleUtil.readString("Enter search term: ");
        
        try {
            List<BlogPost> blogPosts = blogPostService.searchByTitleOrContent(searchText);
            
            if (blogPosts.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No blog posts found matching the search term.");
                return;
            }
            
            displayBlogPostList(blogPosts, "SEARCH RESULTS FOR: " + searchText);
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void viewPostDetails() {
        Long blogPostId = ConsoleUtil.readLong("Enter blog post ID (0 to cancel): ");
        if (blogPostId == 0) {
            return;
        }
        
        displayBlogPostDetails(blogPostId);
    }
    
    private void browseByDateRange() {
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
        
        try {
            List<BlogPost> blogPosts = blogPostService.findByDateRange(startTimestamp, endTimestamp);
            
            if (blogPosts.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No blog posts found in the specified date range.");
                return;
            }
            
            displayBlogPostList(blogPosts, "POSTS FROM " + inputFormat.format(startDate) + " TO " + inputFormat.format(new Date(endDate.getTime() - 24 * 60 * 60 * 1000)));
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void displayBlogPostList(List<BlogPost> blogPosts, String title) {
        ConsoleUtil.displayTitle(title);
        System.out.printf("%-5s %-30s %-20s%n", "ID", "TITLE", "DATE");
        System.out.println("-----------------------------------------------------");
        
        for (BlogPost blogPost : blogPosts) {
            System.out.printf("%-5d %-30s %-20s%n",
                    blogPost.getId(),
                    (blogPost.getTitle().length() > 27 ? blogPost.getTitle().substring(0, 24) + "..." : blogPost.getTitle()),
                    dateFormat.format(blogPost.getCreatedAt()));
        }
        
        System.out.println("-----------------------------------------------------");
        System.out.println("Total: " + blogPosts.size() + " post(s)");
        
        Long blogPostId = ConsoleUtil.readLong("\nEnter a blog post ID to view details (0 to go back): ");
        if (blogPostId > 0) {
            displayBlogPostDetails(blogPostId);
        }
    }
    
    private void displayBlogPostDetails(Long id) {
        try {
            BlogPost blogPost = blogPostService.findById(id);
            
            if (blogPost == null) {
                ConsoleUtil.pressEnterToContinue("Blog post not found.");
                return;
            }
            
            ConsoleUtil.displayTitle("BLOG POST #" + id);
            System.out.println("Title: " + blogPost.getTitle());
            System.out.println("Created at: " + dateFormat.format(blogPost.getCreatedAt()));
            System.out.println("\nContent:");
            System.out.println("-------------------------------------------");
            System.out.println(blogPost.getContent());
            System.out.println("-------------------------------------------");
            
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    @Override
    public String getTitle() {
        return "BROWSE BLOG";
    }
}