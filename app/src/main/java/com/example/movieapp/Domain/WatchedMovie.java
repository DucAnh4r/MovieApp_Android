package com.example.movieapp.Domain;

public class WatchedMovie {
    private String slug;
    private Long addTime;

    // Constructor
    public WatchedMovie(String slug, Long addTime) {
        this.slug = slug;
        this.addTime = addTime;
    }

    // Getter cho slug
    public String getSlug() {
        return slug;
    }

    // Setter cho slug
    public void setSlug(String slug) {
        this.slug = slug;
    }

    // Getter cho addTime
    public Long getAddTime() {
        return addTime;
    }

    // Setter cho addTime
    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }
}
