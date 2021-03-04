package com.project.privatetanker.Models.Connections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddConnectionModel {

    @Expose
    @SerializedName("user_id")
    private Integer user_id;

    @Expose
    @SerializedName("phone")
    private String phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
