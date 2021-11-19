package com.example.clockclone.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clockclone.databinding.RecyclerviewSelectWorldClockHeaderBinding;
import com.example.clockclone.databinding.RecyclerviewSelectWorldClockItemBinding;
import com.example.clockclone.domain.WorldClockCity;

import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectWorldClockRecyclerAdapter extends RecyclerView.Adapter<SelectWorldClockRecyclerAdapter.SelectWorldClockViewHolder> {


    private List<SelectWorldClockItem> worldClockItems;

    public interface ItemClickListener {
        void onItemClicked(int position, WorldClockCity city);
    }
    private ItemClickListener itemClickListener = null;

    public SelectWorldClockRecyclerAdapter(List<SelectWorldClockItem> worldClockItems) {
        this.worldClockItems = worldClockItems;
    }

    public SelectWorldClockRecyclerAdapter(List<SelectWorldClockItem> worldClockItems, ItemClickListener itemClickListener) {
        this(worldClockItems);
        this.itemClickListener = itemClickListener;
    }

    @Override
    public SelectWorldClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == SelectWorldClockItem.VIEW_TYPE_HEADER) {
            RecyclerviewSelectWorldClockHeaderBinding binding = RecyclerviewSelectWorldClockHeaderBinding.inflate(inflater, parent, false);
            return new SelectWorldClockViewHolder.Header(binding);
        } else {
            RecyclerviewSelectWorldClockItemBinding binding = RecyclerviewSelectWorldClockItemBinding.inflate(inflater, parent, false);
            return new SelectWorldClockViewHolder.Item(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectWorldClockRecyclerAdapter.SelectWorldClockViewHolder holder, int position) {
        if(holder.getItemViewType() == SelectWorldClockItem.VIEW_TYPE_HEADER) {
            String header = worldClockItems.get(position).getHeader();
            RecyclerviewSelectWorldClockHeaderBinding binding = ((SelectWorldClockViewHolder.Header) holder).binding;
            binding.textviewSelectWorldClockHeader.setText(header);
        } else {
            WorldClockCity city = worldClockItems.get(position).getWorldClockCity();
            RecyclerviewSelectWorldClockItemBinding binding = ((SelectWorldClockViewHolder.Item) holder).binding;
            String title = city.getCity() + " / " + city.getCountry();
            TimeZone timeZone = TimeZone.getTimeZone(city.getTimeZone());
            int rawOffset = timeZone.getRawOffset();
            int offset = rawOffset / 3_600_000;
            String sign = offset < 0 ? "-" : "+";
            offset = offset < 0 ? offset * -1 : offset;
            String offsetString = "GMT" + sign + offset;
            binding.textviewSelectWorldClockItemTitle.setText(title);
            binding.textviewSelectWorldClockItemOffset.setText(offsetString);
            binding.getRoot().setOnClickListener(v -> {
                itemClickListener.onItemClicked(position, city);
            });
        }
    }

    @Override
    public int getItemCount() {
        return worldClockItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return worldClockItems.get(position).getViewType();
    }

    public void setWorldClockItems(List<SelectWorldClockItem> worldClockItems) {
        this.worldClockItems = worldClockItems;
        notifyDataSetChanged();
    }

    static abstract class SelectWorldClockViewHolder extends RecyclerView.ViewHolder {

        public SelectWorldClockViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        static class Header extends SelectWorldClockViewHolder {

            private final RecyclerviewSelectWorldClockHeaderBinding binding;
            public Header(RecyclerviewSelectWorldClockHeaderBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
        static class Item extends SelectWorldClockViewHolder {

            private final RecyclerviewSelectWorldClockItemBinding binding;
            public Item(RecyclerviewSelectWorldClockItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
