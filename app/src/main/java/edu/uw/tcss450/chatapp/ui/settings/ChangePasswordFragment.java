package edu.uw.tcss450.chatapp.ui.settings;

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

import edu.uw.tcss450.chatapp.MainActivityArgs;
import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentChangePasswordBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.ui.home.SuccessFragment;
import edu.uw.tcss450.chatapp.ui.home.register.RegisterFragmentDirections;
import edu.uw.tcss450.chatapp.ui.home.register.RegisterViewModel;
import edu.uw.tcss450.chatapp.ui.home.signin.LoginFragment;
import edu.uw.tcss450.chatapp.utils.PasswordValidator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {


    private ChangePasswordViewModel mChangePassModel;

    private FragmentChangePasswordBinding binding;

    // add more maybe later
    private PasswordValidator mPasswordValidator = PasswordValidator.checkPwdLength(7)
            .and(PasswordValidator.checkPwdSpecialChar())
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdDigit())
            .and(PasswordValidator.checkPwdLowerCase().or(PasswordValidator.checkPwdUpperCase()));


    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangePassModel = new ViewModelProvider(getActivity())
                .get(ChangePasswordViewModel.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the OnClickListener to the register button



        binding.buttonChangePassChangePass.setOnClickListener(button -> attemptChangePass());

        mChangePassModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);


    }

    /**
     * Pass the password into their corresponding validation methods.
     *
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void attemptChangePass() {
        mPasswordValidator.processResult(
                mPasswordValidator.apply(binding.editPassChangePass.getText().toString().trim()),
                this::verifyAuthWithServer,
                this::handlePasswordError);
    }

    /**
     * Connects to the server and tries sending the validated credentials.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void verifyAuthWithServer() {

//        MainActivityArgs args = MainActivityArgs.fromBundle(getArguments());

//        UserInfoViewModel model = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);



//        RegisterConfirmFragmentArgs args = RegisterConfirmFragmentArgs.fromBundle(getArguments());

        String email = LoginFragment.getEmail();

        mChangePassModel.connect(email, binding.editPassChangePass.getText().toString());

    }

    private void navigateToConfirm() {
        Navigation.findNavController(getView())
                .navigate(ChangePasswordFragmentDirections.actionChangePasswordFragmentToChangePassConfirmFragment());
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
        binding.editPassChangePass.setError(message);
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
                try { binding.editPassChangePass.setError(
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
