package com.example.clockclone.domain;

import java.util.Objects;

public class WorldClockCity {

    private String timeZone;
    private String city;
    private String countryCode;
    private String country;

    public WorldClockCity(String timeZone, String city, String countryCode, String country) {
        this.timeZone = timeZone;
        this.city = city;
        this.countryCode = countryCode;
        this.country = country;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getCity() {
        return city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldClockCity that = (WorldClockCity) o;
        return timeZone.equals(that.timeZone) &&
                city.equals(that.city) &&
                countryCode.equals(that.countryCode) &&
                country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeZone, city, countryCode, country);
    }
}
