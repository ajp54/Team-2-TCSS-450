package edu.uw.tcss450.chatapp.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private ContactsViewModel mContactsModel;
    private UserInfoViewModel mUserModel;

    List<Contact> userContacts;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mContactsModel = provider.get(ContactsViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
        userContacts = mContactsModel.connectGet(mUserModel.getmJwt()).getValue();

        //TODO: add the method that populates the contacts list
//        usersContacts = mContactsModel.getChatIds(1, mUserModel.getmJwt()).getValue();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentContactsBinding binding = FragmentContactsBinding.bind(getView());

        final RecyclerView rv = binding.recyclerContacts;

        //this is for navigating somewhere when the card is tapped
        ContactRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position) -> {
//            navigateToChat(chatIds.get(position));
            Log.i("CONTACTS", "user clicked on a contact");
        };

        mContactsModel.addContactObserver(mUserModel.getmJwt(), getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                rv.setAdapter(
                        new ContactRecyclerViewAdapter(contactList, listener)
                );
                rv.getAdapter().notifyDataSetChanged();
                mContactsModel.connectGet(mUserModel.getmJwt());
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);
            }
        });

    }

//        private void navigateToChat(final int chatId) {
////        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
////            Navigation.findNavController(getView()).navigate(ChatListFragmentDirections
////                    .actionNavigationChatToChatRoomFragment2(chatId));
//        }
}
