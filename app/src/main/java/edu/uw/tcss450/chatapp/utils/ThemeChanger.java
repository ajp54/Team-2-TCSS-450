package edu.uw.tcss450.chatapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;

/**
 * @author Bayley Cope
 *
 * A simple class that changes the theme of the app during runtime.
 */
public class ThemeChanger {

    private static int mTheme;
    public final static int THEME_ORANGE_LIGHT = 0;
    public final static int THEME_ORANGE_DARK = 1;
    public final static int THEME_BLUE_LIGHT = 2;
    public final static int THEME_BLUE_DARK = 3;


    /**
     * Sets the theme to the one passed by the options menu
     * then restarts the activity to change theme
     *
     * @param activity the activity
     * @param theme the theme
     */
    public static void changeTheme(Activity activity, int theme, String email, String jwt) {
        mTheme = theme;

        activity.finish();
        Intent intent = new Intent(activity, activity.getClass());
        Bundle b = new Bundle();
        b.putString("email", email); //Your id
        b.putString("jwt", jwt);
        intent.putExtras(b); //Put your id to your next Intent
        activity.startActivity(intent);
    }

    /**
     * Sets the theme according to the one assigned to mTheme
     *
     * @param activity the activity
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        switch(mTheme) {
            case THEME_ORANGE_LIGHT:
                activity.setTheme(R.style.Base_Theme_App);
                break;
            case THEME_ORANGE_DARK:
                activity.setTheme(R.style.Theme_App_OrangeDark);
                break;
            case THEME_BLUE_LIGHT:
                activity.setTheme(R.style.Theme_App_BlueLight);
                break;
            case THEME_BLUE_DARK:
                activity.setTheme(R.style.Theme_App_BlueDark);
                break;
            default:
        }
    }

    public int getTheme() {
        return mTheme;
    }
}
