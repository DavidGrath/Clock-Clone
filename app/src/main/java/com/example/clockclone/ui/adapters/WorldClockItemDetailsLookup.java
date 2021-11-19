package com.example.clockclone.ui.adapters;

import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class WorldClockItemDetailsLookup extends ItemDetailsLookup<Long> {

    RecyclerView recyclerView;

    public WorldClockItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull @NotNull MotionEvent e) {
        View v = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if(v != null) {
            return ((WorldClockRecyclerAdapter.WorldClockViewHolder) recyclerView.getChildViewHolder(v))
                    .getItemDetails();
        }
        return null;
    }
}
