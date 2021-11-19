package com.example.clockclone.domain.network;

//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class AccuweatherWeatherCondition {
    @SerializedName("LocalObservationDateTime")
    @Expose
    private String localObservationDateTime;
    @SerializedName("EpochTime")
    @Expose
    private int epochTime;
    @SerializedName("WeatherText")
    @Expose
    private String weatherText;
    @SerializedName("WeatherIcon")
    @Expose
    private int weatherIcon;
    @SerializedName("HasPrecipitation")
    @Expose
    private boolean hasPrecipitation;
    @SerializedName("PrecipitationType")
    @Expose
    private String precipitationType;
    @SerializedName("IsDayTime")
    @Expose
    private boolean isDayTime;
    @SerializedName("Temperature")
    @Expose
    private Temperature temperature;
    @SerializedName("MobileLink")
    @Expose
    private String mobileLink;
    @SerializedName("Link")
    @Expose
    private String link;

    /**
     * No args constructor for use in serialization
     *
     */
    public AccuweatherWeatherCondition() {
    }

    /**
     *
     * @param hasPrecipitation
     * @param weatherIcon
     * @param precipitationType
     * @param localObservationDateTime
     * @param isDayTime
     * @param temperature
     * @param link
     * @param mobileLink
     * @param epochTime
     * @param weatherText
     */
    public AccuweatherWeatherCondition(String localObservationDateTime, int epochTime, String weatherText, int weatherIcon, boolean hasPrecipitation, String precipitationType, boolean isDayTime, Temperature temperature, String mobileLink, String link) {
        super();
        this.localObservationDateTime = localObservationDateTime;
        this.epochTime = epochTime;
        this.weatherText = weatherText;
        this.weatherIcon = weatherIcon;
        this.hasPrecipitation = hasPrecipitation;
        this.precipitationType = precipitationType;
        this.isDayTime = isDayTime;
        this.temperature = temperature;
        this.mobileLink = mobileLink;
        this.link = link;
    }

    public String getLocalObservationDateTime() {
        return localObservationDateTime;
    }

    public void setLocalObservationDateTime(String localObservationDateTime) {
        this.localObservationDateTime = localObservationDateTime;
    }

    public int getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(int epochTime) {
        this.epochTime = epochTime;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public boolean isHasPrecipitation() {
        return hasPrecipitation;
    }

    public void setHasPrecipitation(boolean hasPrecipitation) {
        this.hasPrecipitation = hasPrecipitation;
    }

    public String getPrecipitationType() {
        return precipitationType;
    }

    public void setPrecipitationType(String precipitationType) {
        this.precipitationType = precipitationType;
    }

    public boolean isIsDayTime() {
        return isDayTime;
    }

    public void setIsDayTime(boolean isDayTime) {
        this.isDayTime = isDayTime;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public String getMobileLink() {
        return mobileLink;
    }

    public void setMobileLink(String mobileLink) {
        this.mobileLink = mobileLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
