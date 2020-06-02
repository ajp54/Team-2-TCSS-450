package edu.uw.tcss450.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.ItemWeatherForecastBinding;
import edu.uw.tcss450.chatapp.ui.weather.WeeklyForecastWeatherBuilder;

public class MyWeatherRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherRecyclerViewAdapter.ViewHolder> {

    private final List<WeeklyForecastWeatherBuilder> mDays;

    public MyWeatherRecyclerViewAdapter(List<WeeklyForecastWeatherBuilder> items) {
        this.mDays = items;
    }

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_weather_forecast, parent, false));
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeeklyForecastWeatherBuilder day = mDays.get(position);
        holder.setDay(day);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mDays.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public ItemWeatherForecastBinding binding;


        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            binding= ItemWeatherForecastBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void setDay(final WeeklyForecastWeatherBuilder day) {
            binding.txtDate.setText(day.getDate());
            binding.txtTemperature.setText(day.getAvgTempF());
        }
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
