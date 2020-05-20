package edu.uw.tcss450.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.ui.weather.Hour;

public class MyWeather24RecyclerViewAdapter extends RecyclerView.Adapter<MyWeather24RecyclerViewAdapter.ViewHolder> {

    private Hour[] mHours;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyWeather24RecyclerViewAdapter(Context context, Hour[] hours) {
        this.mInflater = LayoutInflater.from(context);
        this.mHours = hours;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_weather_forecast24, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hour hour = mHours[position];
        //System.out.println(hour.getmHour());
        holder.myHour.setText(hour.getmHour());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mHours.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myHour;

        ViewHolder(View itemView) {
            super(itemView);
            myHour = itemView.findViewById(R.id.txt_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Hour getItem(int id) {
        return mHours[id];
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
