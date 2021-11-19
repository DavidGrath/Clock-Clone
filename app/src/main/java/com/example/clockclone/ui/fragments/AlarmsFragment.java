package com.example.clockclone.ui.fragments;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.clockclone.R;
import com.example.clockclone.databinding.FragmentAlarmsBinding;
import com.example.clockclone.domain.room.Alarm;
import com.example.clockclone.domain.ui.AlarmSummaryUI;
import com.example.clockclone.ui.activities.AddEditAlarmActivity;
import com.example.clockclone.ui.adapters.AlarmsRecyclerAdapter;
import com.example.clockclone.ui.viewmodels.AlarmsViewModel;
import com.example.clockclone.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmsFragment extends Fragment implements View.OnClickListener {

    private AlarmsViewModel viewModel;
    private FragmentAlarmsBinding fragmentAlarmsBinding;
    private AlarmsRecyclerAdapter adapter;
    private AlarmManager alarmManager;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(Constants.Preferences.SUNDAY_FIRST)) {
                boolean sundayFirst = sharedPreferences.getBoolean(Constants.Preferences.SUNDAY_FIRST, true);
                requireActivity().invalidateOptionsMenu();
                adapter.setSundayFirst(sundayFirst);
            }
        }
    };

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentAlarmsBinding = FragmentAlarmsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(AlarmsViewModel.class);
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        preferences = requireContext().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        boolean sundayFirst = preferences.getBoolean(Constants.Preferences.SUNDAY_FIRST, true);
        adapter = new AlarmsRecyclerAdapter(new ArrayList<>(), sundayFirst, new AlarmsRecyclerAdapter.OnAlarmClickListener() {
            @Override
            public void onAlarmClicked(AlarmSummaryUI alarmSummaryUI, int position) {
                Intent intent = new Intent(requireContext(), AddEditAlarmActivity.class);
                intent.putExtra(Constants.Extras.Titles.EDIT_ALARM_ID, alarmSummaryUI.getId());
                startActivity(intent);
            }

            @Override
            public void onAlarmActiveToggled(AlarmSummaryUI alarmSummaryUI, boolean activeState) {
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), REQUEST_CODE_SET_ALARM, )
//                alarmManager.cancel();
                //Cancel Alarm
                viewModel.setAlarmEnabled(alarmSummaryUI.getId(), activeState);
            }
        });
        viewModel.getAlarmSummaries().observe(requireActivity(), alarmSummaries -> {
            if(alarmSummaries.isEmpty()) {
                fragmentAlarmsBinding.textviewAlarmsPlaceholder.setVisibility(View.VISIBLE);
                fragmentAlarmsBinding.recyclerviewAlarms.setVisibility(View.GONE);
                return;
            } else {
                fragmentAlarmsBinding.textviewAlarmsPlaceholder.setVisibility(View.GONE);
                fragmentAlarmsBinding.recyclerviewAlarms.setVisibility(View.VISIBLE);
            }
            adapter.setAlarmList(alarmSummaries);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        RecyclerView alarmsRecyclerView = fragmentAlarmsBinding.recyclerviewAlarms;
        alarmsRecyclerView.setAdapter(adapter);
        alarmsRecyclerView.setLayoutManager(linearLayoutManager);
        alarmsRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), linearLayoutManager.getOrientation()));
        fragmentAlarmsBinding.fabAlarms.setOnClickListener(this);
        return fragmentAlarmsBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_alarms, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem firstDayItem = menu.findItem(R.id.menuitem_alarms_first_day);
        boolean sundayFirstDay = preferences.getBoolean(Constants.Preferences.SUNDAY_FIRST, true);
        String firstDayTitle = sundayFirstDay ? "Sunday first day" : "Monday first day";
        firstDayItem.setTitle(firstDayTitle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if(item.getItemId() == R.id.menuitem_alarms_delete) {
            viewModel.deleteAllAlarms();
            return true;
        } else if(item.getItemId() == R.id.menuitem_alarms_first_day) {
            boolean sundayFirstDay = preferences.getBoolean(Constants.Preferences.SUNDAY_FIRST, true);
            preferences.edit()
                    .putBoolean(Constants.Preferences.SUNDAY_FIRST, !sundayFirstDay)
                    .apply();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_alarms) {
            Intent intent = new Intent(requireContext(), AddEditAlarmActivity.class);
            startActivity(intent);
        }
    }

    public static AlarmsFragment createInstance() {
        AlarmsFragment alarmsFragment = new AlarmsFragment();
        return alarmsFragment;
    }
}
