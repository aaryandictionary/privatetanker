package com.project.privatetanker.Models.Charges;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.project.privatetanker.Models.User.UserData;

import java.util.List;

public class ChargeListResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("data")
    @Expose
    private List<ChargeData> data=null;

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

    public List<ChargeData> getData() {
        return data;
    }

    public void setData(List<ChargeData> data) {
        this.data = data;
    }
}
