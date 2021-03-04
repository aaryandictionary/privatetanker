package com.project.privatetanker.Models.Connections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddConnectionResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("data")
    @Expose
    private ConnectionData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ConnectionData getData() {
        return data;
    }

    public void setData(ConnectionData data) {
        this.data = data;
    }
}
