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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.adapter.MyWeather24RecyclerViewAdapter;
import edu.uw.tcss450.chatapp.adapter.MyWeatherRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.databinding.FragmentForecastBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private FragmentForecastBinding binding;
    private View myView;
    private GoogleMap mMap;
    private Marker marker;
    private WeatherViewModel mCurrentWeatherModel;
    private WeatherViewModel mDailyWeatherModel;
    private WeatherViewModel mWeeklyWeatherModel;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private static double longitude;
    private static double latitude;
    private static String mZipcode;

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
        binding.buttonZip.setOnClickListener(button -> getUserZipcode(view));

        RecyclerView recyclerView24 = myView.findViewById(R.id.recyclerview24);
        RecyclerView recyclerView = myView.findViewById(R.id.recyclerview);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(myView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManager24
                = new LinearLayoutManager(myView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView24.setLayoutManager(horizontalLayoutManager24);

        mCurrentWeatherModel.addWeatherListObserver(getViewLifecycleOwner(), currentWeatherList -> {
                    if (!currentWeatherList.isEmpty()) {
                        binding.textCity.setText(CurrentWeatherBuilder.getCity());
                        binding.textTemperature.setText(CurrentWeatherBuilder.getTemp_F() + " F");
                        binding.textWind.setText("Wind: " + CurrentWeatherBuilder.getWindSpeedMiles() + " MPH");
                        binding.textHumidity.setText("Humidity: " +CurrentWeatherBuilder.getHumidity() + "%");
                        binding.textPrecipitation.setText("Precipitation: " +CurrentWeatherBuilder.getPrecipMM() + "mm");
                    }
                });

        mDailyWeatherModel.addDailyWeatherListObserver(getViewLifecycleOwner(), dailyWeatherList -> {
                    if (!dailyWeatherList.isEmpty()) {
                        binding.recyclerview24.setAdapter(
                                new MyWeather24RecyclerViewAdapter(dailyWeatherList));
                    }
                });

        mWeeklyWeatherModel.addWeeklyWeatherListObserver(getViewLifecycleOwner(), weeklyWeatherList -> {
                    if (!weeklyWeatherList.isEmpty()) {
                        binding.recyclerview.setAdapter(
                                new MyWeatherRecyclerViewAdapter(weeklyWeatherList));
                    }
                });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);
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
                                try {
                                    getZipCodeFromCoords(longitude, latitude);
                                    mCurrentWeatherModel.connectGet(mZipcode);
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
            LocationManager.NETWORK_PROVIDER);
        }

    /**
     * Did the user give permission to use their location?
     *
     * @return true or false
     */
    private boolean checkPermissions() {
        return (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
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

    /**
     * A helper method to get the zipcode and city of a location based off
     * of its longitude and latitude coordinates
     *
     * @param longitude the longitude of the location
     * @param latitude the latitude of the location
     * @throws IOException IOexception
     */
    private void getZipCodeFromCoords(Double longitude, Double latitude) throws IOException {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        mZipcode = addresses.get(0).getPostalCode();
    }

    /**
     * A helper method to get the zipcode from the editText field on button press
     *
     * @param view the View
     */
    private void getUserZipcode(View view) {
        EditText edit = view.findViewById(R.id.edit_zip);
        mZipcode = edit.getText().toString();
        mCurrentWeatherModel.connectGet(mZipcode);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        final LatLng c = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
        if (marker != null) {
            marker.setPosition(latLng);
        }  else {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Your Destination")
                    .draggable(true));
        }
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom));
        try {
            getZipCodeFromCoords(latLng.longitude, latLng.latitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentWeatherModel.connectGet(mZipcode);
    }
}
