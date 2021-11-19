package com.example.clockclone.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.example.clockclone.domain.ui.VibrationPatternUI;

import java.util.List;

public class AlarmVibrationAdapter extends BaseAdapter {

    private List<VibrationPatternUI> vibrationPatterns;
    private boolean enabled;

    public AlarmVibrationAdapter(List<VibrationPatternUI> vibrationPatterns, boolean enabled) {
        this.vibrationPatterns = vibrationPatterns;
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return vibrationPatterns.size();
    }

    @Override
    public Object getItem(int position) {
        return vibrationPatterns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VibrationPatternUI vibrationPattern = vibrationPatterns.get(position);
        RadioButton radioButton = new RadioButton(parent.getContext());
        radioButton.setText(vibrationPattern.getTitle());
        radioButton.setFocusableInTouchMode(false);
        radioButton.setFocusable(false);
        radioButton.setClickable(false);
        radioButton.setEnabled(enabled);
        return radioButton;
    }
}
