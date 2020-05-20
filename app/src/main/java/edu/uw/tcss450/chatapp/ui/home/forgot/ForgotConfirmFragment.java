package edu.uw.tcss450.chatapp.ui.home.forgot;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import edu.uw.tcss450.chatapp.MainActivity;
import edu.uw.tcss450.chatapp.databinding.FragmentForgotConfirmBinding;

public class ForgotConfirmFragment extends Fragment {

    private FragmentForgotConfirmBinding binding;

    public ForgotConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.buttonBackForgotConfirm.setOnClickListener(button -> navigateToLogin());

    }

    private void navigateToLogin() {

        Navigation.findNavController(getView())
                .navigate(ForgotConfirmFragmentDirections.actionForgotConfirmFragmentToLoginFragment());

    }
}
