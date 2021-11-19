package com.example.clockclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.clockclone.R;
import com.example.clockclone.databinding.ActivityMainBinding;
import com.example.clockclone.ui.adapters.MainViewPagerAdapter;
import com.example.clockclone.ui.fragments.AlarmsFragment;
import com.example.clockclone.ui.fragments.StopwatchFragment;
import com.example.clockclone.ui.fragments.TimerFragment;
import com.example.clockclone.ui.fragments.WorldClockFragment;
import com.example.clockclone.util.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCE_FRAGMENT_POSITION = "fragment_position";
    public static final int REQUEST_CODE_PERMISSIONS = 100;

    private AlarmsFragment alarmsFragment = null;
    private WorldClockFragment worldClockFragment = null;
    private StopwatchFragment stopwatchFragment = null;
    private TimerFragment timerFragment = null;

    private SharedPreferences preferences;

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAllPermissions();
        }
        activityMainBinding  = ActivityMainBinding.inflate(getLayoutInflater());
        setSupportActionBar(activityMainBinding.toolbarMain);
        getSupportActionBar().setTitle(R.string.title_main_activity);

        preferences = getPreferences(Context.MODE_PRIVATE);

        //TODO Today 09/Sep 2021 I learned you shouldn't try to use savedInstanceState when working
        // with FragmentStateAdapter because it is responsible for handling state
//        if(savedInstanceState == null) {
            alarmsFragment = AlarmsFragment.createInstance();
            worldClockFragment = WorldClockFragment.createInstance();
            stopwatchFragment = StopwatchFragment.createInstance();
            timerFragment = TimerFragment.createInstance();
//        }

        final int[] titles = new int[]{R.string.tab_title_alarms, R.string.tab_title_world_clock, R.string.tab_title_stopwatch, R.string.tab_title_timer};
        Fragment[] list = new Fragment[]{alarmsFragment, worldClockFragment, stopwatchFragment, timerFragment};
        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(this, list);
        activityMainBinding.viewPagerMain.setAdapter(viewPagerAdapter);
        TabLayoutMediator tabLayoutMediator =
                new TabLayoutMediator(activityMainBinding.tabLayoutMain, activityMainBinding.viewPagerMain, (tab, position) -> {
                    tab.setText(titles[position]);
                });
//        activityMainBinding.viewPagerMain.setUserInputEnabled(false);
        tabLayoutMediator.attach();

        Intent intent = getIntent();
        int position;
        position = intent.getIntExtra(Constants.Extras.Titles.MAIN_ACTIVITY_FRAGMENT_INDEX, -1);
        if(position != -1) {
            activityMainBinding.viewPagerMain.setCurrentItem(position, false);
        } else {
            position = preferences.getInt(PREFERENCE_FRAGMENT_POSITION, -1);
            if(position == -1) {
                position = 0;
            }
            activityMainBinding.viewPagerMain.setCurrentItem(position);
        }
        setContentView(activityMainBinding.getRoot());
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences.edit()
                .putInt(PREFERENCE_FRAGMENT_POSITION, activityMainBinding.viewPagerMain.getCurrentItem())
                .apply();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAllPermissions() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        //TODO Possibly EasyPermissions
        for(String perm : permissions) {
            if(ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{perm}, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //TODO Convince user why I should see storage
    }
}