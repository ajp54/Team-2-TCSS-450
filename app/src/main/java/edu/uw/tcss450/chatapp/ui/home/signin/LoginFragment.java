package edu.uw.tcss450.chatapp.ui.home.signin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.chatapp.utils.PasswordValidator;
import edu.uw.tcss450.chatapp.databinding.FragmentLoginBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    private PasswordValidator mEmailValidator =
            (PasswordValidator.checkPwdSpecialChar("@"));

    // add more maybe later
    private PasswordValidator mPasswordValidator = PasswordValidator.checkPwdLength(1);

    public LoginFragment() {
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
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLoginRegister.setOnClickListener(button -> Navigation.findNavController(getView())
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment()));

        binding.buttonLoginSignin.setOnClickListener(button -> handleLogin());
        //NavDirections directions = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();



    }

    private void handleLogin() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmailLogin.getText().toString().trim()),
                this::validatePassword,
                this::handleEmailError);
    }

    private void validatePassword() {
        mPasswordValidator.processResult(
                mPasswordValidator.apply(binding.editPassLogin.getText().toString()),
                this::navigateToMain,
                this::handlePasswordError);
    }

    private void handlePasswordError(PasswordValidator.ValidationResult validationResult) {
        String message = "Password must be at least of length two";
        binding.editPassLogin.setError(message);
    }

    private void handleEmailError(PasswordValidator.ValidationResult validationResult) {
        String message = "Email must contain a '@'";
        binding.editEmailLogin.setError(message);
    }

    private void navigateToMain() {
        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
    }

}
