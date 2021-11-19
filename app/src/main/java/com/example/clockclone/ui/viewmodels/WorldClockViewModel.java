package com.example.clockclone.ui.viewmodels;

import android.app.Application;
import android.util.SparseBooleanArray;

import com.example.clockclone.data.MainRepository;
import com.example.clockclone.domain.WorldClockCity;
import com.example.clockclone.domain.WorldClockCityInfo;
import com.example.clockclone.domain.WeatherState;
import com.example.clockclone.framework.ClockClone;
import com.example.clockclone.ui.adapters.SelectWorldClockItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import io.reactivex.rxjava3.core.BackpressureStrategy;

public class WorldClockViewModel extends AndroidViewModel {

    private MainRepository mainRepository;
    private List<SelectWorldClockItem> fullCityList = new ArrayList<>();
    private MutableLiveData<List<SelectWorldClockItem>> _searchableList = new MutableLiveData<>(fullCityList);
    private LiveData<List<SelectWorldClockItem>> searchableList = _searchableList;

    public WorldClockViewModel(@NonNull @NotNull Application application) {
        super(application);
        ((ClockClone) application).daggerApplicationComponent.inject(this);
        groupList();
        _searchableList.postValue(fullCityList);
    }

    private void groupList() {
        List<WorldClockCity> fullList = mainRepository.getFullCityList();
        Map<String, ArrayList<WorldClockCity>> grouped = new HashMap<>();
        for(WorldClockCity worldClockCity: fullList) {
            String firstLetter = worldClockCity.getCity().substring(0,1).toLowerCase();
            if(grouped.get(firstLetter) == null) {
                grouped.put(firstLetter, new ArrayList<>());
            }
            grouped.get(firstLetter).add(worldClockCity);
        }
        ArrayList<String> keys = new ArrayList<>(grouped.keySet());
        Collections.sort(keys);
        for(String key: keys) {
            String upper = key.toUpperCase(Locale.getDefault());
            fullCityList.add(new SelectWorldClockItem(SelectWorldClockItem.VIEW_TYPE_HEADER, null, upper));
            ArrayList<WorldClockCity> cities = grouped.get(key);
            Collections.sort(cities, new Comparator<WorldClockCity>() {
                @Override
                public int compare(WorldClockCity o1, WorldClockCity o2) {
                    return o1.getCity().compareTo(o2.getCity());
                }
            });
            for(WorldClockCity city : cities) {
                fullCityList.add(new SelectWorldClockItem(SelectWorldClockItem.VIEW_TYPE_ITEM, city, null));
            }
        }
    }

    @Inject
    public void setMainRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public LiveData<List<WorldClockCityInfo>> getWorldClocks() {
        return LiveDataReactiveStreams.fromPublisher(mainRepository.getWorldClockList().toFlowable(BackpressureStrategy.BUFFER));
    }

    public void swapWorldClockItems(int firstIndex, int secondIndex) {
        mainRepository.swapWorldClockItems(firstIndex, secondIndex);
    }

    public void updateWeatherInfo() {
        mainRepository.updateWeatherInfo();
    }
    public LiveData<String> addCity(String timeZone) {
        return LiveDataReactiveStreams.fromPublisher(mainRepository.addCity(timeZone).toFlowable());
    }
    public void deleteCity(String timeZone) {
        mainRepository.deleteCity(timeZone);
    }

    public void deleteCities(List<String> timeZoneList) {
        mainRepository.deleteCities(timeZoneList);
    }

    public LiveData<List<SelectWorldClockItem>> getFullCityList() {
        return searchableList;
    }

    public void setLocalCity() {
        List<WorldClockCity> cities = mainRepository.getFullCityList();
        TimeZone timeZone = TimeZone.getDefault();
        for(WorldClockCity city: cities) {
            if(city.getTimeZone().equals(timeZone.getDisplayName()));
            SelectWorldClockItem item = new SelectWorldClockItem(SelectWorldClockItem.VIEW_TYPE_ITEM, city, null);
            _searchableList.postValue(Collections.singletonList(item));
        }
        _searchableList.postValue(new ArrayList<>());
    }

    public void searchCities(String query) {
        if(query == null || query.isEmpty()) {
            _searchableList.postValue(fullCityList);
        }
        List<WorldClockCity> cities = mainRepository.getFullCityList();
        List<WorldClockCity> results = new ArrayList<>();
        for(WorldClockCity worldClockCity: cities) {
            String city = worldClockCity.getCity().toLowerCase();
            String country = worldClockCity.getCountry().toLowerCase();
            if(city.contains(query.toLowerCase()) || country.contains(query.toLowerCase())) {
               results.add(worldClockCity);
            }

        }
        Collections.sort(results, new Comparator<WorldClockCity>() {
            @Override
            public int compare(WorldClockCity o1, WorldClockCity o2) {
                return o1.getCity().compareTo(o2.getCity());
            }
        });
        List<SelectWorldClockItem> items = new ArrayList<>();
        for(WorldClockCity city: results) {
            items.add(new SelectWorldClockItem(SelectWorldClockItem.VIEW_TYPE_ITEM, city, null));
        }
        _searchableList.postValue(items);
    }

    public void refreshWeather(String timeZone) {
        mainRepository.refreshWeather(timeZone);
    }
}
