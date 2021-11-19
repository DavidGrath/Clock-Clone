package com.example.clockclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.example.clockclone.R;
import com.example.clockclone.databinding.ActivityAlarmSnoozeBinding;
import com.example.clockclone.util.Constants;

public class AlarmSnoozeActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean snoozeEnabled = true;
    private int snoozeRepeat = Constants.Snooze.SNOOZE_REPEAT_3_TIMES;
    private int snoozeInterval = Constants.Snooze.SNOOZE_INTERVAL_5_MINUTES;
    private ActivityAlarmSnoozeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmSnoozeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarAlarmSnooze);
        getSupportActionBar().setTitle("Snooze");

        Intent intent = getIntent();
        int snooze = intent.getIntExtra(Constants.Extras.Titles.SNOOZE_ACTIVITY_VALUE, -1);
        if(snooze > 0) {
            snoozeRepeat = snooze & Constants.Snooze.SNOOZE_MASK_REPEAT;
            snoozeInterval = snooze & Constants.Snooze.SNOOZE_MASK_INTERVAL;
            int enabledMask = snooze & Constants.Snooze.SNOOZE_MASK_ENABLED;
            boolean enabled = (enabledMask & Constants.Snooze.SNOOZE_FLAG_ENABLED) > 0;
            snoozeEnabled = enabled;
        }
        binding.switchSnoozeEnabled.setChecked(snoozeEnabled);
        setRadioButtonsEnabled();
        switch (snoozeInterval) {
            case Constants.Snooze.SNOOZE_INTERVAL_5_MINUTES:
                binding.radiobuttonIntervalGroup5Minutes.setChecked(true);
                break;
            case Constants.Snooze.SNOOZE_INTERVAL_10_MINUTES:
                binding.radiobuttonIntervalGroup10Minutes.setChecked(true);
                break;
            case Constants.Snooze.SNOOZE_INTERVAL_15_MINUTES:
                binding.radiobuttonIntervalGroup15Minutes.setChecked(true);
                break;
            case Constants.Snooze.SNOOZE_INTERVAL_30_MINUTES:
                binding.radiobuttonIntervalGroup30Minutes.setChecked(true);
                break;
        }
        switch (snoozeRepeat) {
            case Constants.Snooze.SNOOZE_REPEAT_3_TIMES:
                binding.radiobuttonRepeatGroup3Times.setChecked(true);
                break;
            case Constants.Snooze.SNOOZE_REPEAT_5_TIMES:
                binding.radiobuttonRepeatGroup5Times.setChecked(true);
                break;
            case Constants.Snooze.SNOOZE_REPEAT_FOREVER:
                binding.radiobuttonRepeatGroupForever.setChecked(true);
                break;
        }
        binding.radiobuttonIntervalGroup5Minutes.setOnClickListener(this);
        binding.radiobuttonIntervalGroup10Minutes.setOnClickListener(this);
        binding.radiobuttonIntervalGroup15Minutes.setOnClickListener(this);
        binding.radiobuttonIntervalGroup30Minutes.setOnClickListener(this);
        binding.radiobuttonRepeatGroup3Times.setOnClickListener(this);
        binding.radiobuttonRepeatGroup5Times.setOnClickListener(this);
        binding.radiobuttonRepeatGroupForever.setOnClickListener(this);
        binding.buttonAlarmSnoozeDone.setOnClickListener(this);
        binding.switchSnoozeEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            snoozeEnabled = isChecked;
            setRadioButtonsEnabled();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radiobutton_interval_group_5_minutes:
                if(((RadioButton) v).isChecked()) {
                    snoozeInterval = Constants.Snooze.SNOOZE_INTERVAL_5_MINUTES;
                }
                break;
            case R.id.radiobutton_interval_group_10_minutes:
                if(((RadioButton) v).isChecked()) {
                    snoozeInterval = Constants.Snooze.SNOOZE_INTERVAL_10_MINUTES;
                }
                break;
            case R.id.radiobutton_interval_group_15_minutes:
                if(((RadioButton) v).isChecked()) {
                    snoozeInterval = Constants.Snooze.SNOOZE_INTERVAL_15_MINUTES;
                }
                break;
            case R.id.radiobutton_interval_group_30_minutes:
                if(((RadioButton) v).isChecked()) {
                    snoozeInterval = Constants.Snooze.SNOOZE_INTERVAL_30_MINUTES;
                }
                break;
            case R.id.radiobutton_repeat_group_3_times:
                if(((RadioButton) v).isChecked()) {
                    snoozeRepeat = Constants.Snooze.SNOOZE_REPEAT_3_TIMES;
                }
                break;
            case R.id.radiobutton_repeat_group_5_times:
                if(((RadioButton) v).isChecked()) {
                    snoozeRepeat = Constants.Snooze.SNOOZE_REPEAT_5_TIMES;
                }
                break;
            case R.id.radiobutton_repeat_group_forever:
                if(((RadioButton) v).isChecked()) {
                    snoozeRepeat = Constants.Snooze.SNOOZE_REPEAT_FOREVER;
                }
                break;
            case R.id.button_alarm_snooze_done:
                int enabled = snoozeEnabled? Constants.Snooze.SNOOZE_FLAG_ENABLED : 0;
                int snooze = enabled | snoozeInterval | snoozeRepeat;
                Intent snoozeResult = new Intent();
                snoozeResult.putExtra(Constants.Extras.Titles.SNOOZE_ACTIVITY_VALUE, snooze);
                setResult(RESULT_OK, snoozeResult);
                finish();
        }
        int enabled = snoozeEnabled? Constants.Snooze.SNOOZE_FLAG_ENABLED : 0;
        int snooze = enabled | snoozeInterval | snoozeRepeat;
        Log.d("SNOOZE", Integer.toString(snooze, 2));
    }

    private void setRadioButtonsEnabled() {
        if(snoozeEnabled) {
            binding.radiobuttonRepeatGroup3Times.setEnabled(true);
            binding.radiobuttonRepeatGroup5Times.setEnabled(true);
            binding.radiobuttonRepeatGroupForever.setEnabled(true);
            binding.radiobuttonIntervalGroup5Minutes.setEnabled(true);
            binding.radiobuttonIntervalGroup10Minutes.setEnabled(true);
            binding.radiobuttonIntervalGroup15Minutes.setEnabled(true);
            binding.radiobuttonIntervalGroup30Minutes.setEnabled(true);
        } else {
            binding.radiobuttonRepeatGroup3Times.setEnabled(false);
            binding.radiobuttonRepeatGroup5Times.setEnabled(false);
            binding.radiobuttonRepeatGroupForever.setEnabled(false);
            binding.radiobuttonIntervalGroup5Minutes.setEnabled(false);
            binding.radiobuttonIntervalGroup10Minutes.setEnabled(false);
            binding.radiobuttonIntervalGroup15Minutes.setEnabled(false);
            binding.radiobuttonIntervalGroup30Minutes.setEnabled(false);
        }
    }
}