package com.project.privatetanker.Models.Bookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.project.privatetanker.Models.BookingParticular.BookingParticularModel;

import java.util.List;

public class BookingModel {

    @Expose
    @SerializedName("id")
    private Integer id;
    @Expose
    @SerializedName("user_id")
    private Integer user_id;
    @Expose
    @SerializedName("area_id")
    private Integer area_id;
    @Expose
    @SerializedName("date")
    private String date;
    @Expose
    @SerializedName("time")
    private String time;
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
    @Expose
    @SerializedName("capacity")
    private String capacity;
    @Expose
    @SerializedName("price")
    private double price;
    @Expose
    @SerializedName("amount")
    private double amount;
    @Expose
    @SerializedName("notes")
    private String notes;
    @Expose
    @SerializedName("particulars")
    private List<BookingParticularModel> particulars=null;

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

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<BookingParticularModel> getParticulars() {
        return particulars;
    }

    public void setParticulars(List<BookingParticularModel> particulars) {
        this.particulars = particulars;
    }
}
