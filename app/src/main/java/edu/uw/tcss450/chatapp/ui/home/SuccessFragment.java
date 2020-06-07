package edu.uw.tcss450.chatapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.auth0.android.jwt.JWT;

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
import edu.uw.tcss450.chatapp.adapter.MyNotificationRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.databinding.FragmentSuccessBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.ui.home.signin.HomeViewModel;
import edu.uw.tcss450.chatapp.ui.weather.CurrentWeatherBuilder;
import edu.uw.tcss450.chatapp.ui.weather.WeatherViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends Fragment {
    private WeatherViewModel mCurrentWeatherModel;
    private HomeViewModel mHomeModel;
    private UserInfoViewModel mUserModel;

    private FragmentSuccessBinding binding;
    private View myView;
    private static List<String> locationInfo;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private static double longitude;
    private static double latitude;

    public SuccessFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mCurrentWeatherModel = provider.get(WeatherViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
        mHomeModel = provider.get(HomeViewModel.class);
        getLastLocation();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_success, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSuccessBinding.bind((getView()));

        mCurrentWeatherModel.addWeatherListObserver(getViewLifecycleOwner(), currentWeatherList -> {
            if (!currentWeatherList.isEmpty()) {
                binding.textCity.setText(locationInfo.get(0));
                binding.textTemperature.setText(CurrentWeatherBuilder.getTemp_F() + " F");
                binding.textWind.setText("Wind: " + CurrentWeatherBuilder.getWindSpeedMiles() + " MPH");
                binding.textHumidity.setText("Humidity: " +CurrentWeatherBuilder.getHumidity() + "%");
                binding.textPrecipitation.setText("Precipitation: " +CurrentWeatherBuilder.getPrecipMM() + "mm");
            }
        });


        RecyclerView recyclerView = myView.findViewById(R.id.notifications_recyclerview);

        ArrayList<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification("user0", "this is my message 0"));
        notifications.add(new Notification("user1", "this is my message 1"));
        notifications.add(new Notification("user2", ""));
        notifications.add(new Notification("user3", "this is my message 3"));
        notifications.add(new Notification("user4", ""));
        notifications.add(new Notification("user5", "this is my message 5"));


        LinearLayoutManager layoutManager = new LinearLayoutManager(myView.getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyNotificationRecyclerViewAdapter(getContext(), notifications));

        mHomeModel.addMessageObserver(getViewLifecycleOwner(), chatList -> {
            if (recyclerView.getAdapter() != null) {
                Log.i("HOME", "received a new notification");
                recyclerView.getAdapter().notifyDataSetChanged();
            }
                });

    // old recycler view adapter
//        MyNotificationRecyclerViewAdapter adapter = new MyNotificationRecyclerViewAdapter(getContext(),
//                notifications, new MyNotificationRecyclerViewAdapter.DetailsAdapterListener() {
//            @Override
//            public void acceptOnClick(View v, int position) {
//                String user = notifications.get(position).getUsername();
//                Log.e("Notifications", user + " accepted :)");
//            }
//
//            @Override
//            public void rejectOnClick(View v, int position) {
//                String user = notifications.get(position).getUsername();
//                Log.e("Notifications", user + " rejected :(");
//            }
//        }, new MyNotificationRecyclerViewAdapter.ItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                String user = notifications.get(position).getUsername();
//                Log.e("Notifications", "message from " + user + " clicked");
//            }
//        });

//        MyNotificationRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position) -> {

//        binding.notificationsRecyclerview.setAdapter(adapter);


    }

    /**
     * Requests the device location from the API
     */
    private void getLastLocation() {
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

}
