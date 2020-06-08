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
import edu.uw.tcss450.chatapp.databinding.ItemWeatherForecast24Binding;
import edu.uw.tcss450.chatapp.ui.weather.DailyForecastWeatherBuilder;
import edu.uw.tcss450.chatapp.ui.weather.Hour;

public class MyWeather24RecyclerViewAdapter extends RecyclerView.Adapter<MyWeather24RecyclerViewAdapter.ViewHolder> {

    private final List<DailyForecastWeatherBuilder> mHours;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyWeather24RecyclerViewAdapter(List<DailyForecastWeatherBuilder> items) {
        this.mHours = items;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_weather_forecast24, parent, false));
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyForecastWeatherBuilder day = mHours.get(position);
        holder.setHour(day);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mHours.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public ItemWeatherForecast24Binding binding;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            binding= ItemWeatherForecast24Binding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void setHour(final DailyForecastWeatherBuilder hour) {
            binding.txtTime.setText(hour.getTime());
            binding.txtTemp.setText(hour.getAvgTempF());
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
