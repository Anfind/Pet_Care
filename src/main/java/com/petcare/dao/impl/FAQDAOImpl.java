package com.petcare.dao.impl;

import com.petcare.dao.FAQDAO;
import com.petcare.model.FAQ;
import com.petcare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of FAQDAO interface for database operations.
 */
public class FAQDAOImpl implements FAQDAO {

    @Override
    public FAQ save(FAQ faq) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (faq.getId() == null) {
                // Insert new FAQ
                String sql = "INSERT INTO faqs (question, answer) VALUES (?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, faq.getQuestion());
                stmt.setString(2, faq.getAnswer());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating FAQ failed, no rows affected.");
                }
                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    faq.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating FAQ failed, no ID obtained.");
                }
            } else {
                // Update existing FAQ
                return update(faq);
            }
            
            return faq;
        } catch (SQLException e) {
            System.err.println("Error saving FAQ: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<FAQ> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM faqs WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                FAQ faq = mapFAQFromResultSet(rs);
                return Optional.of(faq);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding FAQ by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<FAQ> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<FAQ> faqs = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM faqs ORDER BY id";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                FAQ faq = mapFAQFromResultSet(rs);
                faqs.add(faq);
            }
            
            return faqs;
        } catch (SQLException e) {
            System.err.println("Error finding all FAQs: " + e.getMessage());
            return faqs;
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
            String sql = "DELETE FROM faqs WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting FAQ by ID: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public FAQ update(FAQ faq) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE faqs SET question = ?, answer = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, faq.getQuestion());
            stmt.setString(2, faq.getAnswer());
            stmt.setLong(3, faq.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating FAQ failed, no rows affected.");
            }
            
            return faq;
        } catch (SQLException e) {
            System.err.println("Error updating FAQ: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<FAQ> searchByQuestionOrAnswer(String searchText) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<FAQ> faqs = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM faqs WHERE LOWER(question) LIKE ? OR LOWER(answer) LIKE ? ORDER BY id";
            stmt = conn.prepareStatement(sql);
            
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                FAQ faq = mapFAQFromResultSet(rs);
                faqs.add(faq);
            }
            
            return faqs;
        } catch (SQLException e) {
            System.err.println("Error searching FAQs: " + e.getMessage());
            return faqs;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<FAQ> findByTopic(String topic) {
        // Since we don't have a direct topic field, we search in the question
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<FAQ> faqs = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM faqs WHERE LOWER(question) LIKE ? ORDER BY id";
            stmt = conn.prepareStatement(sql);
            
            String searchPattern = "%" + topic.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                FAQ faq = mapFAQFromResultSet(rs);
                faqs.add(faq);
            }
            
            return faqs;
        } catch (SQLException e) {
            System.err.println("Error finding FAQs by topic: " + e.getMessage());
            return faqs;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
    
    private FAQ mapFAQFromResultSet(ResultSet rs) throws SQLException {
        FAQ faq = new FAQ();
        faq.setId(rs.getLong("id"));
        faq.setQuestion(rs.getString("question"));
        faq.setAnswer(rs.getString("answer"));
        return faq;
    }
}