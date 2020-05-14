package edu.uw.tcss450.chatapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.chatapp.utils.ThemeChanger;


/**
 * A simple {@link android.app.Activity} subclass.
 */
public class AuthActivity extends AppCompatActivity {

    /**
     * Class constructor
     */
    public AuthActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeChanger.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_auth);
        //
    }
}
