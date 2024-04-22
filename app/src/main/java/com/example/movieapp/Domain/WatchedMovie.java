package com.example.movieapp.Domain;

public class WatchedMovie {
    private String slug;
    private Long addTime;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Constructor
    public WatchedMovie(String slug, String name, Long addTime) {
        this.slug = slug;
        this.addTime = addTime;
        this.name = name;
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
