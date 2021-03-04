package com.project.privatetanker.Models.Connections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RemoveConnectionModel {

    @Expose
    @SerializedName("user_id")
    private Integer user_id;

    @Expose
    @SerializedName("connected_id")
    private Integer connected_id;

    @Expose
    @SerializedName("pending")
    private String pending;

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getConnected_id() {
        return connected_id;
    }

    public void setConnected_id(Integer connected_id) {
        this.connected_id = connected_id;
    }
}
