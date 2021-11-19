package com.example.clockclone.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clockclone.databinding.RecyclerviewWorldClockItemBinding;
import com.example.clockclone.domain.WeatherInfo;
import com.example.clockclone.domain.WorldClockCity;
import com.example.clockclone.domain.WorldClockCityInfo;
import com.example.clockclone.util.GeneralUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class WorldClockRecyclerAdapter extends RecyclerView.Adapter<WorldClockRecyclerAdapter.WorldClockViewHolder>{

    private List<WorldClockCityInfo> weatherCityInfoList;

    public void onItemMove(int fromPos, int toPos) {
        if (fromPos < toPos) {
            for (int i = fromPos; i < toPos; i++) {
                Collections.swap(weatherCityInfoList, i, i + 1);
            }
        } else {
            for (int i = fromPos; i > toPos; i--) {
                Collections.swap(weatherCityInfoList, i, i - 1);
            }
        }
        notifyItemMoved(fromPos, toPos);
    }

    public enum UNIT_TYPE {
        IMPERIAL,
        METRIC
    }
    private UNIT_TYPE unitType ;
    private SelectionTracker<Long> selectionTracker = null;
    private ItemTouchHelper itemTouchHelper;

    private final DateFormat sdf = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

    public interface WorldClockItemListener {
        void onRefreshClicked(WorldClockCityInfo worldClockCityInfo);
        void onWeatherInfoClicked(WorldClockCityInfo info, int position);
    }
    private WorldClockItemListener worldClockItemListener;

    // Commenting this out removed a NoSuchMethodException
//    public WorldClockRecyclerAdapter(List<WorldClockCityInfo> weatherCityInfoList, UNIT_TYPE unitType, ItemTouchHelper itemTouchHelper) {
//        this(weatherCityInfoList, unitType, itemTouchHelper, null);
//    }

    public WorldClockRecyclerAdapter(List<WorldClockCityInfo> weatherCityInfoList, UNIT_TYPE unitType, ItemTouchHelper itemTouchHelper, WorldClockItemListener worldClockItemListener) {
        setHasStableIds(true);
        this.weatherCityInfoList = weatherCityInfoList;
        this.unitType = unitType;
        this.itemTouchHelper = itemTouchHelper;
        this.worldClockItemListener = worldClockItemListener;
    }

    public void setWeatherCityInfoList(List<WorldClockCityInfo> weatherCityInfoList) {
        this.weatherCityInfoList = weatherCityInfoList;
        notifyDataSetChanged();
    }

    public void setUnitType(UNIT_TYPE unitType) {
        this.unitType = unitType;
        notifyDataSetChanged();
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public WorldClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerviewWorldClockItemBinding binding = RecyclerviewWorldClockItemBinding.inflate(inflater, parent, false);
        return new WorldClockViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorldClockRecyclerAdapter.WorldClockViewHolder holder, int position) {
        WorldClockCityInfo info = weatherCityInfoList.get(position);
        WorldClockCity city = info.getWorldClockCity();
        WeatherInfo weatherInfo  = info.getWeatherInfo();
        RecyclerviewWorldClockItemBinding binding = holder.binding;

        boolean selected = selectionTracker.isSelected((long) position);
        holder.itemView.setActivated(selected);
        if(selectionTracker.hasSelection()) {
            binding.checkboxWorldClockSelect.setVisibility(View.VISIBLE);
            binding.imageviewWorldClockDragHandle.setVisibility(View.VISIBLE);
            binding.framelayoutWeatherStatus.setVisibility(View.GONE);
        } else {
            binding.checkboxWorldClockSelect.setVisibility(View.GONE);
            binding.imageviewWorldClockDragHandle.setVisibility(View.GONE);
            binding.framelayoutWeatherStatus.setVisibility(View.VISIBLE);
        }
        binding.checkboxWorldClockSelect.setChecked(selected);
        //On pause for now
//        binding.imageviewWorldClockDragHandle.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                    itemTouchHelper.startDrag(holder);
//                }
//                return true;
//            }
//        });
        binding.textviewWorldClockTitle.setText(city.getCity());
        binding.textviewWorldClockRelative.setText(GeneralUtils.timeZoneDifference(city));
        Calendar c = Calendar.getInstance(Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(city.getTimeZone()));
        binding.textviewWorldClockTime.setText(sdf.format(c.getTime()));
        binding.imageviewWorldClockRefreshWeather.setOnClickListener((v) -> {
            if(worldClockItemListener != null) {
                worldClockItemListener.onRefreshClicked(weatherCityInfoList.get(holder.getAdapterPosition()));
            }
        });
        if(!selectionTracker.hasSelection()) {
            switch (info.getWeatherState()) {
                case NONE:
                    binding.framelayoutWeatherStatus.setVisibility(View.GONE);
                    binding.textviewWorldClockWeatherTemperature.setVisibility(View.GONE);
                    binding.progressbarWeatherDetails.setVisibility(View.GONE);
                    binding.imageviewWorldClockRefreshWeather.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.framelayoutWeatherStatus.setVisibility(View.VISIBLE);
                    binding.textviewWorldClockWeatherTemperature.setVisibility(View.GONE);
                    binding.imageviewWorldClockRefreshWeather.setVisibility(View.GONE);
                    binding.progressbarWeatherDetails.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    final char DEGREE_SIGN = 0x00B0;
                    String temperature;
                    if (unitType == UNIT_TYPE.METRIC) {
                        temperature = Long.toString(Math.round(weatherInfo.getMetric().getValue()));
                    } else {
                        temperature = Long.toString(Math.round(weatherInfo.getImperial().getValue()));
                    }
                    String sb = temperature + DEGREE_SIGN;
                    holder.binding.textviewWorldClockWeatherTemperature.setText(sb);
                    binding.framelayoutWeatherStatus.setVisibility(View.VISIBLE);
                    binding.progressbarWeatherDetails.setVisibility(View.GONE);
                    binding.imageviewWorldClockRefreshWeather.setVisibility(View.GONE);
                    binding.textviewWorldClockWeatherTemperature.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    binding.framelayoutWeatherStatus.setVisibility(View.VISIBLE);
                    binding.progressbarWeatherDetails.setVisibility(View.GONE);
                    binding.textviewWorldClockWeatherTemperature.setVisibility(View.GONE);
                    binding.imageviewWorldClockRefreshWeather.setVisibility(View.VISIBLE);
                    break;

            }
        }
        binding.textviewWorldClockWeatherTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worldClockItemListener.onWeatherInfoClicked(info, position);
            }
        });


//            switch (cachedState) {
//                case NONE:
//                    binding.linearlayoutWorldClockWeather.setVisibility(View.GONE);
//                    binding.progressbarWeatherDetails.setVisibility(View.GONE);
//                    break;
//                case LOADING:
////                    binding.linearlayoutWorldClockWeather.animate()
////                            .alpha(0F)
////                            .setDuration(Constants.AnimationDurations.SHORT)
////                            .setListener(new AnimatorListenerAdapter() {
////                                @Override
////                                public void onAnimationEnd(Animator animation) {
//                                    binding.framelayoutWeatherStatus.setVisibility(VI);
//                                    binding.linearlayoutWorldClockWeather.setVisibility(View.GONE);
////                                }
////                            });
////                    binding.progressbarWeatherDetails.animate()
////                            .alpha(1F)
////                            .setDuration(Constants.AnimationDurations.SHORT)
////                            .setListener(new AnimatorListenerAdapter() {
////                                @Override
////                                public void onAnimationEnd(Animator animation) {
//                                    binding.progressbarWeatherDetails.setVisibility(View.VISIBLE);
////                                }
////                            });
//                    break;
//                case SUCCESS:
//                    final char DEGREE_SIGN = 0x00B0;
//                    String temperature = Long.toString(Math.round(weatherInfo.getTemperature()));
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(temperature)
//                            .append(DEGREE_SIGN)
//                            .append(weatherInfo.getUnit());
////                    binding.progressbarWeatherDetails.animate()
////                            .alpha(1F)
////                            .setDuration(Constants.AnimationDurations.SHORT)
////                            .setListener(new AnimatorListenerAdapter() {
////                                @Override
////                                public void onAnimationEnd(Animator animation) {
//                                    binding.progressbarWeatherDetails.setVisibility(View.GONE);
////                                }
////                            });
////                    binding.linearlayoutWorldClockWeather.animate()
////                            .alpha(1F)
////                            .setDuration(Constants.AnimationDurations.SHORT)
////                            .setListener(new AnimatorListenerAdapter() {
////                                @Override
////                                public void onAnimationEnd(Animator animation) {
//                                    binding.linearlayoutWorldClockWeather.setVisibility(View.VISIBLE);
//                                    holder.binding.textviewWorldClockWeatherTemperature.setText(sb.toString());
////                                }
////                            });
//                    break;
//            }
    }

    @Override
    public int getItemCount() {
        return weatherCityInfoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public WorldClockCityInfo getItem(int position) {
        return weatherCityInfoList.get(position);
    }

    public static class WorldClockViewHolder extends RecyclerView.ViewHolder {
        final RecyclerviewWorldClockItemBinding binding;
        public WorldClockViewHolder(RecyclerviewWorldClockItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void forceCheckboxVisible() {
            binding.checkboxWorldClockSelect.setVisibility(View.VISIBLE);
            binding.imageviewWorldClockDragHandle.setVisibility(View.VISIBLE);
            binding.framelayoutWeatherStatus.setVisibility(View.GONE);
        }
        private ItemDetailsLookup.ItemDetails<Long> itemDetails = new ItemDetailsLookup.ItemDetails<Long>() {
            @Override
            public int getPosition() {
                return getAdapterPosition();
            }

            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Long getSelectionKey() {
                return getItemId();
            }
        };

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return itemDetails;
        }
    }
}
