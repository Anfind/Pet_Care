package com.petcare.service.impl;

import com.petcare.dao.BlogPostDAO;
import com.petcare.dao.impl.BlogPostDAOImpl;
import com.petcare.model.BlogPost;
import com.petcare.service.BlogPostService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the BlogPostService interface.
 */
public class BlogPostServiceImpl implements BlogPostService {
    
    private final BlogPostDAO blogPostDAO;
    
    public BlogPostServiceImpl() {
        this.blogPostDAO = new BlogPostDAOImpl();
    }
    
    @Override
    public BlogPost findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid blog post ID");
        }
        
        Optional<BlogPost> blogPostOpt = blogPostDAO.findById(id);
        return blogPostOpt.orElse(null);
    }
    
    @Override
    public List<BlogPost> findAll() {
        return blogPostDAO.findAll();
    }
    
    @Override
    public BlogPost create(String title, String content) {
        validateBlogPostData(title, content);
        
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(title);
        blogPost.setContent(content);
        blogPost.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        return blogPostDAO.save(blogPost);
    }
    
    @Override
    public BlogPost update(Long id, String title, String content) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid blog post ID");
        }
        
        validateBlogPostData(title, content);
        
        Optional<BlogPost> blogPostOpt = blogPostDAO.findById(id);
        if (!blogPostOpt.isPresent()) {
            return null;
        }
        
        BlogPost blogPost = blogPostOpt.get();
        blogPost.setTitle(title);
        blogPost.setContent(content);
        
        return blogPostDAO.update(blogPost);
    }
    
    @Override
    public boolean delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid blog post ID");
        }
        
        return blogPostDAO.deleteById(id);
    }
    
    @Override
    public List<BlogPost> findByDateRange(Timestamp startDate, Timestamp endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        return blogPostDAO.findByDateRange(startDate, endDate);
    }
    
    @Override
    public List<BlogPost> searchByTitleOrContent(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }
        
        return blogPostDAO.searchByTitleOrContent(searchText);
    }
    
    @Override
    public List<BlogPost> findLatest(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }
        
        return blogPostDAO.findLatest(limit);
    }
    
    /**
     * Validate blog post data for creation or update.
     */
    private void validateBlogPostData(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Blog post title cannot be empty");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Blog post content cannot be empty");
        }
        
        if (title.length() > 200) {
            throw new IllegalArgumentException("Blog post title cannot exceed 200 characters");
        }
    }
}