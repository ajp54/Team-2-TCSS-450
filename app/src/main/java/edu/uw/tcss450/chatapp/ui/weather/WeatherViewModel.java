package edu.uw.tcss450.chatapp.ui.weather;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import edu.uw.tcss450.chatapp.R;

public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<List<CurrentWeatherBuilder>> mCurrentWeatherList;
    private MutableLiveData<List<DailyForecastWeatherBuilder>> mDailyWeatherList;
    private MutableLiveData<List<WeeklyForecastWeatherBuilder>> mWeeklyWeatherList;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        mCurrentWeatherList = new MutableLiveData<>();
        mDailyWeatherList = new MutableLiveData<>();
        mWeeklyWeatherList = new MutableLiveData<>();

        mCurrentWeatherList.setValue(new ArrayList<>());
        mDailyWeatherList.setValue(new ArrayList<>());
        mWeeklyWeatherList.setValue(new ArrayList<>());
    }

    public void addWeatherListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<CurrentWeatherBuilder>> currentObserver,
                                       @NonNull Observer<? super List<DailyForecastWeatherBuilder>> dailyObserver,
                                       @NonNull Observer<? super List<WeeklyForecastWeatherBuilder>> weeklyObserver) {
        mCurrentWeatherList.observe(owner, currentObserver);
        mDailyWeatherList.observe(owner, dailyObserver);
        mWeeklyWeatherList.observe(owner, weeklyObserver);
    }

    private void handleResult(final JSONObject result) {
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;

            if (root.has(getString.apply(R.string.keys_json_weather_data))) {
                JSONObject data =
                        root.getJSONObject(getString.apply(
                                R.string.keys_json_weather_data));
                // CURRENT WEATHER
                if (data.has(getString.apply(R.string.keys_json_weather_current_condition))) {
                        JSONArray current_condition = data.getJSONArray(
                                getString.apply(R.string.keys_json_weather_current_condition));

                    for (int i = 0; i < current_condition.length(); i++) {
                        JSONObject jsonCurrentWeather = current_condition.getJSONObject(i);
                        mCurrentWeatherList.getValue().add(new CurrentWeatherBuilder.Builder(
                                jsonCurrentWeather.getString(getString.apply(R.string.keys_json_weather_temp_F)),
                                jsonCurrentWeather.getString(getString.apply(R.string.keys_json_weather_windspeedMiles)),
                                jsonCurrentWeather.getString(getString.apply(R.string.keys_json_weather_humidity)),
                                jsonCurrentWeather.getString(getString.apply(R.string.keys_json_weather_precipMM))).build());
                    }
                }
                // DAILY WEATHER
                if (data.has(getString.apply(R.string.keys_json_weather_weather))) {
                    JSONArray forecast =
                            data.getJSONArray(getString.apply(
                                    R.string.keys_json_weather_weather));
                    if (forecast.get(0) != null) {
                        Log.e("IM HERE", "ERROR");
                        JSONObject daily_weather =
                                forecast.getJSONObject(1);
                        for (int j = 0; j < daily_weather.length(); j++) {
                            JSONObject jsonDailyWeather = daily_weather.getJSONObject(String.valueOf(j));
                            mDailyWeatherList.getValue().add(new DailyForecastWeatherBuilder.Builder(
                                    jsonDailyWeather.getString(getString.apply(R.string.keys_json_weather_date)),
                                    jsonDailyWeather.getString(getString.apply(R.string.keys_json_weather_avgtempF))).build());
                        }
                    }
                }
                // WEEKLY WEATHER
                if (data.has(getString.apply(R.string.keys_json_weather_weather))) {
                    JSONArray weekly_condition = data.getJSONArray(
                            getString.apply(R.string.keys_json_weather_weather));

                    for (int k = 0; k < weekly_condition.length(); k++) {
                        JSONObject jsonWeeklyWeather = weekly_condition.getJSONObject(k);
                        mWeeklyWeatherList.getValue().add(new WeeklyForecastWeatherBuilder.Builder(
                                jsonWeeklyWeather.getString(getString.apply(R.string.keys_json_weather_date)),
                                jsonWeeklyWeather.getString(getString.apply(R.string.keys_json_weather_avgtempF))).build());
                    }
                }


            } else {
                Log.e("ERROR IN WEATHERVIEWMODEL", "No data array");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR IN WEATHERVIEWMODEL", e.getMessage());
        }
        mCurrentWeatherList.setValue(mCurrentWeatherList.getValue());
    }

    public void connectGet() {
        String url = "https://api.worldweatheronline.com/premium/v1/weather.ashx?key=dc96b2428dc140f09a710254201405&q=98402&format=json&num_of_days=7&fx24=yes";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::handleResult,
                this::handleError);

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }

    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }
}

