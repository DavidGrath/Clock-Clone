package com.example.clockclone.data.network;

import com.example.clockclone.domain.WeatherInfo;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface WeatherClient {
    Single<WeatherInfo> getWeatherCondition(int locationKey);
    Single<Integer> getLocationCode(String query, String countryCode);
}
