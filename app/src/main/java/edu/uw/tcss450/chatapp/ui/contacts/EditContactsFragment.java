package edu.uw.tcss450.chatapp.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.chatapp.MainActivity;
import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsEditBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.utils.PasswordValidator;

public class EditContactsFragment extends Fragment {

    private FragmentContactsEditBinding binding;

    private EditContactsViewModel mEditContactsModel;
    private UserInfoViewModel mUserModel;

    private PasswordValidator mUsernameValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mEditContactsModel = provider.get(EditContactsViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);

        //TODO: add the method that populates the contacts list
//        usersContacts = mContactsModel.getChatIds(1, mUserModel.getmJwt()).getValue();
    }

    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.buttonAdd.setOnClickListener(button -> attemptAddContact());

        binding.buttonRemove.setOnClickListener(button -> attemptRemoveContact());

    }

    private void attemptAddContact() {
        mUsernameValidator.processResult(
            mUsernameValidator.apply(binding.editContactUsername.getText().toString()),
                this::verifyContactWithServerAdd,
                this::handleUsernameError);
    }

    private void attemptRemoveContact() {
        mUsernameValidator.processResult(
            mUsernameValidator.apply(binding.editContactUsername.getText().toString()),
                this::verifyContactWithServerRemove,
                this::handleUsernameError);
    }

    /**
     * Connects to the server and tries sending the validated credentials.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void verifyContactWithServerAdd() {

        mEditContactsModel.connectAdd(mUserModel.getmJwt(), binding.editContactUsername.getText().toString());

    }

    /**
     * Connects to the server and tries sending the validated credentials.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void verifyContactWithServerRemove() {

        mEditContactsModel.connectRemove(mUserModel.getmJwt(), binding.editContactUsername.getText().toString());

    }

    /**
     * Takes the result of a password validator and checks to see if the the email the user entered
     * contained any errors. If so, the corresponding error message will be displayed to the user.
     *
     * @param validationResult  The result of the password validation.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void handleUsernameError(PasswordValidator.ValidationResult validationResult) {
        String message = getString(R.string.error_general);
        switch (validationResult) {
            case PWD_INVALID_LENGTH:
                message = getString(R.string.error_username_two_chars);
                break;
            case PWD_INCLUDES_WHITESPACE:
                message = getString(R.string.error_username_whitespace);
                break;
            default:
                // might need a case
                break;
        }
        binding.editContactUsername.setError(message);
    }
}
