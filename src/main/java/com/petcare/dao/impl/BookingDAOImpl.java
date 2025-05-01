package com.petcare.dao.impl;

import com.petcare.dao.BookingDAO;
import com.petcare.model.Booking;
import com.petcare.model.Service;
import com.petcare.model.User;
import com.petcare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of BookingDAO interface for database operations.
 */
public class BookingDAOImpl implements BookingDAO {

    @Override
    public Booking save(Booking booking) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (booking.getId() == null) {
                // Insert new booking
                String sql = "INSERT INTO bookings (user_id, service_id, scheduled_time, status) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setLong(1, booking.getUserId());
                stmt.setLong(2, booking.getServiceId());
                stmt.setTimestamp(3, booking.getScheduledTime());
                stmt.setString(4, booking.getStatus());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating booking failed, no rows affected.");
                }
                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    booking.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            } else {
                // Update existing booking
                return update(booking);
            }
            
            return booking;
        } catch (SQLException e) {
            System.err.println("Error saving booking: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Booking> findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM bookings WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Booking booking = mapBookingFromResultSet(rs);
                return Optional.of(booking);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding booking by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Booking> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM bookings";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
            return bookings;
        } catch (SQLException e) {
            System.err.println("Error finding all bookings: " + e.getMessage());
            return bookings;
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
            String sql = "DELETE FROM bookings WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting booking by ID: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public Booking update(Booking booking) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE bookings SET user_id = ?, service_id = ?, scheduled_time = ?, status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, booking.getUserId());
            stmt.setLong(2, booking.getServiceId());
            stmt.setTimestamp(3, booking.getScheduledTime());
            stmt.setString(4, booking.getStatus());
            stmt.setLong(5, booking.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating booking failed, no rows affected.");
            }
            
            return booking;
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            return null;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM bookings WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
            return bookings;
        } catch (SQLException e) {
            System.err.println("Error finding bookings by user ID: " + e.getMessage());
            return bookings;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Booking> findByServiceId(Long serviceId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM bookings WHERE service_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, serviceId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
            return bookings;
        } catch (SQLException e) {
            System.err.println("Error finding bookings by service ID: " + e.getMessage());
            return bookings;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Booking> findByStatus(String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM bookings WHERE status = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
            return bookings;
        } catch (SQLException e) {
            System.err.println("Error finding bookings by status: " + e.getMessage());
            return bookings;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Booking> findByDateRange(Timestamp startDate, Timestamp endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM bookings WHERE scheduled_time BETWEEN ? AND ?";
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
            return bookings;
        } catch (SQLException e) {
            System.err.println("Error finding bookings by date range: " + e.getMessage());
            return bookings;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Booking> findByUserIdWithService(Long userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT b.*, s.name as service_name, s.description as service_description, " +
                         "s.price as service_price, s.duration_minutes as service_duration, " +
                         "u.name as user_name, u.email as user_email " +
                         "FROM bookings b " +
                         "JOIN services s ON b.service_id = s.id " +
                         "JOIN users u ON b.user_id = u.id " +
                         "WHERE b.user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapBookingFromResultSet(rs);
                
                // Set service details
                Service service = new Service();
                service.setId(rs.getLong("service_id"));
                service.setName(rs.getString("service_name"));
                service.setDescription(rs.getString("service_description"));
                service.setPrice(rs.getBigDecimal("service_price"));
                service.setDurationMinutes(rs.getInt("service_duration"));
                booking.setService(service);
                
                // Set user details
                User user = new User();
                user.setId(rs.getLong("user_id"));
                user.setName(rs.getString("user_name"));
                user.setEmail(rs.getString("user_email"));
                booking.setUser(user);
                
                bookings.add(booking);
            }
            
            return bookings;
        } catch (SQLException e) {
            System.err.println("Error finding bookings by user ID with service: " + e.getMessage());
            return bookings;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public boolean updateStatus(Long bookingId, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE bookings SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setLong(2, bookingId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }
    
    private Booking mapBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));
        booking.setUserId(rs.getLong("user_id"));
        booking.setServiceId(rs.getLong("service_id"));
        booking.setScheduledTime(rs.getTimestamp("scheduled_time"));
        booking.setStatus(rs.getString("status"));
        return booking;
    }
}