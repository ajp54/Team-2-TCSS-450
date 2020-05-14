package edu.uw.tcss450.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uw.tcss450.chatapp.utils.ThemeChanger;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeChanger.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_settings);
    }
}
