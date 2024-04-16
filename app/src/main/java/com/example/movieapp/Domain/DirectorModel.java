package com.example.movieapp.Domain;

public class DirectorModel {
    private String name;
    private String imageUrl;

    public DirectorModel(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
