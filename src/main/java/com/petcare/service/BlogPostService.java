package com.petcare.service;

import com.petcare.model.BlogPost;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for BlogPost operations.
 */
public interface BlogPostService {
    
    /**
     * Find a blog post by its ID.
     * 
     * @param id The blog post ID
     * @return The blog post if found, null otherwise
     */
    BlogPost findById(Long id);
    
    /**
     * Get all blog posts.
     * 
     * @return A list of all blog posts
     */
    List<BlogPost> findAll();
    
    /**
     * Create a new blog post.
     * 
     * @param title The blog post title
     * @param content The blog post content
     * @return The created blog post with its generated ID
     */
    BlogPost create(String title, String content);
    
    /**
     * Update an existing blog post.
     * 
     * @param id The blog post ID
     * @param title The new title
     * @param content The new content
     * @return The updated blog post, or null if not found
     */
    BlogPost update(Long id, String title, String content);
    
    /**
     * Delete a blog post by its ID.
     * 
     * @param id The blog post ID to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Find blog posts created within a date range.
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of blog posts created within the date range
     */
    List<BlogPost> findByDateRange(Timestamp startDate, Timestamp endDate);
    
    /**
     * Search for blog posts containing the given text in title or content.
     * 
     * @param searchText The text to search for
     * @return A list of blog posts matching the search criteria
     */
    List<BlogPost> searchByTitleOrContent(String searchText);
    
    /**
     * Find latest blog posts with limit.
     * 
     * @param limit The maximum number of posts to return
     * @return A list of the latest blog posts
     */
    List<BlogPost> findLatest(int limit);
}