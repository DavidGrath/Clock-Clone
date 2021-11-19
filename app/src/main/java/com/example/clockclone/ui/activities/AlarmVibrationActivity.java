package com.example.clockclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.example.clockclone.R;
import com.example.clockclone.databinding.ActivityAlarmVibrationBinding;
import com.example.clockclone.domain.ui.VibrationPatternUI;
import com.example.clockclone.ui.adapters.AlarmVibrationAdapter;
import com.example.clockclone.ui.viewmodels.AlarmVibrationViewModel;
import com.example.clockclone.ui.viewmodels.factories.AlarmVibrationViewModelFactory;
import com.example.clockclone.util.Constants;

import java.util.ArrayList;

public class AlarmVibrationActivity extends AppCompatActivity implements View.OnClickListener {

    private AlarmVibrationViewModel viewModel;
    private ActivityAlarmVibrationBinding binding;
    private AlarmVibrationAdapter adapter;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmVibrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarAlarmVibrate);
        getSupportActionBar().setTitle("Vibration");

        Intent intent = getIntent();
        int defaultVibrationEnabled = Constants.Vibrate.VIBRATION_FLAG_ENABLED;
        int defaultVibrationPattern = Constants.Vibrate.VIBRATION_BASIC_CALL;
        int input = intent.getIntExtra(Constants.Extras.Titles.VIBRATION_ACTIVITY_INPUT, defaultVibrationEnabled | defaultVibrationPattern);
        int patternEnabledMask = input & Constants.Vibrate.VIBRATION_MASK_FLAGS;
        boolean vibrationEnabled = (patternEnabledMask & Constants.Vibrate.VIBRATION_FLAG_ENABLED) > 0;
        int patternID = input & Constants.Vibrate.VIBRATION_MASK_PATTERN_ID;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        ArrayList<VibrationPatternUI> vibrationPatterns = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            VibrationPatternUI pattern =
                    new VibrationPatternUI(Constants.Vibrate.vibrationPatternTitles[i], Constants.Vibrate.PATTERNS[i]);
            vibrationPatterns.add(pattern);
        }
        viewModel = new AlarmVibrationViewModelFactory(patternID, vibrationEnabled).create(AlarmVibrationViewModel.class);
        adapter = new AlarmVibrationAdapter(vibrationPatterns, vibrationEnabled);
        binding.listviewAlarmVibrationPatterns.setAdapter(adapter);
        binding.listviewAlarmVibrationPatterns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setPatternID(position);
                VibrationPatternUI vibrationPatternUI = (VibrationPatternUI) adapter.getItem(position);
                vibrator.cancel();
                vibrator.vibrate(vibrationPatternUI.getPattern(), -1);
            }
        });
        binding.listviewAlarmVibrationPatterns.setItemChecked(patternID, true);
        binding.switchVibrateEnabled.setChecked(viewModel.isVibrationEnabled());
        binding.switchVibrateEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setVibrationEnabled(isChecked);
                adapter.setEnabled(isChecked);
            }
        });
        binding.buttonVibrateDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.buttonVibrateDone) {
            int vibrationEnabled = viewModel.isVibrationEnabled() ? Constants.Vibrate.VIBRATION_FLAG_ENABLED : 0;
            int patternID = viewModel.getPatternID();
            int output = vibrationEnabled | patternID;
            Intent result = new Intent();
            result.putExtra(Constants.Extras.Titles.VIBRATION_ACTIVITY_OUTPUT, output);
            setResult(RESULT_OK, result);
            finish();
        }
    }
}