package com.project.privatetanker.Models.Charges;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChargeData {

    @Expose
    @SerializedName("id")
    private Integer id;
    @Expose
    @SerializedName("capacity_id")
    private Integer capacity_id;
    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("amount")
    private double amount;
    @Expose
    @SerializedName("percent")
    private double percent;
    @Expose
    @SerializedName("charge_type")
    private Integer charge_type;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("status")
    private Integer status;
    @Expose
    @SerializedName("created_at")
    private String created_at;
    @Expose
    @SerializedName("updated_at")
    private String updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCapacity_id() {
        return capacity_id;
    }

    public void setCapacity_id(Integer capacity_id) {
        this.capacity_id = capacity_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public Integer getCharge_type() {
        return charge_type;
    }

    public void setCharge_type(Integer charge_type) {
        this.charge_type = charge_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
