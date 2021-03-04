package com.project.privatetanker.Models.Capacity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CapacityData {

    @Expose
    @SerializedName("id")
    private Integer id;
    @Expose
    @SerializedName("capacity")
    private String capacity;
    @Expose
    @SerializedName("price")
    private double price;
    @Expose
    @SerializedName("status")
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
