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
    private PasswordValidator mPasswordValidator = PasswordValidator.checkPwdLength(5)
            .and(PasswordValidator.checkExcludeWhiteSpace());


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

        binding.buttonRegisterRegister.setOnClickListener(this::attemptRegister);

        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);

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
                this::verifyAuthWithServer,
                this::handlePasswordError);
    }

    private void verifyAuthWithServer() {
        mRegisterModel.connect( binding.editFnameRegister.getText().toString(),
                binding.editLnameRegister.getText().toString(), binding.editEmailRegister.getText().toString(), binding.editPassRegister.getText().toString());
    }


    private void handlePasswordError(PasswordValidator.ValidationResult validationResult) {
        String message = getString(R.string.error_general);
        switch (validationResult) {
            case PWD_CLIENT_ERROR:
                message = getString(R.string.error_password_match);
                break;
            case PWD_INVALID_LENGTH:
                message = getString(R.string.error_password_five_chars);
                break;
            case PWD_INCLUDES_WHITESPACE:
                message = getString(R.string.error_password_whitespace);
                break;
            default:
                // might need a case
                break;
        }
        binding.editPassRegister.setError(message);
    }

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


    public void navigateToLogin() {

//        Navigation.findNavController(getView())
//                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment());

        RegisterFragmentDirections.ActionRegisterFragmentToLoginFragment directions =
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();

        directions.setEmail(binding.editEmailRegister.getText().toString());
        directions.setPassword(binding.editPassRegister.getText().toString());

        Navigation.findNavController(getView()).navigate(directions);

    }


    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try { binding.editEmailRegister.setError(
                        "Error Authenticating: " + response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage()); }
            } else { navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}
