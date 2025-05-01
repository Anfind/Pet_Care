package com.petcare.dao.impl;

import com.petcare.dao.ReviewDAO;
import com.petcare.model.Review;
import com.petcare.model.Service;
import com.petcare.model.User;
import com.petcare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ReviewDAO interface for database operations.
 */
public class ReviewDAOImpl implements ReviewDAO {

    @Override
    public Review save(Review review) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO reviews (user_id, service_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, review.getUserId());
            stmt.setLong(2, review.getServiceId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.setTimestamp(5, review.getCreatedAt());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                review.setId(rs.getLong(1));
            } else {
                throw new SQLException("Creating review failed, no ID obtained.");
            }
            
            return review;
        } catch (SQLException e) {
            System.err.println("Error saving review: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Review> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.*, u.name as user_name, u.email as user_email, " +
                         "s.name as service_name, s.description as service_description, " +
                         "s.price as service_price, s.duration_minutes as service_duration " +
                         "FROM reviews r " +
                         "LEFT JOIN users u ON r.user_id = u.id " +
                         "LEFT JOIN services s ON r.service_id = s.id " +
                         "WHERE r.id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Review review = mapReviewFromResultSet(rs);
                return Optional.of(review);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding review by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Review> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Review> reviews = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.*, u.name as user_name, u.email as user_email, " +
                         "s.name as service_name, s.description as service_description, " +
                         "s.price as service_price, s.duration_minutes as service_duration " +
                         "FROM reviews r " +
                         "LEFT JOIN users u ON r.user_id = u.id " +
                         "LEFT JOIN services s ON r.service_id = s.id " +
                         "ORDER BY r.created_at DESC";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Review review = mapReviewFromResultSet(rs);
                reviews.add(review);
            }
            
            return reviews;
        } catch (SQLException e) {
            System.err.println("Error finding all reviews: " + e.getMessage());
            return reviews;
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
            String sql = "DELETE FROM reviews WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting review: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public Review update(Review review) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setLong(3, review.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating review failed, no rows affected.");
                // Return null if no rows updated
            }
            
            return review;
        } catch (SQLException e) {
            System.err.println("Error updating review: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Review> reviews = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.*, u.name as user_name, u.email as user_email, " +
                         "s.name as service_name, s.description as service_description, " +
                         "s.price as service_price, s.duration_minutes as service_duration " +
                         "FROM reviews r " +
                         "LEFT JOIN users u ON r.user_id = u.id " +
                         "LEFT JOIN services s ON r.service_id = s.id " +
                         "WHERE r.user_id = ? " +
                         "ORDER BY r.created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Review review = mapReviewFromResultSet(rs);
                reviews.add(review);
            }
            
            return reviews;
        } catch (SQLException e) {
            System.err.println("Error finding reviews by user ID: " + e.getMessage());
            return reviews;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Review> findByServiceId(Long serviceId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Review> reviews = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.*, u.name as user_name, u.email as user_email, " +
                         "s.name as service_name, s.description as service_description, " +
                         "s.price as service_price, s.duration_minutes as service_duration " +
                         "FROM reviews r " +
                         "LEFT JOIN users u ON r.user_id = u.id " +
                         "LEFT JOIN services s ON r.service_id = s.id " +
                         "WHERE r.service_id = ? " +
                         "ORDER BY r.created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, serviceId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Review review = mapReviewFromResultSet(rs);
                reviews.add(review);
            }
            
            return reviews;
        } catch (SQLException e) {
            System.err.println("Error finding reviews by service ID: " + e.getMessage());
            return reviews;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Review> findByMinRating(int minRating) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Review> reviews = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.*, u.name as user_name, u.email as user_email, " +
                         "s.name as service_name, s.description as service_description, " +
                         "s.price as service_price, s.duration_minutes as service_duration " +
                         "FROM reviews r " +
                         "LEFT JOIN users u ON r.user_id = u.id " +
                         "LEFT JOIN services s ON r.service_id = s.id " +
                         "WHERE r.rating >= ? " +
                         "ORDER BY r.rating DESC, r.created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, minRating);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Review review = mapReviewFromResultSet(rs);
                reviews.add(review);
            }
            
            return reviews;
        } catch (SQLException e) {
            System.err.println("Error finding reviews by minimum rating: " + e.getMessage());
            return reviews;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public double getAverageRatingForService(Long serviceId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE service_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, serviceId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            } else {
                return 0.0;
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
            return 0.0;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Review> findByServiceIdWithUser(Long serviceId) {
        // This method is already implemented in findByServiceId since we're joining with users
        return findByServiceId(serviceId);
    }
    
    /**
     * Check if a user has already reviewed a specific service.
     * 
     * @param userId The user ID
     * @param serviceId The service ID
     * @return true if the user has already reviewed the service, false otherwise
     */
    public boolean hasUserReviewedService(Long userId, Long serviceId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) as review_count FROM reviews WHERE user_id = ? AND service_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            stmt.setLong(2, serviceId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("review_count") > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user reviewed service: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    // For backward compatibility with ReviewServiceImpl
    public boolean delete(Long id) {
        return deleteById(id);
    }
    
    private Review mapReviewFromResultSet(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setUserId(rs.getLong("user_id"));
        review.setServiceId(rs.getLong("service_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Map user details if available
        try {
            String userName = rs.getString("user_name");
            if (userName != null) {
                User user = new User();
                user.setId(rs.getLong("user_id"));
                user.setName(userName);
                user.setEmail(rs.getString("user_email"));
                review.setUser(user);
            }
        } catch (SQLException e) {
            // User details might not be available, ignore
        }
        
        // Map service details if available
        try {
            String serviceName = rs.getString("service_name");
            if (serviceName != null) {
                Service service = new Service();
                service.setId(rs.getLong("service_id"));
                service.setName(serviceName);
                service.setDescription(rs.getString("service_description"));
                service.setPrice(rs.getBigDecimal("service_price"));
                service.setDurationMinutes(rs.getInt("service_duration"));
                review.setService(service);
            }
        } catch (SQLException e) {
            // Service details might not be available, ignore
        }
        
        return review;
    }
}