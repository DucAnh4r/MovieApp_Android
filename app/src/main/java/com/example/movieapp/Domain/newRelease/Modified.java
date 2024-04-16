
package com.example.movieapp.Domain.newRelease;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Modified {

    @SerializedName("time")
    @Expose
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
