package com.project.privatetanker.Models.Capacity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.project.privatetanker.Models.Bookings.BookingData;

import java.util.List;

public class CapacityListResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("data")
    @Expose
    private List<CapacityData> data = null;

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

    public List<CapacityData> getData() {
        return data;
    }

    public void setData(List<CapacityData> data) {
        this.data = data;
    }
}
