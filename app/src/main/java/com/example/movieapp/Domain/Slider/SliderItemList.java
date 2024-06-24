package com.example.movieapp.Domain.Slider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SliderItemList {
    @SerializedName("items")
    @Expose
    private List<SliderItem> sliderItems;

    public List<SliderItem> getSliderItems() {
        return sliderItems;
    }

    public void setSliderItems(List<SliderItem> sliderItems) {
        this.sliderItems = sliderItems;
    }
}
