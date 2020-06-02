package edu.uw.tcss450.chatapp.ui.weather;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
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
                                       @NonNull Observer<? super List<CurrentWeatherBuilder>> currentObserver) {
        mCurrentWeatherList.observe(owner, currentObserver);
    }

    public void addDailyWeatherListObserver(@NonNull LifecycleOwner owner,
                                            @NonNull Observer<? super List<DailyForecastWeatherBuilder>> dailyObserver) {
        mDailyWeatherList.observe(owner, dailyObserver);
    }

    public void addWeeklyWeatherListObserver(@NonNull LifecycleOwner owner,
                                            @NonNull Observer<? super List<WeeklyForecastWeatherBuilder>> weeklyObserver) {
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

                // WEEKLY WEATHER
                if (data.has(getString.apply(R.string.keys_json_weather_weather))) {
                    JSONArray weather =
                            data.getJSONArray(getString.apply(
                                    R.string.keys_json_weather_weather));

                        for (int i = 0; i < weather.length(); i++) {
                            JSONObject weekly_weather =
                                    weather.getJSONObject(i);

                            // Day 0
                            // We need the hourly weather from this day for our daily forecast (3 hour increments)
                            if (i == 0) {
                                JSONArray hourly = weekly_weather.getJSONArray(
                                        getString.apply(
                                                R.string.keys_json_weather_hourly));

                                for (int j = 0; j < hourly.length(); j++) {
                                    JSONObject jsonHourlyWeather =
                                            hourly.getJSONObject(j);
                                    // Calls helper method to get the iconUrl
                                    String iconUrl = getIconUrlHourly(jsonHourlyWeather);
                                    // Adds the temperature and icon for each hourly increment of the day
                                    mDailyWeatherList.getValue().add(new DailyForecastWeatherBuilder.Builder(
                                            jsonHourlyWeather.getString(getString.apply(R.string.keys_json_weather_tempF)),
                                            iconUrl).build());
                                }
                                mWeeklyWeatherList.getValue().add(new WeeklyForecastWeatherBuilder.Builder(
                                        weekly_weather.getString(getString.apply(R.string.keys_json_weather_date)),
                                        weekly_weather.getString(getString.apply(R.string.keys_json_weather_avgtempF))).build());
                            // For the rest of the days
                            } else {
                                mWeeklyWeatherList.getValue().add(new WeeklyForecastWeatherBuilder.Builder(
                                        weekly_weather.getString(getString.apply(R.string.keys_json_weather_date)),
                                        weekly_weather.getString(getString.apply(R.string.keys_json_weather_avgtempF))).build());
                            }
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
        mDailyWeatherList.setValue(mDailyWeatherList.getValue());
        mWeeklyWeatherList.setValue(mWeeklyWeatherList.getValue());
    }

    public void connectGet(List<String> locationInfo) {
        Log.e("THE ZIP", locationInfo.get(1));
        String url = "https://api.worldweatheronline.com/premium/v1/weather.ashx?key=dc96b2428dc140f09a710254201405&q="
                + locationInfo.get(1) + "&format=json&num_of_days=7&fx24=no";
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

    /**
     *
     * @param error
     */
    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", "" + error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    /**
     *  A helper method to get the weather icon url
     *
     * @param theHour the hour object that is being looked inside of
     * @return A string containing the url to the weather icon
     * @throws JSONException throws to indicate a problem with the JSON API
     */
    private String getIconUrlHourly(JSONObject theHour) throws JSONException {
        JSONArray weatherIconUrl = theHour.getJSONArray("weatherIconUrl");
        JSONObject innerObject = weatherIconUrl.getJSONObject(0);
        String iconUrl = innerObject.getString("value");
        return iconUrl;
    }

}

