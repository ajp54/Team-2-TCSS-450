package edu.uw.tcss450.chatapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import edu.uw.tcss450.chatapp.R;

/**
 * @author Bayley Cope
 *
 * A simple class that changes the theme of the app during runtime.
 */
public class ThemeChanger {

    private static int mTheme;
    public final static int THEME_ORANGE = 0;
    public final static int THEME_BLUE_LIGHT = 1;
    public final static int THEME_BLUE_DARK = 2;

    /**
     * Sets the theme to the one passed by the options menu
     * then restarts the activity to change theme
     *
     * @param activity the activity
     * @param theme the theme
     */
    public static void changeTheme(Activity activity, int theme) {
        mTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Sets the theme according to the one assigned to mTheme
     *
     * @param activity the activity
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        switch(mTheme) {
            case THEME_ORANGE:
                activity.setTheme(R.style.Base_Theme_App);
                break;
            case THEME_BLUE_LIGHT:
                activity.setTheme(R.style.Theme_App_BlueLight);
                break;
            case THEME_BLUE_DARK:
                activity.setTheme(R.style.Theme_App_BlueDark);
                Log.i("Dark Theme", "Clicked");
                break;
            default:
        }
    }
}
