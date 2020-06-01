package edu.uw.tcss450.chatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.chatapp.databinding.ActivityMainBinding;
import edu.uw.tcss450.chatapp.model.NewMessageCountViewModel;
import edu.uw.tcss450.chatapp.model.PushyTokenViewModel;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.services.PushReceiver;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatMessage;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomViewModel;
import edu.uw.tcss450.chatapp.utils.ThemeChanger;

/**
 * A simple {@link android.app.Activity} subclass.
 */
public class MainActivity extends AppCompatActivity {
    private MainPushMessageReceiver mPushMessageReceiver;

    private NavController navController;

    private NewMessageCountViewModel mNewMessageModel;

    private ActivityMainBinding binding;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeChanger.onActivityCreateSetTheme(this);
//        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        String email = args.getEmail();
        String jwt = args.getJwt();

        //take note that we are not using the constructor explicitly, the no-arg
        //constructor is called implicitly
        new ViewModelProvider(
                this,
                new UserInfoViewModel.UserInfoViewModelFactory(email, jwt))
                .get(UserInfoViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view); // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder( R.id.navigation_home, R.id.navigation_weather, R.id.navigation_contacts, R.id.navigation_chat) .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_chat) {
                //When the user navigates to the chats page, reset the new message count.
                //This will need some extra logic for your project as it should have
                //multiple chat rooms.
                mNewMessageModel.reset();
            }
        });

        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chat);
            badge.setMaxCharacterCount(2);
            if (count > 0) {
                //new messages! update and show the notification badge.
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                //user did some action to clear the new messages, remove the badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        switch (item.getItemId()) {
            case R.id.action_toggleTheme:
                return true;

            case R.id.action_changePassword:
                navController.navigate(R.id.changePasswordFragment);
                return true;

            case R.id.action_signOut:
//                Intent intent = new Intent(this, AuthActivity.class);
//                startActivity(intent);
                signOut();
                return true;

            case R.id.themeOrangeLight:
                ThemeChanger.changeTheme(this, ThemeChanger.THEME_ORANGE_LIGHT, args.getEmail(), args.getJwt());
                return true;

            case R.id.themeOrangeDark:
                ThemeChanger.changeTheme(this, ThemeChanger.THEME_ORANGE_DARK, args.getEmail(), args.getJwt());
                return true;

            case R.id.themeBlueLight:
                ThemeChanger.changeTheme(this, ThemeChanger.THEME_BLUE_LIGHT, args.getEmail(), args.getJwt());
                return true;

            case R.id.themeBlueDark:
                ThemeChanger.changeTheme(this, ThemeChanger.THEME_BLUE_DARK, args.getEmail(), args.getJwt());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {

        private ChatRoomViewModel mModel =
                new ViewModelProvider(MainActivity.this)
                        .get(ChatRoomViewModel.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();

            if (intent.hasExtra("chatMessage")) {

                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("chatMessage");

                //If the user is not on the chat screen, update the
                // NewMessageCountView Model
                if (nd.getId() != R.id.navigation_chat) {
                    mNewMessageModel.increment();
                }
                //Inform the view model holding chatroom messages of the new
                //message.
                mModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
    }

    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();
        //End the app completely
        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);

        //when we hear back from the web service quit
//        model.addResponseObserver(this, result -> finishAndRemoveTask());

        model.deleteTokenFromWebservice(
                new ViewModelProvider(this)
                        .get(UserInfoViewModel.class)
                        .getmJwt()
        );

        Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);

    }


}
