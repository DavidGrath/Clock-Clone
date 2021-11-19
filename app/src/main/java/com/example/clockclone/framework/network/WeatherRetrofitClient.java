package com.example.clockclone.framework.network;

import com.example.clockclone.domain.WeatherInfo;
import com.example.clockclone.domain.network.AccuweatherSearchResult;
import com.example.clockclone.domain.network.AccuweatherWeatherCondition;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherRetrofitClient {
    @GET("locations/v1/cities/{countryCode}/search")
    Single<List<AccuweatherSearchResult>> getLocationDetails(@Path("countryCode") String countryCode, @Query("q") String query);
    @GET("currentconditions/v1/{locationKey}")
    Single<List<AccuweatherWeatherCondition>> getWeatherInfo(@Path("locationKey") int locationKey);
}
