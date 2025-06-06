
package com.example.movieapp.Domain.Search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tmdb {

    @SerializedName("type")
    @Expose
    private Object type;
    @SerializedName("id")
    @Expose
    private Object id;
    @SerializedName("season")
    @Expose
    private Object season;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getSeason() {
        return season;
    }

    public void setSeason(Object season) {
        this.season = season;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

}
