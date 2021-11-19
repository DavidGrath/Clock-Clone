package com.example.clockclone.framework.network;

import android.content.Context;

import com.example.clockclone.domain.network.AccuweatherSearchResult;
import com.example.clockclone.domain.network.AccuweatherWeatherCondition;
import com.example.clockclone.framework.di.scopes.ApplicationScope;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@ApplicationScope
public class WeatherRetrofitClientTempImpl implements WeatherRetrofitClient {
    Gson gson = new Gson();
    @Inject
    public Context context;

    @Inject
    public WeatherRetrofitClientTempImpl() {
    }

    @Override
    public Single<List<AccuweatherSearchResult>> getLocationDetails(String countryCode, String query) {
        try {
            InputStream inputStream = context.getAssets().open("sample_location_results.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            AccuweatherSearchResult[] results = gson.fromJson(reader, AccuweatherSearchResult[].class);
            reader.close();
            inputStream.close();
            List<AccuweatherSearchResult> resultArrayList = new ArrayList<>(Arrays.asList(results));
            return Single.just(resultArrayList)
//                    .subscribeOn(Schedulers.io())
                    .delay(1, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
            return Single.error(e);
        }
    }

    @Override
    public Single<List<AccuweatherWeatherCondition>> getWeatherInfo(int locationKey) {
        try {
            InputStream inputStream = context.getAssets().open("sample_weather_info.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            AccuweatherWeatherCondition[] results = gson.fromJson(reader, AccuweatherWeatherCondition[].class);
            List<AccuweatherWeatherCondition> resultArrayList = new ArrayList<>(Arrays.asList(results));
            return Single.just(resultArrayList)
//                    .subscribeOn(Schedulers.io())
                    .delay(3, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
            return Single.error(e);
        }
    }
}
