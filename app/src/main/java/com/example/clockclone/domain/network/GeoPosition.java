package com.example.clockclone.domain.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class GeoPosition {

    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("Elevation")
    @Expose
    private Elevation elevation;

    /**
     * No args constructor for use in serialization
     */
    public GeoPosition() {
    }

    /**
     * @param elevation
     * @param latitude
     * @param longitude
     */
    public GeoPosition(Double latitude, Double longitude, Elevation elevation) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Elevation getElevation() {
        return elevation;
    }

    public void setElevation(Elevation elevation) {
        this.elevation = elevation;
    }

}
