package edu.uw.tcss450.chatapp.ui.home.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.chatapp.ui.home.registerConfirm.RegisterConfirmFragmentArgs;
import edu.uw.tcss450.chatapp.utils.PasswordValidator;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private RegisterViewModel mRegisterModel;

    private FragmentRegisterBinding binding;

    private PasswordValidator mEmailValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdSpecialChar("@"));

    // add more maybe later
    private PasswordValidator mPasswordValidator = PasswordValidator.checkPwdLength(7)
            .and(PasswordValidator.checkPwdSpecialChar())
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdDigit())
            .and(PasswordValidator.checkPwdLowerCase().or(PasswordValidator.checkPwdUpperCase()));

    private PasswordValidator mUsernameValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace());

    /**
     * class constructor
     */
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);

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


        binding.buttonRegisterRegister.setOnClickListener(button -> attemptRegister());

        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);

    }




    /**
     * Pass the the email and password into their corresponding validation methods.
     *
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void attemptRegister() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmailRegister.getText().toString().trim()),
                this::validateUsername,
                this::handleEmailError);
    }

    /**
     * Verifies that the username the user has entered meets all of our requirements.
     *
     * @author Anders Pedersen
     * @version 1.0
     */
    private void validateUsername() {
        mUsernameValidator.processResult(
                mUsernameValidator.apply(binding.editUsernameRegister.getText().toString()),
                this::validatePassword,
                this::handleUsernameError);
    }

    /**
     * Verifies that the password the user has entered meets all of our requirements.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void validatePassword() {
        mPasswordValidator.processResult(
                mPasswordValidator.apply(binding.editPassRegister.getText().toString()),
                this::verifyAuthWithServer,
                this::handlePasswordError);
    }

    private void navigateToConfirm() {
        Navigation.findNavController(getView())
                .navigate(RegisterFragmentDirections.actionRegisterFragmentToRegisterConfirmFragment(binding.editEmailRegister.getText().toString(),
                        binding.editPassRegister.getText().toString()));
    }

    /**
     * Connects to the server and tries sending the validated credentials.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void verifyAuthWithServer() {

//        RegisterConfirmFragmentArgs args = RegisterConfirmFragmentArgs.fromBundle(getArguments());

        mRegisterModel.connect(binding.editFnameRegister.getText().toString(),
                binding.editLnameRegister.getText().toString(), binding.editEmailRegister.getText().toString(),
                binding.editPassRegister.getText().toString());

    }


    /**
     * Takes the result of a password validator and checks to see if the the password the user entered
     * contained any errors. If so, the corresponding error message will be displayed to the user.
     *
     * @param validationResult  The result of the password validation.
     *
     * @author Charles Bryan
     * @author Bayley Cope
     * @version 1.1
     */
    private void handlePasswordError(PasswordValidator.ValidationResult validationResult) {
        String message = getString(R.string.error_general);
        switch (validationResult) {
            case PWD_CLIENT_ERROR:
                message = getString(R.string.error_password_match);
                break;
            case PWD_INVALID_LENGTH:
                message = getString(R.string.error_password_seven_chars);
                break;
            case PWD_INCLUDES_WHITESPACE:
                message = getString(R.string.error_password_whitespace);
                break;
            case PWD_MISSING_DIGIT:
                message = getString(R.string.error_password_digit);
                break;
            case PWD_MISSING_SPECIAL:
                message = getString(R.string.error_password_special);
                break;
            case PWD_MISSING_LOWER:
                message = getString(R.string.error_password_lower);
                break;
            case PWD_MISSING_UPPER:
                message = getString(R.string.error_password_upper);
                break;
            default:
                // might need a case
                break;
        }
        binding.editPassRegister.setError(message);
    }

    /**
     * Takes the result of a password validator and checks to see if the the email the user entered
     * contained any errors. If so, the corresponding error message will be displayed to the user.
     *
     * @param validationResult  The result of the password validation.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void handleEmailError(PasswordValidator.ValidationResult validationResult) {
        String message = getString(R.string.error_general);
        switch (validationResult) {
            case PWD_INVALID_LENGTH:
                message = getString(R.string.error_email_two_chars);
                break;
            case PWD_INCLUDES_WHITESPACE:
                message = getString(R.string.error_email_whitespace);
                break;
            case PWD_MISSING_SPECIAL:
                message = getString(R.string.error_email_character);
                break;
            default:
                // might need a case
                break;
        }
        binding.editEmailRegister.setError(message);
    }

    /**
     * Takes the result of a password validator and checks to see if the the email the user entered
     * contained any errors. If so, the corresponding error message will be displayed to the user.
     *
     * @param validationResult  The result of the password validation.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void handleUsernameError(PasswordValidator.ValidationResult validationResult) {
        String message = getString(R.string.error_general);
        switch (validationResult) {
            case PWD_INVALID_LENGTH:
                message = getString(R.string.error_username_two_chars);
                break;
            case PWD_INCLUDES_WHITESPACE:
                message = getString(R.string.error_username_whitespace);
                break;
            default:
                // might need a case
                break;
        }
        binding.editUsernameRegister.setError(message);
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try { binding.editEmailRegister.setError(
                        "Error Authenticating: " + response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage()); }
            } else { navigateToConfirm();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }


}
