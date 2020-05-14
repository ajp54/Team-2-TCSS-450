package edu.uw.tcss450.chatapp.ui.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentLoginBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentSettingsBinding;
import edu.uw.tcss450.chatapp.ui.home.signin.LoginFragmentArgs;
import edu.uw.tcss450.chatapp.ui.home.signin.LoginFragmentDirections;
import edu.uw.tcss450.chatapp.ui.home.signin.SignInViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        binding.buttonSettingsChangePass.setOnClickListener(button -> Navigation.findNavController(getView()).navigate(
                SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment()
        ));


    }

    private void doNothing() {

    }


}
