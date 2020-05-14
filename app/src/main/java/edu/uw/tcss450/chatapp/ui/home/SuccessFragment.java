package edu.uw.tcss450.chatapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;

import edu.uw.tcss450.chatapp.MainActivityArgs;
import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentChangePasswordBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentSuccessBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends Fragment {

    private String email;

    public SuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =  getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

        String token = prefs.getString(getString(R.string.keys_prefs_jwt), "");
        JWT jwt = new JWT(token);
        email = jwt.getClaim("email").asString();

//            // Check to see if the web token is still valid or not. To make a JWT expire after a
//            // longer or shorter time period, change the expiration time when the JWT is
//            // created on the web service.
//            if(!jwt.isExpired(0)) {
//                String email = jwt.getClaim("email").asString();
//                navigateToSuccess(email, token);
//                return;
//            }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
//                System.exit(0);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentSuccessBinding.bind(getView()).textViewSuccessGreeting.setText("Hello " + email);


    }

}
