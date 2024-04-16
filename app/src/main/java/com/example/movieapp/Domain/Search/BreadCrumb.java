
package com.example.movieapp.Domain.Search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BreadCrumb {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isCurrent")
    @Expose
    private Boolean isCurrent;
    @SerializedName("position")
    @Expose
    private Integer position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}
