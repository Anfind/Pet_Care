package com.petcare.model;

import java.sql.Timestamp;

public class ContactMessage {
    private Long id;
    private Long userId; // can be null for anonymous messages
    private String subject;
    private String message;
    private Timestamp timestamp;
    private User user; // For ease of displaying user details, can be null

    public ContactMessage() {
    }

    public ContactMessage(Long id, Long userId, String subject, String message, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.subject = subject;
        this.message = message;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    @Override
    public String toString() {
        return "ContactMessage{" +
                "id=" + id +
                ", userId=" + userId +
                ", subject='" + subject + '\'' +
                ", message='" + (message != null ? message.substring(0, Math.min(20, message.length())) + "..." : "null") + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}