package com.petcare.dao;

import com.petcare.model.BlogPost;
import java.util.List;
import java.sql.Timestamp;

/**
 * DAO interface for BlogPost entity operations.
 */
public interface BlogPostDAO extends BaseDAO<BlogPost, Long> {
    
    /**
     * Find blog posts by created date range.
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
