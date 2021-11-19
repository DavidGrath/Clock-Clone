package com.example.clockclone.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    final Fragment[] fragmentList;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Fragment[] fragmentList) {
        super(fragmentActivity);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList[position];
    }

    @Override
    public int getItemCount() {
        return fragmentList.length;
    }
}