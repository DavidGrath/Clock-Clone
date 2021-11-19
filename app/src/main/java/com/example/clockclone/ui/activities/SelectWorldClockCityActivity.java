package com.example.clockclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.clockclone.R;
import com.example.clockclone.databinding.ActivitySelectWorldClockCityBinding;
import com.example.clockclone.domain.WorldClockCity;
import com.example.clockclone.ui.adapters.SelectWorldClockItem;
import com.example.clockclone.ui.adapters.SelectWorldClockRecyclerAdapter;
import com.example.clockclone.ui.viewmodels.WorldClockViewModel;

import java.util.ArrayList;
import java.util.List;

public class SelectWorldClockCityActivity extends AppCompatActivity implements View.OnClickListener {

    private WorldClockViewModel viewModel;
    private ActivitySelectWorldClockCityBinding binding;
    SelectWorldClockRecyclerAdapter adapter;

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(query.isEmpty()) {
                return true;
            } else {
                viewModel.searchCities(query);
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            viewModel.searchCities(newText);
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectWorldClockCityBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbarSelectWorldClockCity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add city");
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(WorldClockViewModel.class);
        viewModel.getFullCityList().observe(this, list -> {
            adapter.setWorldClockItems(list);
        });
        adapter = new SelectWorldClockRecyclerAdapter(new ArrayList<>(), (position, item) -> {
            Intent data = new Intent()
                    .putExtra("TIMEZONE", item.getTimeZone());
            setResult(RESULT_OK, data);
            finish();
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.searchviewSelectWorldClockCities.setOnQueryTextListener(queryTextListener);
        binding.recyclerviewWorldClockSelectItem.setLayoutManager(linearLayoutManager);
        binding.recyclerviewWorldClockSelectItem.setAdapter(adapter);
        binding.recyclerviewWorldClockSelectItem.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        binding.imageviewSelectWorldClockLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imageview_select_world_clock_location) {
            viewModel.setLocalCity();
        }
    }
}