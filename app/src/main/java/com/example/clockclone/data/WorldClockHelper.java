package com.example.clockclone.data;

import com.example.clockclone.domain.WorldClockCityInfo;
import com.example.clockclone.domain.WorldClockCity;
import com.example.clockclone.domain.WeatherState;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface WorldClockHelper {

    List<WorldClockCity> getFullCityList();
    List<WorldClockCity> getSavedCities();
    Single<String> saveCity(String timeZone);
    void deleteCity(String timeZone);
    void deleteCities(List<String> timeZoneList);
    void reorderCities(String commaSep);
    Observable<List<WorldClockCityInfo>> getWeatherInfo();
    Observable<Integer> getInterval();
    void updateWeatherInfo();
    void updateWeatherInfo(String timeZone);
    int getLocationCode(String timeZone);
    Map<String, Integer> getLocationCodes();
    void saveLocationCode(String timeZone, int code);
    void swapCities(int firstIndex, int secondIndex);
}
