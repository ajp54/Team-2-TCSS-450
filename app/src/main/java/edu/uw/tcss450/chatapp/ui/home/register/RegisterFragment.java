package edu.uw.tcss450.chatapp.ui.home.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.chatapp.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.combinatorpattern.util.PasswordValidator;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    private PasswordValidator mEmailValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdSpecialChar("@"));

    // add more maybe later
    private PasswordValidator mPasswordValidator = PasswordValidator.checkPwdLength(5)
            .and(PasswordValidator.checkExcludeWhiteSpace());


    public RegisterFragment() {
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
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the OnClickListener to the register button

        binding.buttonRegisterRegister.setOnClickListener(this::attemptRegister);

//        binding.buttonRegisterRegister.setOnClickListener(button -> Navigation.findNavController(getView())
//                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()));

    }

    private void attemptRegister(View view) {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmailRegister.getText().toString().trim()),
                this::validatePassword,
                this::handleEmailError);
    }


    private void validatePassword() {
        mPasswordValidator.processResult(
                mPasswordValidator.apply(binding.editPassRegister.getText().toString()),
                this::navigateToLogin,
                this::handlePasswordError);
    }

    private void handlePasswordError(PasswordValidator.ValidationResult validationResult) {
        String message = "Error";
        switch (validationResult) {
            case PWD_CLIENT_ERROR:
                message = "Passwords must match";
                break;
            case PWD_INVALID_LENGTH:
                message = "Password must include more than 5 characters";
                break;
            case PWD_INCLUDES_WHITESPACE:
                message = "Password must not include whitespace";
                break;
            default:
                // might need a case
                break;
        }
        binding.editPassRegister.setError(message);
    }

    private void handleEmailError(PasswordValidator.ValidationResult validationResult) {
        String message = "Error";
        switch (validationResult) {
            case PWD_INVALID_LENGTH:
                message = "Email must include more than 2 characters";
                break;
            case PWD_INCLUDES_WHITESPACE:
                message = "Email must not include whitespace";
                break;
            case PWD_MISSING_SPECIAL:
                message = "Email must include '@'";
                break;
            default:
                // might need a case
                break;
        }
        binding.editEmailRegister.setError(message);
    }

    public void navigateToLogin() {
        Navigation.findNavController(getView())
                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment());
    }
}
