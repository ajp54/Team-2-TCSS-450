package edu.uw.tcss450.chatapp.ui.contacts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsCardBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.ui.home.signin.LoginFragmentDirections;
import edu.uw.tcss450.chatapp.utils.PasswordValidator;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;

    private ContactsViewModel mContactsModel;
    private ContactPendingViewModel mContactsPendingModel;
    private UserInfoViewModel mUserModel;

    List<Contact> userContacts;
    List<ContactPending> userContactsPending;

    Dialog mDialog;

    private PasswordValidator mUsernameValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace());

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mContactsModel = provider.get(ContactsViewModel.class);
        mContactsPendingModel = provider.get(ContactPendingViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
        userContacts = mContactsModel.connectGet(mUserModel.getmJwt()).getValue();
        userContactsPending = mContactsPendingModel.connectGet(mUserModel.getmJwt()).getValue();

        //TODO: add the method that populates the contacts list
//        usersContacts = mContactsModel.getChatIds(1, mUserModel.getmJwt()).getValue();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView rv = binding.recyclerContacts;
        final RecyclerView rvPend = binding.recyclerContactsPending;

//        binding.buttonContactsEdit.setOnClickListener(button -> Navigation.findNavController(getView())
//                .navigate(ContactsFragmentDirections.actionNavigationContactsToEditContactsFragment()));

        binding.buttonAdd.setOnClickListener(button -> attemptAddContact());

//        binding.buttonRemove.setOnClickListener(button -> attemptRemoveContact());

        ContactPendingRecyclerViewAdapter.RecyclerViewClickListener pendListener = (v, position, pending) -> {
            if(pending == "accept") {
                mContactsPendingModel.connectAccept(mUserModel.getmJwt(), position);
                //mContactsPendingModel.connectGet(mUserModel.getmJwt());
//                rvPend.getAdapter().notifyDataSetChanged();
//                rv.getAdapter().notifyDataSetChanged();
//                rvPend.setLayoutManager(new LinearLayoutManager(this.getContext()));
            } else if(pending == "reject") {
                mContactsPendingModel.connectReject(mUserModel.getmJwt(), position);
                //mContactsPendingModel.connectGet(mUserModel.getmJwt());
//                rvPend.getAdapter().notifyDataSetChanged();
//                rv.getAdapter().notifyDataSetChanged();
//                rvPend.setLayoutManager(new LinearLayoutManager(this.getContext()));
            }
            Log.i("CONTACTS PENDING", "user clicked on a contact");
        };

        mContactsPendingModel.addContactPendingObserver(mUserModel.getmJwt(), getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                rvPend.setAdapter(
                        new ContactPendingRecyclerViewAdapter(contactList, pendListener)
                );
                mContactsPendingModel.connectGet(mUserModel.getmJwt());
//                rvPend.getAdapter().notifyDataSetChanged();
                rvPend.getAdapter().notifyDataSetChanged();
                rvPend.setLayoutManager(new LinearLayoutManager(this.getContext()));
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);
            }
        });

        mContactsModel.addUpdateContactsResponseObserver(getViewLifecycleOwner(), contactList -> {
            if (rv.getAdapter() != null) {
                rv.getAdapter().notifyDataSetChanged();
            }
        });

        mContactsPendingModel.addPendingRequestResponseObserver(getViewLifecycleOwner(), contactList -> {
            if (rvPend.getAdapter() != null) {
                rvPend.getAdapter().notifyDataSetChanged();
                mContactsModel.connectGet(mUserModel.getmJwt());
                Log.i("PENDING", "accepted a contact request");
            }
        });

        //this is for navigating somewhere when the card is tapped
        ContactRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position, delete) -> {
            FragmentContactsCardBinding cardBinding = FragmentContactsCardBinding.bind(v);
            if(delete == true) {
                mContactsModel.connectRemove(mUserModel.getmJwt(), position);
            } else if(cardBinding.buttonMore.getVisibility() == View.GONE) {
                cardBinding.buttonMore.setImageIcon(
                        Icon.createWithResource(
                                v.getContext(),
                                R.drawable.ic_less_grey_24dp));
                cardBinding.buttonDelete.setVisibility(View.GONE);
            } else {
                cardBinding.buttonMore.setImageIcon(
                        Icon.createWithResource(
                                v.getContext(),
                                R.drawable.ic_more_grey_24dp));
                cardBinding.buttonDelete.setVisibility(View.VISIBLE);
            }
            Log.i("CONTACTS", "user clicked on a contact");
        };

        mContactsModel.addContactObserver(getActivity(), mUserModel.getmJwt(), getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                rv.setAdapter(
                        new ContactRecyclerViewAdapter(contactList, listener, mContactsModel, mUserModel, false)
                );
                rv.getAdapter().notifyDataSetChanged();
                rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
                mContactsModel.connectGet(mUserModel.getmJwt());
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);
            }
        });



    }

    private void attemptAddContact() {
        mUsernameValidator.processResult(
                mUsernameValidator.apply(binding.editContactUsername.getText().toString()),
                this::verifyContactWithServerAdd,
                this::handleUsernameError);
    }

//    private void attemptRemoveContact() {
//        mUsernameValidator.processResult(
//                mUsernameValidator.apply(binding.editContactUsername.getText().toString()),
//                this::verifyContactWithServerRemove,
//                this::handleUsernameError);
//    }

    private void verifyContactWithServerAdd() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Add the buttons
        builder.setMessage(R.string.dialog_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();

        mContactsModel.connectAdd(mUserModel.getmJwt(), binding.editContactUsername.getText().toString());

        dialog.show();

// Set other dialog properties

    }

//    private void verifyContactWithServerRemove() {
//
//        mContactsModel.connectRemove(mUserModel.getmJwt(), 0);
//
//    }

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


//        private void navigateToChat(final int chatId) {
////        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
////            Navigation.findNavController(getView()).navigate(ChatListFragmentDirections
////                    .actionNavigationChatToChatRoomFragment2(chatId));
//        }
}
