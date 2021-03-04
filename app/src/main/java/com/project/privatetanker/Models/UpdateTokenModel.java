package com.project.privatetanker.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateTokenModel {

    @Expose
    @SerializedName("id")
    private Integer id;
    @Expose
    @SerializedName("fcm_token")
    private String fcm_token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }
}
