package edu.uw.tcss450.chatapp.ui.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uw.tcss450.chatapp.R;
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
    private static List<String> locationInfo;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private static double longitude;
    private static double latitude;

    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mCurrentWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        mDailyWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        mWeeklyWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        getLastLocation();
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
                        binding.textCity.setText(locationInfo.get(0));
                        binding.textTemperature.setText(CurrentWeatherBuilder.getTemp_F());
                        binding.textWind.setText(CurrentWeatherBuilder.getWindSpeedMiles());
                        binding.textHumidity.setText(CurrentWeatherBuilder.getHumidity());
                        binding.textPrecipitation.setText(CurrentWeatherBuilder.getPrecipMM());
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

    /**
     * Requests the device location from the API
     */
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                                //Log.e("LONGITUDE:", String.valueOf(longitude));
                                //Log.e("Latitude", String.valueOf(latitude));
                                try {
                                    locationInfo = getZipCode(longitude, latitude);
                                    mCurrentWeatherModel.connectGet(locationInfo);
                                    //Log.e("CITY", locationInfo.get(0));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    /**
     * A helper method to get the zipcode of a location based off
     * of its longitude and latitude coordinates
     *
     * @param longitude the longitude of the location
     * @param latitude the latitude of the location
     * @return the zipcode that the coordinates fall into
     * @throws IOException
     */
    private List<String> getZipCode(Double longitude, Double latitude) throws IOException {
        List<String> info = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        String city = addresses.get(0).getLocality();
        String zipcode = addresses.get(0).getPostalCode();
        info.add(city);
        info.add(zipcode);
        return info;
    }

    /**
     *  Requests new location data if updates are needed
     */
    private void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    /**
     * Does the user have location services enabled?
     *
     * @return true or false
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
        );
        }

    /**
     * Did the user give permission to use their location?
     *
     * @return true or false
     */
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * Request permission to use location from user
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_LOCATIONS
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_LOCATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}
