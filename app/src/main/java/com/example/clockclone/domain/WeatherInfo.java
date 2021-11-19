package com.example.clockclone.domain;

import java.util.Objects;

public class WeatherInfo {

    private final Temperature metric;
    private final Temperature imperial;
    private final int icon;
    private final String mobileLink;

    public static class Temperature {
        final double value;

        public Temperature(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Temperature that = (Temperature) o;
            return Double.compare(that.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public WeatherInfo(Temperature metric, Temperature imperial, int icon, String mobileLink) {
        this.metric = metric;
        this.imperial = imperial;
        this.icon = icon;
        this.mobileLink = mobileLink;
    }

    public Temperature getMetric() {
        return metric;
    }

    public Temperature getImperial() {
        return imperial;
    }

    public int getIcon() {
        return icon;
    }

    public String getMobileLink() {
        return mobileLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherInfo that = (WeatherInfo) o;
        return icon == that.icon &&
                metric.equals(that.metric) &&
                imperial.equals(that.imperial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metric, imperial, icon);
    }
}
