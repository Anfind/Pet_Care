package com.petcare.model;

import java.sql.Timestamp;

public class Booking {
    private Long id;
    private Long userId;
    private Long serviceId;
    private Timestamp scheduledTime;
    private String status;
    private Service service; // For ease of displaying service details
    private User user; // For ease of displaying user details

    public Booking() {
    }

    public Booking(Long id, Long userId, Long serviceId, Timestamp scheduledTime, String status) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.scheduledTime = scheduledTime;
        this.status = status;
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

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", serviceId=" + serviceId +
                ", scheduledTime=" + scheduledTime +
                ", status='" + status + '\'' +
                ", service=" + (service != null ? service.getName() : "null") +
                ", user=" + (user != null ? user.getName() : "null") +
                '}';
    }
}