package com.example.clockclone.framework;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.clockclone.framework.di.components.ApplicationComponent;
import com.example.clockclone.framework.di.components.DaggerApplicationComponent;
import com.example.clockclone.framework.di.modules.MainModuleBinds;
import com.example.clockclone.framework.di.modules.MainModuleProvides;
import com.example.clockclone.util.Constants;

public class ClockClone extends Application {
    public ApplicationComponent daggerApplicationComponent = DaggerApplicationComponent.builder()
            .mainModuleProvides(new MainModuleProvides(this))
            .build();

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(!preferences.contains(Constants.Preferences.SUNDAY_FIRST)) {
            editor.putBoolean(Constants.Preferences.SUNDAY_FIRST, true);
        }
        if(!preferences.contains(Constants.Preferences.TIMER_PRESETS)) {
            editor.putString(Constants.Preferences.TIMER_PRESETS, "");
        }
        editor.apply();

    }
}
