package com.example.movieapp.Domain;

public class NotificationMessage {
    private String content, type, slug;
    private long timestamp;

    public NotificationMessage(String content, long timestamp, String type, String slug) {
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getSlug() {
        return slug;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

