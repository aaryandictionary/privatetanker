package com.project.privatetanker.Models.BookingParticular;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingParticularModel {

    @Expose
    @SerializedName("id")
    private Integer id;
    @Expose
    @SerializedName("booking_id")
    private Integer booking_id;
    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("price")
    private double price;
    @Expose
    @SerializedName("type")
    private Integer type;
    @Expose
    @SerializedName("extra")
    private String extra;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Integer booking_id) {
        this.booking_id = booking_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
