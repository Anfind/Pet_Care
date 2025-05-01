package com.petcare.cli;

import com.petcare.model.BlogPost;
import com.petcare.service.BlogPostService;
import com.petcare.service.impl.BlogPostServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * Menu for blog post management.
 */
public class BlogManagementMenu implements Menu {
    
    private final AppSession session;
    private final BlogPostService blogPostService;
    private final SimpleDateFormat dateFormat;
    private final Scanner scanner;
    
    public BlogManagementMenu() {
        this.session = AppSession.getInstance();
        this.blogPostService = new BlogPostServiceImpl();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public boolean display() {
        if (!session.isAdmin()) {
            ConsoleUtil.pressEnterToContinue("Access denied. Admin privileges required.");
            return true;
        }
        
        String[] options = {
            "View All Blog Posts",
            "Create New Blog Post",
            "Edit Blog Post",
            "Delete Blog Post",
            "Search Blog Posts"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                viewAllBlogPosts();
                break;
            case 2:
                createNewBlogPost();
                break;
            case 3:
                editBlogPost();
                break;
            case 4:
                deleteBlogPost();
                break;
            case 5:
                searchBlogPosts();
                break;
        }
        
        return true;
    }
    
    private void viewAllBlogPosts() {
        List<BlogPost> blogPosts = blogPostService.findAll();
        
        if (blogPosts.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("There are no blog posts in the system.");
            return;
        }
        
        displayBlogPostList(blogPosts, "ALL BLOG POSTS");
    }
    
    private void createNewBlogPost() {
        ConsoleUtil.displayTitle("CREATE NEW BLOG POST");
        
        String title = ConsoleUtil.readString("Enter blog post title: ");
        
        System.out.println("Enter blog post content (enter an empty line to finish):");
        StringBuilder contentBuilder = new StringBuilder();
        String line;
        
        // Read multiple lines until an empty line is entered
        scanner.nextLine(); // Clear any pending newline
        while (true) {
            line = scanner.nextLine();
            if (line.trim().isEmpty() && contentBuilder.length() > 0) {
                break;
            }
            contentBuilder.append(line).append("\n");
        }
        
        String content = contentBuilder.toString().trim();
        
        if (content.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("Blog post creation cancelled. Content cannot be empty.");
            return;
        }
        
        try {
            BlogPost blogPost = blogPostService.create(title, content);
            
            if (blogPost != null) {
                ConsoleUtil.pressEnterToContinue("Blog post created successfully with ID: " + blogPost.getId());
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to create blog post. Please try again later.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void editBlogPost() {
        Long blogPostId = ConsoleUtil.readLong("Enter blog post ID to edit (0 to cancel): ");
        if (blogPostId == 0) {
            return;
        }
        
        try {
            BlogPost blogPost = blogPostService.findById(blogPostId);
            
            if (blogPost == null) {
                ConsoleUtil.pressEnterToContinue("Blog post not found.");
                return;
            }
            
            ConsoleUtil.displayTitle("EDIT BLOG POST #" + blogPostId);
            System.out.println("Current title: " + blogPost.getTitle());
            System.out.println("Current content:\n" + blogPost.getContent());
            System.out.println("Created at: " + dateFormat.format(blogPost.getCreatedAt()));
            
            System.out.println("\nEnter new title (press Enter to keep current):");
            String titleInput = scanner.nextLine().trim();
            String title = titleInput.isEmpty() ? blogPost.getTitle() : titleInput;
            
            System.out.println("\nEnter new content (press Enter on an empty line to finish, enter '.' to keep current):");
            String contentInput = scanner.nextLine();
            
            String content;
            if (contentInput.equals(".")) {
                content = blogPost.getContent();
            } else {
                StringBuilder contentBuilder = new StringBuilder();
                if (!contentInput.isEmpty()) {
                    contentBuilder.append(contentInput).append("\n");
                }
                
                // Read multiple lines until an empty line is entered
                while (true) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) {
                        break;
                    }
                    contentBuilder.append(line).append("\n");
                }
                
                content = contentBuilder.toString().trim();
                if (content.isEmpty()) {
                    content = blogPost.getContent();
                }
            }
            
            boolean confirm = ConsoleUtil.readYesNo("Confirm update of blog post #" + blogPostId + "?");
            
            if (confirm) {
                BlogPost updatedBlogPost = blogPostService.update(blogPostId, title, content);
                
                if (updatedBlogPost != null) {
                    ConsoleUtil.pressEnterToContinue("Blog post updated successfully.");
                } else {
                    ConsoleUtil.pressEnterToContinue("Failed to update blog post. Please try again later.");
                }
            } else {
                ConsoleUtil.pressEnterToContinue("Blog post update cancelled.");
            }
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void deleteBlogPost() {
        Long blogPostId = ConsoleUtil.readLong("Enter blog post ID to delete (0 to cancel): ");
        if (blogPostId == 0) {
            return;
        }
        
        try {
            BlogPost blogPost = blogPostService.findById(blogPostId);
            
            if (blogPost == null) {
                ConsoleUtil.pressEnterToContinue("Blog post not found.");
                return;
            }
            
            ConsoleUtil.displayTitle("DELETE BLOG POST #" + blogPostId);
            System.out.println("Title: " + blogPost.getTitle());
            System.out.println("Created at: " + dateFormat.format(blogPost.getCreatedAt()));
            
            boolean confirm = ConsoleUtil.readYesNo("Are you sure you want to delete this blog post?");
            
            if (confirm) {
                boolean deleted = blogPostService.delete(blogPostId);
                
                if (deleted) {
                    ConsoleUtil.pressEnterToContinue("Blog post deleted successfully.");
                } else {
                    ConsoleUtil.pressEnterToContinue("Failed to delete blog post. Please try again later.");
                }
            } else {
                ConsoleUtil.pressEnterToContinue("Blog post deletion cancelled.");
            }
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void searchBlogPosts() {
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
        return "BLOG MANAGEMENT";
    }
}