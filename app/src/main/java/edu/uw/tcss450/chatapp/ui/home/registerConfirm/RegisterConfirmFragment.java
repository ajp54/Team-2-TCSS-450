package edu.uw.tcss450.chatapp.ui.home.registerConfirm;

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

import edu.uw.tcss450.chatapp.databinding.FragmentRegisterConfirmBinding;
import edu.uw.tcss450.chatapp.ui.home.register.RegisterViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterConfirmFragment extends Fragment {

    //private RegisterViewModel mRegisterModel;

    private FragmentRegisterConfirmBinding binding;


    public RegisterConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mRegisterModel = new ViewModelProvider(getActivity())
//                .get(RegisterViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the OnClickListener to the register button

        binding.buttonConfirmRegisterContinue.setOnClickListener(this::navigateToLogin);

//        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
//                this::observeResponse);



    }

//    /**
//     * Connects to the server and tries sending the validated credentials.
//     *
//     * @author Charles Bryan
//     * @version 1.0
//     */
//    private void verifyAuthWithServer(View view) {
//        RegisterConfirmFragmentArgs args = RegisterConfirmFragmentArgs.fromBundle(getArguments());
//
//        mRegisterModel.connect(args.getFname(), args.getLname(), args.getEmail(), args.getPass());
//    }

    /**
     * Navigates from the register fragment to the login fragment
     *
     * @author Charles Bryan
     * @version 1.0
     */
    public void navigateToLogin(View view) {
        RegisterConfirmFragmentArgs args = RegisterConfirmFragmentArgs.fromBundle(getArguments());
//
//
//        Navigation.findNavController(getView())
//                .navigate(RegisterConfirmFragmentDirections.actionRegisterConfirmFragmentToLoginFragment());
//
        RegisterConfirmFragmentDirections.ActionRegisterConfirmFragmentToLoginFragment directions =
                RegisterConfirmFragmentDirections.actionRegisterConfirmFragmentToLoginFragment();

        directions.setEmail(args.getEmail());
        directions.setPassword(args.getPass());

        Navigation.findNavController(getView()).navigate(directions);

    }

//    /**
//     * An observer on the HTTP Response from the web server. This observer should be
//     * attached to SignInViewModel.
//     *
//     * @param response the Response from the server
//     *
//     * @author Charles Bryan
//     * @version 1.0
//     */
//    private void observeResponse(final JSONObject response) {
//        if (response.length() > 0) {
//            if (response.has("code")) {
//                try { binding.textViewRegisterConfirmGreeting.setError(
//                        "Error Authenticating: " + response.getJSONObject("data").getString("message"));
//                } catch (JSONException e) {
//                    Log.e("JSON Parse Error", e.getMessage()); }
//            } else { navigateToLogin();
//            }
//        } else {
//            Log.d("JSON Response", "No Response");
//        }
//    }


}
