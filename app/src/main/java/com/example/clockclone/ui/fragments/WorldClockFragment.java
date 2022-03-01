package com.example.clockclone.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clockclone.R;
import com.example.clockclone.databinding.FragmentWorldClockBinding;
import com.example.clockclone.domain.WorldClockCityInfo;
import com.example.clockclone.ui.activities.SelectWorldClockCityActivity;
import com.example.clockclone.ui.adapters.WorldClockItemDetailsLookup;
import com.example.clockclone.ui.adapters.WorldClockRecyclerAdapter;
import com.example.clockclone.ui.viewmodels.WorldClockViewModel;
import com.example.clockclone.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorldClockFragment extends Fragment implements View.OnClickListener {

    private FragmentWorldClockBinding fragmentWorldClockBinding;
    private WorldClockViewModel viewModel;
    private ActivityResultLauncher<Object> activityResultLauncher;
    private WorldClockRecyclerAdapter adapter;
    private SelectionTracker<Long> selectionTracker = null;
    private FragmentActivity activity;
    private SharedPreferences sharedPreferences;

    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//            viewModel.swapWorldClockItems(fromPos, toPos);
            return true;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    });

    private final ActivityResultContract<Object, String> contract = new ActivityResultContract<Object, String>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Object input) {
            Intent intent = new Intent(context, SelectWorldClockCityActivity.class);
            return intent;
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            if(resultCode == Activity.RESULT_OK) {
                String timeZone = intent.getStringExtra("TIMEZONE");
                return timeZone;
            }
            return null;
        }
    };

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(Constants.Preferences.WEATHER_UNIT_TYPE)) {
                WorldClockRecyclerAdapter.UNIT_TYPE unitType;
                if(sharedPreferences.getString(key, "METRIC").equals("METRIC")) {
                    unitType = WorldClockRecyclerAdapter.UNIT_TYPE.METRIC;
                } else {
                    unitType = WorldClockRecyclerAdapter.UNIT_TYPE.IMPERIAL;
                }
                adapter.setUnitType(unitType);
            }
        }
    };
    boolean inActionMode = false;
    ActionMode actionMode;
    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_world_clock_contextual, menu);
            inActionMode = true;
            RecyclerView recyclerView = fragmentWorldClockBinding.recyclerviewWorldClock;
            int count = recyclerView.getChildCount();
            for(int i =0; i < count; i++) {
                WorldClockRecyclerAdapter.WorldClockViewHolder viewHolder = (WorldClockRecyclerAdapter.WorldClockViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                viewHolder.forceCheckboxVisible();
            }
            mode.setTitle(Integer.toString(selectionTracker.getSelection().size()));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if(item.getItemId() == R.id.menuitem_world_clock_contextual_delete) {
                Iterator<Long> keys = selectionTracker.getSelection().iterator();
                if(!selectionTracker.hasSelection()) {
                    return true;
                }
                ArrayList<String> timeZoneList = new ArrayList<>();
                while (keys.hasNext()) {
                    int key = (int) (long) keys.next();
                    timeZoneList.add(adapter.getItem(key).getWorldClockCity().getTimeZone());
                }
                viewModel.deleteCities(timeZoneList);
                actionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selectionTracker.clearSelection();
            adapter.notifyDataSetChanged();
            inActionMode = false;
            actionMode = null;
        }
    };

    //In the end, I couldn't find a way to simply update all the checkboxes' visibility without causing an infinite loop
    //stack overflow, or just crashing. Sigh.
    //Update: finally found a workaround. Feels hacky, but I've wasted enough time.
    SelectionTracker.SelectionObserver<Long> selectionObserver = new SelectionTracker.SelectionObserver<Long>() {

        @Override
        public void onSelectionChanged() {
            Selection<Long> selection = selectionTracker.getSelection();
            int size = selection.size();
            if (selectionTracker.hasSelection()) {
                if(actionMode == null) {
                    actionMode = requireActivity().startActionMode(actionModeCallback);
                } else {
                    actionMode.setTitle(Integer.toString(size));
                }
            } else {
                if(inActionMode) {
                    actionMode.finish();
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentWorldClockBinding = FragmentWorldClockBinding.inflate(inflater, container, false);
        WorldClockRecyclerAdapter.WorldClockItemListener worldClockItemListener = new WorldClockRecyclerAdapter.WorldClockItemListener() {
            @Override
            public void onRefreshClicked(WorldClockCityInfo worldClockCityInfo) {
                viewModel.refreshWeather(worldClockCityInfo.getWorldClockCity().getTimeZone());
            }

            @Override
            public void onWeatherInfoClicked(WorldClockCityInfo info, int position) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(info.getWeatherInfo().getMobileLink()));
                startActivity(intent);
            }
        };

        sharedPreferences = requireContext().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        String unitTypeString = sharedPreferences.getString(Constants.Preferences.WEATHER_UNIT_TYPE, "METRIC");
        WorldClockRecyclerAdapter.UNIT_TYPE unitType;
        if(unitTypeString.equals("METRIC")) {
            unitType = WorldClockRecyclerAdapter.UNIT_TYPE.METRIC;
        } else {
            unitType = WorldClockRecyclerAdapter.UNIT_TYPE.IMPERIAL;
        }
        RecyclerView worldClockRecyclerView = fragmentWorldClockBinding.recyclerviewWorldClock;
        viewModel = new ViewModelProvider(requireActivity()).get(WorldClockViewModel.class);
        adapter = new WorldClockRecyclerAdapter(new ArrayList<>(), unitType, itemTouchHelper, worldClockItemListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        //Complicated. Paused since not priority
//        itemTouchHelper.attachToRecyclerView(worldClockRecyclerView);
        worldClockRecyclerView.setAdapter(adapter);
        worldClockRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), linearLayoutManager.getOrientation()));
        worldClockRecyclerView.setLayoutManager(linearLayoutManager);
        selectionTracker = new SelectionTracker.Builder<Long>(
                "worldclock-selection",
                worldClockRecyclerView,
                new StableIdKeyProvider(worldClockRecyclerView),
                new WorldClockItemDetailsLookup(worldClockRecyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
//                .withOnDragInitiatedListener(new OnDragInitiatedListener() {
//                    @Override
//                    public boolean onDragInitiated(@NonNull @NotNull MotionEvent e) {
//                        View v = worldClockRecyclerView.findChildViewUnder(e.getX(), e.getY());
//                        if(v != null) {
//                            RecyclerView.ViewHolder vh = worldClockRecyclerView.findContainingViewHolder(v);
//                            Log.d("POS", ""+vh.getAdapterPosition());
//                        }
//                        return false;
//                    }
//                })
                .build();
        selectionTracker.addObserver(selectionObserver);
        adapter.setSelectionTracker(selectionTracker);
        activity = requireActivity();
        LiveData<List<WorldClockCityInfo>> weatherCityInfoListLiveData = viewModel.getWorldClocks();
        weatherCityInfoListLiveData.observe(activity, weatherCityInfoList -> {
            adapter.setWeatherCityInfoList(weatherCityInfoList);
        });

        ActivityResultCallback<String> activityResultCallback =  new ActivityResultCallback<String>() {
            @Override
            public void onActivityResult(String timeZone) {
                if(timeZone != null) {
                    LiveData<String> liveData = viewModel.addCity(timeZone);
                    liveData.observe(requireActivity(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if (s.equals("FAILURE")) {
                                Toast.makeText(requireContext(), "Error. Please try again", Toast.LENGTH_SHORT).show();
                            }
                            liveData.removeObserver(this);
                        }
                    });
                }
            }
        };
        activityResultLauncher = registerForActivityResult(contract, activityResultCallback);
        fragmentWorldClockBinding.fabWorldClock.setOnClickListener(this);
        return fragmentWorldClockBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectionTracker.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_world_clock, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem unitTypeItem = menu.findItem(R.id.menuitem_world_clock_settings);
        String currentUnitType = sharedPreferences.getString(Constants.Preferences.WEATHER_UNIT_TYPE, "METRIC");
        unitTypeItem.setTitle(currentUnitType.equals("METRIC")? "Metric" : "Imperial");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuitem_world_clock_edit) {
            if(actionMode == null) {
                actionMode = requireActivity().startActionMode(actionModeCallback);
            }
        } else if(item.getItemId() == R.id.menuitem_world_clock_update) {
            viewModel.updateWeatherInfo();
        } else if(item.getItemId() == R.id.menuitem_world_clock_settings) {
            String currentUnitType = sharedPreferences.getString(Constants.Preferences.WEATHER_UNIT_TYPE, "METRIC");
            String newUnitType = currentUnitType.equals("METRIC") ? "IMPERIAL" : "METRIC";
            sharedPreferences.edit()
                    .putString(Constants.Preferences.WEATHER_UNIT_TYPE, newUnitType)
                    .apply();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_world_clock) {
            activityResultLauncher.launch(null);
        }
    }

    public static WorldClockFragment createInstance() {
        WorldClockFragment worldClockFragment = new WorldClockFragment();
        return worldClockFragment;
    }
}
