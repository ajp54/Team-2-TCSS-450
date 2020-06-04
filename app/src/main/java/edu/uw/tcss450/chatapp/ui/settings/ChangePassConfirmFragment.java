package edu.uw.tcss450.chatapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.chatapp.MainActivity;
import edu.uw.tcss450.chatapp.databinding.FragmentChangePassConfirmBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassConfirmFragment extends Fragment {

    private FragmentChangePassConfirmBinding binding;

    public ChangePassConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePassConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.buttonBackChangePassConfirm.setOnClickListener(button -> navigateToHome());

    }

    private void navigateToHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

}
