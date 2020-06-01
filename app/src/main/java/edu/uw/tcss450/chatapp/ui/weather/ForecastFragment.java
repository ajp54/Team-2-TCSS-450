package edu.uw.tcss450.chatapp.ui.weather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.adapter.MyWeather24RecyclerViewAdapter;
import edu.uw.tcss450.chatapp.adapter.MyWeatherRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.databinding.FragmentForecastBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private FragmentForecastBinding binding;
    private View myView;
    private WeatherViewModel mCurrentWeatherModel;
    private WeatherViewModel mDailyWeatherModel;
    private WeatherViewModel mWeeklyWeatherModel;



    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        mDailyWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        mWeeklyWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        mCurrentWeatherModel.connectGet();
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
        binding = FragmentForecastBinding.bind((getView()));

        RecyclerView recyclerView24 = myView.findViewById(R.id.recyclerview);
        RecyclerView recyclerView = myView.findViewById(R.id.recyclerview);


        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(myView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        //LinearLayoutManager horizontalLayoutManager24
        //        = new LinearLayoutManager(myView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        //recyclerView24.setLayoutManager(horizontalLayoutManager24);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        mCurrentWeatherModel.addWeatherListObserver(getViewLifecycleOwner(), currentWeatherList -> {
                    if (!currentWeatherList.isEmpty()) {
                        /*binding.tempF.setText(CurrentWeatherBuilder.getTemp_F());
                        binding.windspeedMiles.setText(CurrentWeatherBuilder.getWindSpeedMiles());
                        binding.humidity.setText(CurrentWeatherBuilder.getHumidity());
                        binding.precipMM.setText(CurrentWeatherBuilder.getPrecipMM());*/
                    }
                });

        mDailyWeatherModel.addDailyWeatherListObserver(getViewLifecycleOwner(), dailyWeatherList -> {
                    if (!dailyWeatherList.isEmpty()) {
                        /*binding.date.setText(DailyForecastWeatherBuilder.getAvgTempF());
                        binding.avgtempF.setText(DailyForecastWeatherBuilder.getIconUrl());*/
                    }
                });

        mWeeklyWeatherModel.addWeeklyWeatherListObserver(getViewLifecycleOwner(), weeklyWeatherList -> {
                    if (!weeklyWeatherList.isEmpty()) {
                        /*binding.date.setText(WeeklyForecastWeatherBuilder.getDate());
                        binding.avgtempF.setText(WeeklyForecastWeatherBuilder.getAvgTempF());*/
                        binding.recyclerview.setAdapter(
                                new MyWeatherRecyclerViewAdapter(weeklyWeatherList));
                    }
                });
    }
}
