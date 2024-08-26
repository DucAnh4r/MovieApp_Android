package com.example.movieapp.Domain;

public class WatchedHistoryPage {
    private String slug, movieName;
    private long watchTime;

    public WatchedHistoryPage(String slug, String movieName, long watchTime) {
        this.slug = slug;
        this.movieName = movieName;
        this.watchTime = watchTime;

    }

    public String getMovieName() {
        return movieName;
    }

    public long getWatchTime() {
        return watchTime;
    }

    public String getSlug() { return slug; }
}
