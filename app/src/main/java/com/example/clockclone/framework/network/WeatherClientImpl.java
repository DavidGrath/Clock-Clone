package com.example.clockclone.framework.network;

import com.example.clockclone.BuildConfig;
import com.example.clockclone.data.network.WeatherClient;
import com.example.clockclone.domain.WeatherInfo;
import com.example.clockclone.domain.network.AccuweatherWeatherCondition;
import com.example.clockclone.framework.di.scopes.ApplicationScope;
import com.example.clockclone.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

@ApplicationScope
public class WeatherClientImpl implements WeatherClient {

    @Inject
    public WeatherRetrofitClient weatherRetrofitClient;

    @Inject
    public WeatherClientImpl() {
    }

    @Override
    public Single<WeatherInfo> getWeatherCondition(int locationKey) {
        return weatherRetrofitClient.getWeatherInfo(locationKey)
                .map((weatherConditions) -> {
                    AccuweatherWeatherCondition weatherCondition = weatherConditions.get(0);
                    WeatherInfo.Temperature metric = new WeatherInfo.Temperature(weatherCondition.getTemperature().getMetric().getValue());
                    WeatherInfo.Temperature imperial = new WeatherInfo.Temperature(weatherCondition.getTemperature().getImperial().getValue());
                    return new WeatherInfo(metric, imperial,
                            weatherCondition.getWeatherIcon(), weatherCondition.getMobileLink());
                });
    }

    @Override
    public Single<Integer> getLocationCode(String countryCode, String query) {
        return weatherRetrofitClient.getLocationDetails(countryCode, query)
        .map((accuweatherSearchResults) -> {
            if(accuweatherSearchResults.isEmpty()) {
                return null;
            }
            return Integer.parseInt(accuweatherSearchResults.get(0).getKey());
        });
    }
}
