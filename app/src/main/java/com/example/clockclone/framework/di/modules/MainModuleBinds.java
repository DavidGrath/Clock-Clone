package com.example.clockclone.framework.di.modules;

import com.example.clockclone.data.WorldClockHelper;
import com.example.clockclone.data.network.WeatherClient;
import com.example.clockclone.framework.WorldClockHelperImpl;
import com.example.clockclone.framework.network.WeatherClientImpl;
import com.example.clockclone.framework.network.WeatherRetrofitClient;
import com.example.clockclone.framework.network.WeatherRetrofitClientTempImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MainModuleBinds {
    @Binds
    public abstract WorldClockHelper bindWorldClockHelper(WorldClockHelperImpl worldClockHelper);
    @Binds
    public abstract WeatherClient bindWeatherClient(WeatherClientImpl weatherClient);
}
