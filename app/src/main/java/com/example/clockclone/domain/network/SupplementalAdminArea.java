package com.example.clockclone.domain.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class SupplementalAdminArea {

    @SerializedName("Level")
    @Expose
    private Integer level;
    @SerializedName("LocalizedName")
    @Expose
    private String localizedName;
    @SerializedName("EnglishName")
    @Expose
    private String englishName;

    /**
     * No args constructor for use in serialization
     */
    public SupplementalAdminArea() {
    }

    /**
     * @param englishName
     * @param localizedName
     * @param level
     */
    public SupplementalAdminArea(Integer level, String localizedName, String englishName) {
        super();
        this.level = level;
        this.localizedName = localizedName;
        this.englishName = englishName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

}
