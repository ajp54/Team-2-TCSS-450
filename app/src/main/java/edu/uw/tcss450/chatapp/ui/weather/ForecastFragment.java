package edu.uw.tcss450.chatapp.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentForecastBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private FragmentForecastBinding binding;
    private View myView;
    private WeatherViewModel mWeatherModel;



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
        FragmentForecastBinding binding = FragmentForecastBinding.bind((getView()));

        mWeatherModel.addWeatherListObserver(getViewLifecycleOwner(),
                (weatherList -> {
                    if (!weatherList.isEmpty()) {
                        binding.tempF.setText(CurrentWeatherBuilder.getTemp_F());
                        binding.windspeedMiles.setText(CurrentWeatherBuilder.getWindSpeedMiles());
                        binding.humidity.setText(CurrentWeatherBuilder.getHumidity());
                        binding.precipMM.setText(CurrentWeatherBuilder.getPrecipMM());
                    }
                }),
                (dailyWeatherList -> {
                    if (!dailyWeatherList.isEmpty()) {
                        binding.date.setText(DailyForecastWeatherBuilder.getDate());
                        binding.avgtempF.setText(DailyForecastWeatherBuilder.getAvgTempF());
                    }
                }),
                (weeklyWeatherList -> {
                    if (!weeklyWeatherList.isEmpty()) {
                        binding.date0.setText(WeeklyForecastWeatherBuilder.getDate());
                        binding.avgtempF0.setText(WeeklyForecastWeatherBuilder.getAvgTempF());
                    }
                }));
    }
}
