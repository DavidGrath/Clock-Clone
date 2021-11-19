package com.example.clockclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.clockclone.R;
import com.example.clockclone.databinding.ActivityAlarmBinding;

public class AlarmActivity extends AppCompatActivity {

    private ActivityAlarmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}