package edu.uw.tcss450.chatapp.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.auth0.android.jwt.JWT;

import java.util.ArrayList;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.adapter.MyNotificationRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.databinding.FragmentSuccessBinding;
import edu.uw.tcss450.chatapp.ui.weather.CurrentWeatherBuilder;
import edu.uw.tcss450.chatapp.ui.weather.WeatherViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends Fragment {

    private FragmentSuccessBinding binding;
    private View myView;
    private WeatherViewModel mCurrentWeatherModel;

    public SuccessFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);

        mCurrentWeatherModel.connectGet();

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
                binding.txtTemperature.setText(CurrentWeatherBuilder.getTemp_F() + " F");
                binding.textWind.setText("Wind: " + CurrentWeatherBuilder.getWindSpeedMiles() + " MPH");
                binding.textHumidity.setText("Humidity: " +CurrentWeatherBuilder.getHumidity() + " %");
                binding.Precipitation.setText("Precipitation: " +CurrentWeatherBuilder.getPrecipMM() + " mm");
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

        MyNotificationRecyclerViewAdapter adapter = new MyNotificationRecyclerViewAdapter(getContext(),
                notifications, new MyNotificationRecyclerViewAdapter.DetailsAdapterListener() {
            @Override
            public void acceptOnClick(View v, int position) {
                String user = notifications.get(position).getUsername();
                Log.e("Notifications", user + " accepted :)");
            }

            @Override
            public void rejectOnClick(View v, int position) {
                String user = notifications.get(position).getUsername();
                Log.e("Notifications", user + " rejected :(");
            }
        }, new MyNotificationRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String user = notifications.get(position).getUsername();
                Log.e("Notifications", "message from " + user + " clicked");
            }
        });

        binding.notificationsRecyclerview.setAdapter(adapter);


    }

}
