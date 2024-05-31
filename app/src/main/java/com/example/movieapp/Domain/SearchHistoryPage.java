package com.example.movieapp.Domain;

public class SearchHistoryPage {
    private String searchQuery;
    private long searchTime;

    public SearchHistoryPage(String searchQuery, long searchTime) {
        this.searchQuery = searchQuery;
        this.searchTime = searchTime;

    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public long getSearchTime() {
        return searchTime;
    }
}
