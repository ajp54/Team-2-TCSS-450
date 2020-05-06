package edu.uw.tcss450.chatapp.ui.home.signin;

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
import edu.uw.tcss450.chatapp.utils.PasswordValidator;
import edu.uw.tcss450.chatapp.databinding.FragmentLoginBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private SignInViewModel mSignInModel;

    private PasswordValidator mEmailValidator = PasswordValidator.checkPwdLength(2)
                    .and(PasswordValidator.checkExcludeWhiteSpace())
                    .and(PasswordValidator.checkPwdSpecialChar("@"));

    private PasswordValidator mPasswordValidator = PasswordValidator.checkPwdLength(1)
            .and(PasswordValidator.checkExcludeWhiteSpace());

    /**
     * Class constructor
     */
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignInModel = new ViewModelProvider(getActivity())
                .get(SignInViewModel.class);
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

        mSignInModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);

        LoginFragmentArgs args = LoginFragmentArgs.fromBundle(getArguments());

        binding.editEmailLogin.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        binding.editPassLogin.setText(args.getPassword().equals("default") ? "" : args.getPassword());
        //NavDirections directions = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();



    }

    /**
     * Pass the the email and password into their corresponding validation methods.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void handleLogin() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmailLogin.getText().toString().trim()),
                this::validatePassword,
                this::handleEmailError);
    }

    /**
     * Verifies that the password the user has entered meets all of our requirements.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void validatePassword() {
        mPasswordValidator.processResult(
                mPasswordValidator.apply(binding.editPassLogin.getText().toString()),
                this::verifyAuthWithServer,
                this::handlePasswordError);
    }

    /**
     * Verifies that the email the user has entered meets all of our requirements.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void verifyAuthWithServer() {
        mSignInModel.connect( binding.editEmailLogin.getText().toString(),
                binding.editPassLogin.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        // result of connect().

    }

    /**
     * Takes the result of a password validator and checks to see if the the password the user entered
     * contained any errors. If so, the corresponding error message will be displayed to the user.
     *
     * @param validationResult  The result of the password validation.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void handlePasswordError(PasswordValidator.ValidationResult validationResult) {
        String message = getString(R.string.error_password_one_char);
        binding.editPassLogin.setError(message);
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
        String message = getString(R.string.error_email_character);
        binding.editEmailLogin.setError(message);
    }

    /**
     * Navigates from the login fragment to the main activity.
     *
     * @param email User's email getting passed into the main activity
     * @param jwt   JSON Web Token getting passed into the main activity
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void navigateToMain(final String email, final String jwt) {
//        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
        Navigation.findNavController(getView()).navigate(LoginFragmentDirections
                .actionLoginFragmentToMainActivity(email, jwt));
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
                try { binding.editEmailLogin.setError(
                        "Error Authenticating: " + response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    navigateToMain(binding.editEmailLogin.getText().toString(), response.getString("token"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage()); }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }

    }

}
