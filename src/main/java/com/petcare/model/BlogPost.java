package com.petcare.model;

import java.sql.Timestamp;

public class BlogPost {
    private Long id;
    private String title;
    private String content;
    private Timestamp createdAt;

    public BlogPost() {
    }

    public BlogPost(Long id, String title, String content, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BlogPost{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(20, content.length())) + "..." : "null") + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}