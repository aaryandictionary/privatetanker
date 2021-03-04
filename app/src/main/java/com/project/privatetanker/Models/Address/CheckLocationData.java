package com.project.privatetanker.Models.Address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckLocationData {

    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("distance")
    private String distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
