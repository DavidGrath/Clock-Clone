package com.example.clockclone.domain;

import java.util.Objects;

public class WorldClockCityInfo {
    private final WorldClockCity worldClockCity;
    private final WeatherInfo weatherInfo;
    private final WeatherState weatherState;

    public WorldClockCityInfo(WorldClockCity worldClockCity, WeatherInfo weatherInfo, WeatherState weatherState) {
        this.worldClockCity = worldClockCity;
        this.weatherInfo = weatherInfo;
        this.weatherState = weatherState;
    }

    public WorldClockCity getWorldClockCity() {
        return worldClockCity;
    }

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public WeatherState getWeatherState() {
        return weatherState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldClockCityInfo info = (WorldClockCityInfo) o;
        return worldClockCity.equals(info.worldClockCity) &&
                Objects.equals(weatherInfo, info.weatherInfo) &&
                weatherState == info.weatherState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldClockCity, weatherInfo, weatherState);
    }
}
