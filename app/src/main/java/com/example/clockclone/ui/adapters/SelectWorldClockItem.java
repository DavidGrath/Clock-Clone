package com.example.clockclone.ui.adapters;

import com.example.clockclone.domain.WorldClockCity;

public class SelectWorldClockItem {

    public static final int VIEW_TYPE_HEADER = 100;
    public static final int VIEW_TYPE_ITEM = 200;

    private int viewType;
    private WorldClockCity worldClockCity;
    private String header;

    public SelectWorldClockItem(int viewType, WorldClockCity worldClockCity, String header) {
        this.viewType = viewType;
        this.worldClockCity = worldClockCity;
        this.header = header;
    }

    public int getViewType() {
        return viewType;
    }

    public WorldClockCity getWorldClockCity() {
        return worldClockCity;
    }

    public String getHeader() {
        return header;
    }
}