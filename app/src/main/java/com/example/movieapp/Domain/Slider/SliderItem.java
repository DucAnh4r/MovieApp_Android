package com.example.movieapp.Domain.Slider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SliderItem {

    @SerializedName("sliderid")
    @Expose
    private String sliderid;

    @SerializedName("sliderimg")
    @Expose
    private String sliderimg;

    @SerializedName("sliderslug")
    @Expose
    private String sliderslug;

    @SerializedName("sliderlink")
    @Expose
    private String sliderlink;

    @SerializedName("id")
    @Expose
    private String id;

    // Getters and setters
    public String getSliderid() {
        return sliderid;
    }

    public void setSliderid(String sliderid) {
        this.sliderid = sliderid;
    }

    public String getSliderimg() {
        return sliderimg;
    }

    public void setSliderimg(String sliderimg) {
        this.sliderimg = sliderimg;
    }

    public String getSliderslug() {
        return sliderslug;
    }

    public void setSliderslug(String sliderslug) {
        this.sliderslug = sliderslug;
    }

    public String getSliderlink() {
        return sliderlink;
    }

    public void setSliderlink(String sliderlink) {
        this.sliderlink = sliderlink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
