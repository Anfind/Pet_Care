package com.petcare.model;

import java.sql.Timestamp;

public class Message {
    private Long id;
    private Long userId;
    private Long adminId;
    private String messageText;
    private Timestamp timestamp;
    private User user; // For ease of displaying user details
    private User admin; // For ease of displaying admin details

    public Message() {
    }

    public Message(Long id, Long userId, Long adminId, String messageText, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.adminId = adminId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", userId=" + userId +
                ", adminId=" + adminId +
                ", messageText='" + (messageText != null ? messageText.substring(0, Math.min(20, messageText.length())) + "..." : "null") + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}