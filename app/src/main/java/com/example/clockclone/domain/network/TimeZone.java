package com.example.clockclone.domain.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class TimeZone {

    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("GmtOffset")
    @Expose
    private Integer gmtOffset;
    @SerializedName("IsDaylightSaving")
    @Expose
    private Boolean isDaylightSaving;
    @SerializedName("NextOffsetChange")
    @Expose
    private String nextOffsetChange;

    /**
     * No args constructor for use in serialization
     */
    public TimeZone() {
    }

    /**
     * @param code
     * @param isDaylightSaving
     * @param gmtOffset
     * @param name
     * @param nextOffsetChange
     */
    public TimeZone(String code, String name, Integer gmtOffset, Boolean isDaylightSaving, String nextOffsetChange) {
        super();
        this.code = code;
        this.name = name;
        this.gmtOffset = gmtOffset;
        this.isDaylightSaving = isDaylightSaving;
        this.nextOffsetChange = nextOffsetChange;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(Integer gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public Boolean getIsDaylightSaving() {
        return isDaylightSaving;
    }

    public void setIsDaylightSaving(Boolean isDaylightSaving) {
        this.isDaylightSaving = isDaylightSaving;
    }

    public String getNextOffsetChange() {
        return nextOffsetChange;
    }

    public void setNextOffsetChange(String nextOffsetChange) {
        this.nextOffsetChange = nextOffsetChange;
    }

}
