package com.example.clockclone.domain.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class Metric {

    @SerializedName("Value")
    @Expose
    private double value;
    @SerializedName("Unit")
    @Expose
    private String unit;
    @SerializedName("UnitType")
    @Expose
    private int unitType;

    /**
     * No args constructor for use in serialization
     */
    public Metric() {
    }

    /**
     * @param unitType
     * @param unit
     * @param value
     */
    public Metric(double value, String unit, int unitType) {
        super();
        this.value = value;
        this.unit = unit;
        this.unitType = unitType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }

}
