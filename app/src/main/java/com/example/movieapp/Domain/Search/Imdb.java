
package com.example.movieapp.Domain.Search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Imdb {

    @SerializedName("id")
    @Expose
    private Object id;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

}
