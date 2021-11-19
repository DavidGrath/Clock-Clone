package com.example.clockclone.framework;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.clockclone.data.WorldClockHelper;
import com.example.clockclone.data.network.WeatherClient;
import com.example.clockclone.domain.WorldClockCity;
import com.example.clockclone.domain.WorldClockCityInfo;
import com.example.clockclone.domain.WeatherInfo;
import com.example.clockclone.domain.WeatherState;
import com.example.clockclone.framework.di.scopes.ApplicationScope;
import com.example.clockclone.util.Constants;
import com.example.clockclone.util.GeneralUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.reactivestreams.Subscriber;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@ApplicationScope
public class WorldClockHelperImpl implements WorldClockHelper {

    private WeatherClient weatherClient;
    private SharedPreferences sharedPreferences;
    private Context context;
    private ArrayList<WorldClockCity> fullCityList = new ArrayList<>();
    private Map<String, WorldClockCity> weatherCityMap = new HashMap<>();
    private BehaviorSubject<List<WorldClockCityInfo>> weatherInfoBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<Integer> intervalBehaviorSubject = BehaviorSubject.create();
    CompositeDisposable weatherDisposable = new CompositeDisposable();
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<WorldClockCityInfo> cachedCities = new ArrayList<>();

    Runnable intervalRunnable = new Runnable() {
        @Override
        public void run() {
            Calendar now = Calendar.getInstance(Locale.getDefault());
            Calendar next = Calendar.getInstance(Locale.getDefault());
            next.add(Calendar.MINUTE, 1);
            next.set(Calendar.SECOND, 0);
            next.set(Calendar.MILLISECOND, 0);
            long duration = next.getTimeInMillis() - now.getTimeInMillis();
            if (duration >= 0L && duration <= 60_000L) {
                intervalBehaviorSubject.onNext(-1);
            }
            handler.postDelayed(this, duration);
        }
    };

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("SAVED_CITIES")) {
                List<WorldClockCity> newList = getSavedCities();
                ArrayList<WorldClockCityInfo> worldClockCityInfos = new ArrayList<>(newList.size());
                for (int i = 0; i < newList.size(); i++) {
                    worldClockCityInfos.add(new WorldClockCityInfo(newList.get(i), null, WeatherState.NONE));
                }
                cachedCities = worldClockCityInfos;
                weatherInfoBehaviorSubject.onNext(cachedCities);
                updateWeatherInfo();
            }
        }
    };

    @Inject
    public WorldClockHelperImpl(Context context, WeatherClient weatherClient) {
        this.context = context;
        this.weatherClient = weatherClient;
        sharedPreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        handler.post(intervalRunnable);
        try {
            InputStream inputStream = context.getAssets().open("clock_clone_timezone.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVFormat format = CSVFormat.Builder
                    .create(CSVFormat.DEFAULT)
                    .setHeader("timeZone", "strippedCity", "country", "countryCode", "gmtOffset")
                    .setSkipHeaderRecord(true)
                    .build();
            CSVParser parser = CSVParser.parse(inputStreamReader, format);
            ArrayList<WorldClockCity> weatherCities = new ArrayList<>();
            for (CSVRecord record : parser) {
                String timeZone = record.get("timeZone");
                String city = record.get("strippedCity");
                String country = record.get("country");
                String countryCode = record.get("countryCode");
                WorldClockCity worldClockCity = new WorldClockCity(timeZone, city, countryCode, country);
                weatherCities.add(worldClockCity);
                weatherCityMap.put(timeZone, worldClockCity);
            }
            this.fullCityList = weatherCities;
            List<WorldClockCity> cityList = getSavedCities();
            ArrayList<WorldClockCityInfo> worldClockCityInfos = new ArrayList<>(cityList.size());
            for (int i = 0; i < cityList.size(); i++) {
                worldClockCityInfos.add(new WorldClockCityInfo(cityList.get(i), null, WeatherState.NONE));
            }
            cachedCities = worldClockCityInfos;
            weatherInfoBehaviorSubject.onNext(cachedCities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WorldClockCity> getFullCityList() {
        return fullCityList;
    }

    @Override
    public List<WorldClockCity> getSavedCities() {
        String savedCities = sharedPreferences.getString("SAVED_CITIES", "");
        if (savedCities.equals("")) {
            return new ArrayList<>();
        }
        String[] split = savedCities.split(",");
        ArrayList<WorldClockCity> saved = new ArrayList<>();
        for (String s : split) {
            saved.add(weatherCityMap.get(s));
        }
        return saved;
    }

    @Override
    public Single<String> saveCity(String timeZone) {
        String savedCities = sharedPreferences.getString("SAVED_CITIES", "");
        String[] split = savedCities.split(",");
        if(savedCities.equals("")) {
            split = new String[]{};
        }
        ArrayList<String> cities = new ArrayList<>(Arrays.asList(split));
        int index = GeneralUtils.find(cities, (c) -> {
            return c.equals(timeZone);
        });
        if (index == -1) {
            //Set State NONE
            WorldClockCity city = weatherCityMap.get(timeZone);
            weatherDisposable.dispose();
            if (getLocationCode(timeZone) == -1) {
                cities.add(0, timeZone);
                String join = TextUtils.join(",", cities);
                sharedPreferences.edit().putString("SAVED_CITIES", join).apply();
                //No longer using Toast. Appending "X" to make equals() return false
                return saveLocKey(city)
                        .map((locationKey) -> "SUCCESS")
                        .onErrorReturnItem("X-FAILURE");
            } else {
                cities.add(0, timeZone);
                String join = TextUtils.join(",", cities);
                sharedPreferences.edit().putString("SAVED_CITIES", join).apply();
                return Single.just("SUCCESS");
            }
        } else {
            return Single.just("SUCCESS");
        }
    }

    @Override
    public void deleteCity(String timeZone) {
        String savedCities = sharedPreferences.getString("SAVED_CITIES", "");
        if (savedCities.equals("")) {
            return;
        }
        String[] split = savedCities.split(",");
        ArrayList<String> cities = new ArrayList<>(Arrays.asList(split));
        cities.remove(timeZone);
        weatherDisposable.dispose();
        String join = TextUtils.join(",", cities);
        sharedPreferences.edit().putString("SAVED_CITIES", join).apply();
    }

    @Override
    public void deleteCities(List<String> timeZoneList) {
        String savedCities = sharedPreferences.getString("SAVED_CITIES", "");
        if (savedCities.equals("")) {
            return;
        }
        String[] split = savedCities.split(",");
        ArrayList<String> cities = new ArrayList<>(Arrays.asList(split));
        cities.removeAll(timeZoneList);
        weatherDisposable.dispose();
        String join = TextUtils.join(",", cities);
        sharedPreferences.edit().putString("SAVED_CITIES", join).apply();
    }

    @Override
    public void reorderCities(String commaSep) {
        sharedPreferences.edit().putString("SAVED_CITIES", commaSep).apply();
    }


    @Override
    public Observable<List<WorldClockCityInfo>> getWeatherInfo() {
        return weatherInfoBehaviorSubject;
    }

    @Override
    public Observable<Integer> getInterval() {
        return intervalBehaviorSubject;
    }

    @Override
    public void updateWeatherInfo() {
        List<WorldClockCity> cities = getSavedCities();
        ArrayList<WorldClockCityInfo> worldClockCityInfos = new ArrayList<>(cachedCities.size());
        for (int i = 0; i < cachedCities.size(); i++) {
            worldClockCityInfos.add(new WorldClockCityInfo(cities.get(i), null, WeatherState.LOADING));
        }
        cachedCities = worldClockCityInfos;
        weatherInfoBehaviorSubject.onNext(cachedCities);
        ArrayList<Single<WeatherInfo>> singles = new ArrayList<>();
        weatherDisposable = new CompositeDisposable();
        int length = cities.size();
        for (int i = 0; i < length; i++) {
            WorldClockCity c = cities.get(i);
            final int index = i;
            if(getLocationCode(c.getTimeZone()) == -1) {
                saveLocKey(c);
                continue;
            }
            Single<WeatherInfo> weatherInfoMaybe = weatherClient.getWeatherCondition(getLocationCode(c.getTimeZone()));
            weatherDisposable.add(weatherInfoMaybe
                    .onErrorReturn(new Function<Throwable, WeatherInfo>() {
                        @Override
                        public WeatherInfo apply(Throwable throwable) throws Throwable {
                            return null;
                        }
                    })
                    .subscribe((weatherInfo -> {
                        WorldClockCityInfo info = new WorldClockCityInfo(cities.get(index), weatherInfo, WeatherState.SUCCESS);
                        cachedCities.set(index, info);
                        weatherInfoBehaviorSubject.onNext(cachedCities);
                    }), throwable -> {
                        WorldClockCityInfo info = new WorldClockCityInfo(cities.get(index), null, WeatherState.ERROR);
                        cachedCities.set(index, info);
                        weatherInfoBehaviorSubject.onNext(cachedCities);
                    }));
            singles.add(weatherInfoMaybe);
        }
//        weatherDisposable.add(Single.merge(singles)
////                .subscribeOn(Schedulers.io())
//                .ignoreElements()
//                .subscribe(() -> {
//                            weatherInfoBehaviorSubject.onNext(cachedCities);
//                        },
//                        err -> {
//                            ArrayList<WorldClockCityInfo> worldClockCityInfos1 = new ArrayList<>();
//                            for (WorldClockCity city : cities) {
//                                worldClockCityInfos1.add(new WorldClockCityInfo(city, null, WeatherState.ERROR));
//                            }
//                            weatherInfoBehaviorSubject.onNext(worldClockCityInfos1);
//                        }));
    }

    @Override
    public void updateWeatherInfo(String timeZone) {
        int index = GeneralUtils.find(cachedCities, (city)->{
            return city.getWorldClockCity().getTimeZone().equals(timeZone);
        });
        if(index == -1) {
            return;
        }
        cachedCities
                .set(index, new WorldClockCityInfo(cachedCities.get(index).getWorldClockCity(), null, WeatherState.LOADING));
        weatherDisposable = new CompositeDisposable();
        weatherInfoBehaviorSubject.onNext(cachedCities);
        int locationKey = getLocationCode(timeZone);
        weatherDisposable.add(weatherClient.getWeatherCondition(locationKey)
                .subscribe((weatherInfo)-> {
                    WorldClockCityInfo info = cachedCities.get(index);
                    WorldClockCityInfo newInfo = new WorldClockCityInfo(info.getWorldClockCity(),
                            weatherInfo, WeatherState.SUCCESS);
                    ArrayList<WorldClockCityInfo> newList = new ArrayList<>();
                    int size = cachedCities.size();
                    for(int i = 0; i < size; i++) {
                        if(index == i) {
                            newList.add(newInfo);
                        } else {
                            newList.add(cachedCities.get(i));
                        }
                    }
                    cachedCities = newList;
                    weatherInfoBehaviorSubject.onNext(cachedCities);
                }, (err) -> {
                    WorldClockCityInfo info = cachedCities.get(index);
                    WorldClockCityInfo newInfo = new WorldClockCityInfo(info.getWorldClockCity(),
                            null, WeatherState.ERROR);
                    ArrayList<WorldClockCityInfo> newList = new ArrayList<>();
                    int size = cachedCities.size();
                    for(int i = 0; i < size; i++) {
                        if(index == i) {
                            newList.add(newInfo);
                        } else {
                            newList.add(cachedCities.get(i));
                        }
                    }
                    cachedCities = newList;
                    weatherInfoBehaviorSubject.onNext(cachedCities);
                }));
    }

    @Override
    public int getLocationCode(String timeZone) {
        Map<String, Integer> map = getLocationCodes();
        Integer value = map.get(timeZone);
        if (value != null) {
            return value;
        } else {
            return -1;
        }
    }

    @Override
    public Map<String, Integer> getLocationCodes() {
        String savedCodes = sharedPreferences.getString("SAVED_CODES", "");
        String[] split;
        Map<String, Integer> codeMap = new HashMap<>();
        if (savedCodes.equals("")) {
            split = new String[]{};
        } else {
            split = savedCodes.split(",");
        }
        for (String s : split) {
            String[] kv = s.split(":");
            String key = kv[0];
            Integer value = Integer.valueOf(kv[1]);
            codeMap.put(key, value);
        }
        return codeMap;
    }

    public Single<Integer> saveLocKey(WorldClockCity city) {
        //Why does chaining work instead of regular method calls?
        Single<Integer> locationKeyMaybe = weatherClient.getLocationCode(city.getCountryCode(), city.getCity())
//                .observeOn(Schedulers.io())
                .doOnSuccess(locationKey -> {
                    saveLocationCode(city.getTimeZone(), locationKey);
                });
        return locationKeyMaybe;
    }

    @Override
    public void saveLocationCode(String timeZone, int code) {
        String savedCodes = sharedPreferences.getString("SAVED_CODES", "");
        String[] split;

        if (savedCodes.equals("")) {
            split = new String[]{};
        } else {
            split = savedCodes.split(",");
        }
        ArrayList<String> splitList = new ArrayList<>(Arrays.asList(split));
        for (String s : splitList) {
            String[] kv = s.split(":");
            String key = kv[0];
            if (key.equals(timeZone)) {
                return;
            }
        }
        String value = timeZone + ":" + code;
        splitList.add(value);
        sharedPreferences.edit().putString("SAVED_CODES", TextUtils.join(",", splitList)).apply();
    }

    @Override
    public void swapCities(int firstIndex, int secondIndex) {
        String citiesList = sharedPreferences.getString("SAVED_CITIES", "");
        String[] split = citiesList.split(",");
        List<String> splitList = Arrays.asList(split);
        Collections.swap(splitList, firstIndex, secondIndex);
        sharedPreferences.edit().putString("SAVED_CITIES", TextUtils.join(",", splitList)).apply();
    }
}
