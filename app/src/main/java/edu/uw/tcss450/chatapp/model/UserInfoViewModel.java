package edu.uw.tcss450.chatapp.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * A simple {@link ViewModel} subclass.
 */
public class UserInfoViewModel extends ViewModel {

    private final String mEmail;
    private final String mJwt;

    /**
     * Class Constructor.
     *
     * @param email user's email.
     * @param jwt User's JSON Web Token.
     */
    private UserInfoViewModel(String email, String jwt) {
        mEmail = email;
        mJwt = jwt;
    }

    /**
     * Get's the user's JSON Web Token.
     *
     * @return user's JSON Web Token.
     */
    public String getEmail() {

        return mEmail;
    }

    /**
     * Get's the JSON Web Token.
     *
     * @return user's JSON Web Token.
     */
    public String getmJwt() {

        return mJwt;
    }

    /**
     * Instantiates the UserInfoViewModel
     */
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        private final String email;
        private final String jwt;

        public UserInfoViewModelFactory(String email, String jwt) {
            this.email = email;
            this.jwt = jwt;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(email, jwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }

}
