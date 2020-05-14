package edu.uw.tcss450.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.chatapp.ui.home.signin.LoginFragmentDirections;
import edu.uw.tcss450.chatapp.ui.settings.ChangePasswordFragment;
import edu.uw.tcss450.chatapp.utils.ThemeChanger;

/**
 * A simple {@link android.app.Activity} subclass.
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //

        BottomNavigationView navView = findViewById(R.id.nav_view); // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder( R.id.navigation_home, R.id.navigation_weather, R.id.navigation_contacts, R.id.navigation_chat) .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggleTheme:
                return true;

            case R.id.action_changePassword:
                //TODO open a change password fragment Log.d("Change Password", "Clicked"); return true;
                return true;

            case R.id.action_signOut:
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                return true;

            case R.id.themeOrange:
                ThemeChanger.changeTheme(this, ThemeChanger.THEME_ORANGE);
                return true;

            case R.id.themeBlueLight:
                ThemeChanger.changeTheme(this, ThemeChanger.THEME_BLUE_LIGHT);
                return true;

            case R.id.themeBlueDark:
                ThemeChanger.changeTheme(this, ThemeChanger.THEME_BLUE_DARK);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
