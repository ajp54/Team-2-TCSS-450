package edu.uw.tcss450.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.ui.weather.Day;

public class MyWeatherRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherRecyclerViewAdapter.ViewHolder> {

    private Day[] mDays;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyWeatherRecyclerViewAdapter(Context context, Day[] days) {
        this.mInflater = LayoutInflater.from(context);
        this.mDays = days;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_weather_forecast, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = mDays[position];
        holder.myDate.setText(day.getmDay());
        holder.myTemp.setText(day.getmTemp());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mDays.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myDate;
        TextView myTemp;

        ViewHolder(View itemView) {
            super(itemView);
            myDate = itemView.findViewById(R.id.txt_date);
            myTemp = itemView.findViewById(R.id.txt_temperature);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Day getItem(int id) {
        return mDays[id];
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
