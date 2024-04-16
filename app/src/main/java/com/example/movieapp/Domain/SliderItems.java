package com.example.movieapp.Domain;

public class SliderItems {
    private int image;
    private String slug;

    public SliderItems(int image, String slug){
        this.image = image;
        this.slug = slug;
    }

    public int getImage(){
        return image;
    }

    public void setImage(int image){
        this.image = image;
    }

    public String getSlug(){
        return slug;
    }

    public void setSlug(String slug){
        this.slug = slug;
    }
}
