package com.project.privatetanker.Models.Address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddAddressModel {

    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("user_id")
    private Integer user_id;

    @Expose
    @SerializedName("address")
    private String address;

    @Expose
    @SerializedName("address_type")
    private String address_type;

    @Expose
    @SerializedName("lat")
    private double lat;

    @Expose
    @SerializedName("lng")
    private double lng;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_type() {
        return address_type;
    }

    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
