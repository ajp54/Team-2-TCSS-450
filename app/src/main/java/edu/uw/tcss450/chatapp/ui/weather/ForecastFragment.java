package edu.uw.tcss450.chatapp.ui.weather;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedList;

import edu.uw.tcss450.chatapp.MainActivity;
import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.adapter.MyWeather24RecyclerViewAdapter;
import edu.uw.tcss450.chatapp.adapter.MyWeatherRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.adapter.MyRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private View myView;
    private MyRecyclerViewAdapter adapter;
    private WeatherViewModel mWeatherModel;
    private MyWeatherRecyclerViewAdapter adapter;
    private MyWeather24RecyclerViewAdapter adapter24;


    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        mWeatherModel.connectGet();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        myView = inflater.inflate(R.layout.fragment_forecast, container, false);

        return myView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Hour[] hours = {new Hour("12:00PM 55 F"), new Hour("1:00PM 64 F"),
                new Hour("2:00PM 62 F"),
                new Hour("3:00PM 53 F"), new Hour("4:00PM 70 F"),
                new Hour("5:00PM 71 F"), new Hour("6:00PM 73"),
                new Hour("7:00PM 71 F"), new Hour("8:00PM 73"),
                new Hour("9:00PM 71 F"), new Hour("10:00PM 73"),
                new Hour("11:00PM 71 F"), new Hour("12:00AM 73"),
                new Hour("1:00AM 71 F"), new Hour("2:00AM 73"),
                new Hour("3:00AM 71 F"), new Hour("4:00AM 73"),
                new Hour("5:00AM 71 F"), new Hour("6:00AM 73"),
                new Hour("7:00AM 71 F"), new Hour("8:00AM 73"),
                new Hour("9:00AM 71 F"), new Hour("10:00AM 73"),
                new Hour("11:00AM 71 F")
        };



        // here is where you would populate the days with actual data
        Day[] days = {new Day("Monday", "55 F"), new Day("Tuesday", "64 F"), new Day("Wednesday", "62 F"),
                                new Day("Thursday", "53 F"), new Day("Friday", "70 F"),
                                new Day("Saturday", "71 F"), new Day("Sunday", "73 F")};




        RecyclerView recyclerView24 = myView.findViewById(R.id.recyclerview24);
        RecyclerView recyclerView = myView.findViewById(R.id.recyclerview);


        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(myView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager horizontalLayoutManager24
                = new LinearLayoutManager(myView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView24.setLayoutManager(horizontalLayoutManager24);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        adapter24 = new MyWeather24RecyclerViewAdapter(myView.getContext(), hours);

        adapter = new MyWeatherRecyclerViewAdapter(myView.getContext(), days);

        recyclerView24.setAdapter(adapter24);
        recyclerView.setAdapter(adapter);
    }
}
