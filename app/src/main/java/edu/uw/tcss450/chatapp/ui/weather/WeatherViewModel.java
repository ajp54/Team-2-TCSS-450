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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import edu.uw.tcss450.chatapp.AuthActivity;
import edu.uw.tcss450.chatapp.MainActivity;
import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;

public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<List<WeatherBuilder>> mWeatherList;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        mWeatherList = new MutableLiveData<>();
        mWeatherList.setValue(new ArrayList<>());
    }

    public void addWeatherListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<WeatherBuilder>> observer) {
        mWeatherList.observe(owner, observer);
    }

    private void handleResult(final JSONObject result) {
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;
            if (root.has(getString.apply(R.string.keys_json_weather_data))) {
                JSONArray data = root.getJSONArray(getString.apply(R.string.keys_json_weather_data));

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonWeather = data.getJSONObject(i);
                    mWeatherList.getValue().add(new WeatherBuilder.Builder(
                            jsonWeather.getString(getString.apply(R.string.keys_json_weather_temp_F)),
                            jsonWeather.getString(getString.apply(R.string.keys_json_weather_windSpeedMiles)),
                            jsonWeather.getString(getString.apply(R.string.keys_json_weather_humidity)),
                            jsonWeather.getString(getString.apply(R.string.keys_json_weather_precipMM)),
                            jsonWeather.getString(getString.apply(R.string.keys_json_weather_weather)),
                            jsonWeather.getString(getString.apply(R.string.keys_json_weather_avgtempF))).build());

                }
            } else {
                Log.e("ERROR IN WEAHERVIEWMODEL", "No data array");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR IN WEATHERVIEWMODEL", e.getMessage());
        }
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

