package edu.uw.tcss450.chatapp.ui.weather;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.uw.tcss450.chatapp.MainActivity;
import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.adapter.MyRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private View myView;

    private MyRecyclerViewAdapter adapter;


    public ForecastFragment() {
        // Required empty public constructor
    }


//    @Override
//    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
//    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        myView = inflater.inflate(R.layout.fragment_forecast, container, false);

        // data to populate the RecyclerView with
        ArrayList<Integer> viewColors = new ArrayList<>();
        viewColors.add(Color.BLUE);
        viewColors.add(Color.YELLOW);
        viewColors.add(Color.MAGENTA);
        viewColors.add(Color.RED);
        viewColors.add(Color.BLACK);

        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");

        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        // set up the RecyclerView



//        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerview);
        RecyclerView recyclerView = myView.findViewById(R.id.recyclerview);


        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new MyRecyclerViewAdapter(getActivity(), viewColors, animalNames);
        //adapter.setClickListener(getActivity());
        recyclerView.setAdapter(adapter);

        return inflater.inflate(R.layout.fragment_forecast, container, false);

    }
}
