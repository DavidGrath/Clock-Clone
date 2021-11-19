package com.example.clockclone.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.clockclone.databinding.RecyclerviewStopwatchSplitLapBinding;
import com.example.clockclone.domain.SplitLapTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SplitLapRecyclerAdapter extends RecyclerView.Adapter<SplitLapRecyclerAdapter.SplitLapViewHolder> {

    private List<SplitLapTime> splitLapTimeList;
    private SimpleDateFormat lessThanHourFormat = new SimpleDateFormat("mm:ss.SS");
    private SimpleDateFormat hourGreaterFormat = new SimpleDateFormat("kk:mm:ss.SS");
    private TimeZone GMT = TimeZone.getTimeZone("GMT");
    public SplitLapRecyclerAdapter(List<SplitLapTime> splitLapTimeList) {
        this.splitLapTimeList = splitLapTimeList;
        lessThanHourFormat.setTimeZone(GMT);
        hourGreaterFormat.setTimeZone(GMT);
    }

    @NonNull
    @Override
    public SplitLapViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerviewStopwatchSplitLapBinding binding = RecyclerviewStopwatchSplitLapBinding.inflate(inflater, parent, false);
        return new SplitLapViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull  SplitLapRecyclerAdapter.SplitLapViewHolder holder, int position) {
        SplitLapTime splitLapTime = splitLapTimeList.get(position);
        String index = Integer.toString(splitLapTime.getIndex());
        RecyclerviewStopwatchSplitLapBinding binding = holder.binding;
        binding.textviewSplitlapIndex.setText(index);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(new Date(splitLapTime.getSplitTime()));
        int hour = calendar.get(Calendar.HOUR);
        String splitFormatted;
        if (hour >= 1) {
            splitFormatted = hourGreaterFormat.format(splitLapTime.getSplitTime());
        } else {
            splitFormatted = lessThanHourFormat.format(splitLapTime.getSplitTime());
        }
        binding.textviewSplitlapSplit.setText(splitFormatted);
        calendar.setTime(new Date(splitLapTime.getLapTime()));
        hour = calendar.get(Calendar.HOUR);
        String lapFormatted;
        if (hour >= 1) {
            lapFormatted = hourGreaterFormat.format(splitLapTime.getLapTime());
        } else {
            lapFormatted = lessThanHourFormat.format(splitLapTime.getLapTime());
        }
        binding.textviewSplitlapLap.setText(lapFormatted);
    }

    @Override
    public int getItemCount() {
        return splitLapTimeList.size();
    }

    public void setSplitLapTimeList(List<SplitLapTime> splitLapTimeList) {
        this.splitLapTimeList = splitLapTimeList;
        notifyDataSetChanged();
    }

    public class SplitLapViewHolder extends RecyclerView.ViewHolder{
        
        public RecyclerviewStopwatchSplitLapBinding binding;

        public SplitLapViewHolder(RecyclerviewStopwatchSplitLapBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
