package edu.uw.tcss450.chatapp.ui.home.signin;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.model.PushyTokenViewModel;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.utils.PasswordValidator;
import edu.uw.tcss450.chatapp.databinding.FragmentLoginBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private static String mEmail;

    private FragmentLoginBinding binding;
    private SignInViewModel mSignInModel;
    private PushyTokenViewModel mPushyTokenViewModel;
    private UserInfoViewModel mUserViewModel;


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
        mPushyTokenViewModel = new ViewModelProvider(getActivity())
                .get(PushyTokenViewModel.class);

    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        if (prefs.contains(getString(R.string.keys_prefs_jwt))) {
            String token = prefs.getString(getString(R.string.keys_prefs_jwt), "");
            JWT jwt = new JWT(token);
            // Check to see if the web token is still valid or not. To make a JWT expire after a
            // longer or shorter time period, change the expiration time when the JWT is
            // created on the web service.
            if(!jwt.isExpired(0)) {
                String email = jwt.getClaim("email").asString();
                navigateToMain(email, token);
                return;
            }
        }
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

        binding.buttonForgotPass.setOnClickListener(button -> Navigation.findNavController(getView())
                .navigate(LoginFragmentDirections.actionLoginFragmentToForgotFragment()));

        mSignInModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);

        LoginFragmentArgs args = LoginFragmentArgs.fromBundle(getArguments());

        binding.editEmailLogin.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        binding.editPassLogin.setText(args.getPassword().equals("default") ? "" : args.getPassword());
        //NavDirections directions = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();

        //don't allow sign in until pushy token retrieved
        mPushyTokenViewModel.addTokenObserver(getViewLifecycleOwner(), token ->
                binding.buttonLoginSignin.setEnabled(!token.isEmpty()));

        mPushyTokenViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observePushyPutResponse);

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
        SharedPreferences prefs =
            getActivity().getSharedPreferences(
                    getString(R.string.keys_shared_prefs),
                    Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_jwt), jwt).apply();

        mEmail = email;

        Navigation.findNavController(getView()).navigate(LoginFragmentDirections
                .actionLoginFragmentToMainActivity(email, jwt));

        //Remove THIS activity from the Task list. Pops off the backstack
        getActivity().finish();
    }

    public static String getEmail() {
        return mEmail;
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
                    mUserViewModel = new ViewModelProvider(getActivity(),
                            new UserInfoViewModel.UserInfoViewModelFactory(
                                    binding.editEmailLogin.getText().toString(),
                                    response.getString("token")
                            )).get(UserInfoViewModel.class);

                    sendPushyToken();
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage()); }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }

    }

    /**
     * Helper to abstract the request to send the pushy token to the web service
     */
    private void sendPushyToken() {
        mPushyTokenViewModel.sendTokenToWebservice(mUserViewModel.getmJwt());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to PushyTokenViewModel.
     *
     * @param response the Response from the server
     */
    private void observePushyPutResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                //this error cannot be fixed by the user changing credentials...
                binding.editEmailLogin.setError(
                        "Error Authenticating on Push Token. Please contact support");
            } else {
                navigateToMain(
                        binding.editEmailLogin.getText().toString(),
                        mUserViewModel.getmJwt()
                );
            }
        }
    }


}
