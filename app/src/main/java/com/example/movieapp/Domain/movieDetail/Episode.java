package com.example.movieapp.Domain.movieDetail;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("server_name")
    @Expose
    private String serverName;
    @SerializedName("server_data")
    @Expose
    private List<ServerDatum> serverData;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public List<ServerDatum> getServerData() {
        return serverData;
    }

    public void setServerData(List<ServerDatum> serverData) {
        this.serverData = serverData;
    }

}