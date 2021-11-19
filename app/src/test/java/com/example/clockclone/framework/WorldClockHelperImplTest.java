package com.example.clockclone.framework;

import android.content.Context;
import android.os.Looper;

import com.example.clockclone.domain.WeatherState;
import com.example.clockclone.domain.WorldClockCityInfo;
import com.example.clockclone.domain.WorldClockCity;
import com.example.clockclone.domain.network.AccuweatherSearchResult;
import com.example.clockclone.framework.network.WeatherClientImpl;
import com.example.clockclone.framework.network.WeatherRetrofitClientTempImpl;
import com.example.clockclone.util.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.test.core.app.ApplicationProvider;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class WorldClockHelperImplTest {

    Context context;
    WeatherClientImpl weatherClient;
    WorldClockHelperImpl worldClockHelper;
    WeatherRetrofitClientTempImpl weatherRetrofitClientTemp;

    @Before
    public void setUp() throws Exception {

        context = ApplicationProvider.getApplicationContext();
        weatherClient = new WeatherClientImpl();
//        weatherRetrofitClientTemp = new WeatherRetrofitClientTempImpl();
        weatherRetrofitClientTemp = spy(WeatherRetrofitClientTempImpl.class);
        weatherRetrofitClientTemp.context = context;
        weatherClient.weatherRetrofitClient = weatherRetrofitClientTemp;
        worldClockHelper = new WorldClockHelperImpl(context, weatherClient);
    }


    @Test
    public void saveCity() {
        Scheduler scheduler = Schedulers.trampoline();
        TestObserver<String> maybeObserver = new TestObserver<>();
        worldClockHelper.saveCity("Africa/Lagos")
//                .observeOn(scheduler)
//                .subscribeOn(scheduler)
                .blockingSubscribe(maybeObserver);
        maybeObserver.assertValue("SUCCESS");
        maybeObserver.assertComplete();

        assertNotNull(context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).getString("SAVED_CITIES", null));
    }

    @Test
    public void saveCityBadNetworkReturnsFailure() {
        Exception network = new Exception("Network");
        Single<List<AccuweatherSearchResult>> error = Single.<List<AccuweatherSearchResult>>error(network)
                .delay(2, TimeUnit.SECONDS);
        when(weatherRetrofitClientTemp.getLocationDetails("NG", "Lagos")).thenReturn(error);
        TestObserver<String> testObserver = new TestObserver<>();
        worldClockHelper.saveCity("Africa/Lagos")
                .blockingSubscribe(testObserver);
        testObserver.assertValue("FAILURE");
    }

    @Test
    public void getWeatherInfo() {
        TestObserver<List<WorldClockCityInfo>> testObserver = new TestObserver<>();
        Observable<List<WorldClockCityInfo>> observable = worldClockHelper.getWeatherInfo();
        observable.subscribe(testObserver);
        List<WorldClockCityInfo> infoList = new ArrayList<>();
        testObserver.assertValue(infoList);
        worldClockHelper.saveCity("Africa/Lagos")
//                .subscribeOn(Schedulers.io())
                .blockingSubscribe(result -> {
                    assertEquals("SUCCESS", result);
                }, err -> {
                    throw err;
                });
        shadowOf(Looper.getMainLooper()).idle();
        WorldClockCityInfo worldClockCityInfo = new WorldClockCityInfo(new WorldClockCity("Africa/Lagos", "Lagos", "NG", "Nigeria"), null, WeatherState.NONE);
        ArrayList<WorldClockCityInfo> a = new ArrayList<>();
        a.add(worldClockCityInfo);
        testObserver.assertValues(infoList, a);
    }

    @Test
    public void testLocKey() {
        WorldClockCity worldClockCity = new WorldClockCity("Africa/Lagos", "Lagos", "NG", "Nigeria");
        TestObserver<Integer> testObserver = new TestObserver<>();
        worldClockHelper.saveLocKey(worldClockCity)
                .blockingSubscribe(testObserver);
        // SharedPreferences apply()
        shadowOf(Looper.getMainLooper()).idle();
        assertNotNull(context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).getString("SAVED_CODES", null));
        int expectedLocationKey = 4607;
        int actualLocationKey = worldClockHelper.getLocationCode("Africa/Lagos");
        assertEquals(expectedLocationKey, actualLocationKey);
        testObserver.assertValue(4607);
    }
}