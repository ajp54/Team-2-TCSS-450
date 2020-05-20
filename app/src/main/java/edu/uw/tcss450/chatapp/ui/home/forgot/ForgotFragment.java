package edu.uw.tcss450.chatapp.ui.home.forgot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentForgotBinding;
import edu.uw.tcss450.chatapp.ui.home.signin.LoginFragment;
import edu.uw.tcss450.chatapp.utils.PasswordValidator;

public class ForgotFragment extends Fragment {

    private ForgotViewModel mForgotModel;

    private FragmentForgotBinding binding;

    private PasswordValidator mEmailValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdSpecialChar("@"));

    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForgotModel = new ViewModelProvider(getActivity())
                .get(ForgotViewModel.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the OnClickListener to the register button



        binding.buttonForgotSendEmail.setOnClickListener(button -> attemptEmailForgot());

        mForgotModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /**
     * Pass the email into their corresponding validation methods.
     *
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void attemptEmailForgot() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editForgotPass.getText().toString().trim()),
                this::verifyAuthWithServer,
                this::handleEmailError);
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

        mForgotModel.connect(binding.editForgotPass.getText().toString());

    }

    private void navigateToConfirm() {
        Navigation.findNavController(getView())
                .navigate(ForgotFragmentDirections.actionForgotFragmentToForgotConfirmFragment());
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
    private void handleEmailError(PasswordValidator.ValidationResult validationResult) {
        String message = getString(R.string.error_general);
        switch (validationResult) {
            case PWD_MISSING_SPECIAL:
                message = getString(R.string.error_email_character);
                break;
            case PWD_INVALID_LENGTH:
                message = getString(R.string.error_email_two_chars);
                break;
            default:
                // might need a case
                break;
        }
        binding.editForgotPass.setError(message);
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
                try { binding.editForgotPass.setError(
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
