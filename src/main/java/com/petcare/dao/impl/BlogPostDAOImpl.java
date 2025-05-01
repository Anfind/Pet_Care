package com.petcare.dao.impl;

import com.petcare.dao.BlogPostDAO;
import com.petcare.model.BlogPost;
import com.petcare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of BlogPostDAO interface for database operations.
 */
public class BlogPostDAOImpl implements BlogPostDAO {

    @Override
    public BlogPost save(BlogPost blogPost) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (blogPost.getId() == null) {
                // Insert new blog post
                String sql = "INSERT INTO blog_posts (title, content, created_at) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, blogPost.getTitle());
                stmt.setString(2, blogPost.getContent());
                
                // Use current timestamp if not provided
                Timestamp createdAt = blogPost.getCreatedAt();
                if (createdAt == null) {
                    createdAt = new Timestamp(System.currentTimeMillis());
                }
                stmt.setTimestamp(3, createdAt);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating blog post failed, no rows affected.");
                }
                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    blogPost.setId(rs.getLong(1));
                    blogPost.setCreatedAt(createdAt); // Set the created timestamp
                } else {
                    throw new SQLException("Creating blog post failed, no ID obtained.");
                }
            } else {
                // Update existing blog post
                return update(blogPost);
            }
            
            return blogPost;
        } catch (SQLException e) {
            System.err.println("Error saving blog post: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<BlogPost> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM blog_posts WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                BlogPost blogPost = mapBlogPostFromResultSet(rs);
                return Optional.of(blogPost);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding blog post by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<BlogPost> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BlogPost> blogPosts = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            // Use DISTINCT and ORDER BY to ensure no duplicates and consistent ordering
            String sql = "SELECT DISTINCT id, title, content, created_at FROM blog_posts ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BlogPost blogPost = mapBlogPostFromResultSet(rs);
                blogPosts.add(blogPost);
            }
            
            return blogPosts;
        } catch (SQLException e) {
            System.err.println("Error finding all blog posts: " + e.getMessage());
            return blogPosts;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM blog_posts WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting blog post by ID: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public BlogPost update(BlogPost blogPost) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE blog_posts SET title = ?, content = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, blogPost.getTitle());
            stmt.setString(2, blogPost.getContent());
            stmt.setLong(3, blogPost.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating blog post failed, no rows affected.");
            }
            
            return blogPost;
        } catch (SQLException e) {
            System.err.println("Error updating blog post: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<BlogPost> findByDateRange(Timestamp startDate, Timestamp endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BlogPost> blogPosts = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM blog_posts WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BlogPost blogPost = mapBlogPostFromResultSet(rs);
                blogPosts.add(blogPost);
            }
            
            return blogPosts;
        } catch (SQLException e) {
            System.err.println("Error finding blog posts by date range: " + e.getMessage());
            return blogPosts;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<BlogPost> searchByTitleOrContent(String searchText) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BlogPost> blogPosts = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM blog_posts WHERE title LIKE ? OR content LIKE ? ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BlogPost blogPost = mapBlogPostFromResultSet(rs);
                blogPosts.add(blogPost);
            }
            
            return blogPosts;
        } catch (SQLException e) {
            System.err.println("Error searching blog posts: " + e.getMessage());
            return blogPosts;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<BlogPost> findLatest(int limit) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BlogPost> blogPosts = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM blog_posts ORDER BY created_at DESC LIMIT ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BlogPost blogPost = mapBlogPostFromResultSet(rs);
                blogPosts.add(blogPost);
            }
            
            return blogPosts;
        } catch (SQLException e) {
            System.err.println("Error finding latest blog posts: " + e.getMessage());
            return blogPosts;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    private BlogPost mapBlogPostFromResultSet(ResultSet rs) throws SQLException {
        BlogPost blogPost = new BlogPost();
        blogPost.setId(rs.getLong("id"));
        blogPost.setTitle(rs.getString("title"));
        blogPost.setContent(rs.getString("content"));
        blogPost.setCreatedAt(rs.getTimestamp("created_at"));
        return blogPost;
    }
}