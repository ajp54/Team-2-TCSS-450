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
import edu.uw.tcss450.chatapp.adapter.MyRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private View myView;
    private MyRecyclerViewAdapter adapter;
    private WeatherViewModel mWeatherModel;


    public ForecastFragment() {
        // Required empty public constructor
    }


//    @Override
//    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
//    }
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

//      RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerview);
        RecyclerView recyclerView = myView.findViewById(R.id.recyclerview);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(myView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new MyRecyclerViewAdapter(myView.getContext());
        //adapter.setClickListener(getActivity());
        recyclerView.setAdapter(adapter);
    }
}
