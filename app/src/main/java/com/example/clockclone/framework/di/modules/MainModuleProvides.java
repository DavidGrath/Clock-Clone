package com.example.clockclone.framework.di.modules;

import android.app.Application;
import android.content.Context;

import com.example.clockclone.BuildConfig;
import com.example.clockclone.framework.db.ClockCloneDatabase;
import com.example.clockclone.framework.di.scopes.ApplicationScope;
import com.example.clockclone.framework.network.WeatherRetrofitClient;
import com.example.clockclone.framework.network.WeatherRetrofitClientTempImpl;
import com.example.clockclone.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class MainModuleProvides {
    private Application application;

    public MainModuleProvides(Application application) {
        this.application = application;
    }

    @Provides
    public Context provideApplication() {
        return application;
    }

    @Provides
    public ClockCloneDatabase provideClockCloneDatabase() {
        return ClockCloneDatabase.getInstance(application);
    }

    @Provides
    public WeatherRetrofitClient provideWeatherRetrofitClient() {
        Interceptor tokenInterceptor = new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl url = original.url().newBuilder().addQueryParameter("apikey", BuildConfig.ACCUWEATHER_API_KEY).build();
                original = original.newBuilder().url(url).build();
                return chain.proceed(original);
            }
        };
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(tokenInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.Accuweather.BASE_URL)
                .client(client)
                .build();
        return retrofit.create(WeatherRetrofitClient.class);
    }
}
