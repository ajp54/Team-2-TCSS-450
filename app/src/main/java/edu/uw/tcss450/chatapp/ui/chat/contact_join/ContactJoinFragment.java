package edu.uw.tcss450.chatapp.ui.chat.contact_join;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentContactJoinBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsCardBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomViewModel;
import edu.uw.tcss450.chatapp.ui.contacts.Contact;
import edu.uw.tcss450.chatapp.ui.contacts.ContactRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.ui.contacts.ContactsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ContactJoinFragment extends Fragment {
    private ContactsViewModel mContactsModel;
    private ChatRoomViewModel mChatModel;
    private UserInfoViewModel mUserModel;

    private boolean creatingRoom;
    private int chatId;

    List<Contact> userContacts;
    List<String> contactsBeingAdded;

    public ContactJoinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_join, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        creatingRoom = args.getBoolean("creatingRoom");
        chatId = args.getInt("chatId");
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mContactsModel = provider.get(ContactsViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatRoomViewModel.class);
        userContacts = mContactsModel.connectGet(mUserModel.getmJwt()).getValue();
        contactsBeingAdded = new ArrayList<String>();

        //TODO: add the method that populates the contacts list
//        usersContacts = mContactsModel.getChatIds(1, mUserModel.getmJwt()).getValue();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentContactJoinBinding binding = FragmentContactJoinBinding.bind(getView());

        if(!creatingRoom) {
            binding.buttonCreateRoom.setText(R.string.label_add_people);
        } else {
            binding.buttonCreateRoom.setText(R.string.label_create_room);
        }
        binding.buttonCreateRoom.setEnabled(false);

        final RecyclerView rv = binding.recyclerContacts;

        //this is for navigating somewhere when the card is tapped
        ContactRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position, delete) -> {
            FragmentContactsCardBinding cardBinding = FragmentContactsCardBinding.bind(v);
            if(cardBinding.imageSelected.getVisibility() == View.GONE) {
                cardBinding.imageSelected.setVisibility(View.VISIBLE);
//                String name = cardBinding.textUsername.getText().toString();
                contactsBeingAdded.add(cardBinding.textUsername.getText().toString());
                binding.buttonCreateRoom.setEnabled(true);
            } else {
                cardBinding.imageSelected.setVisibility(View.GONE);
//                String name = cardBinding.textUsername.getText().toString();
                contactsBeingAdded.remove(cardBinding.textUsername.getText().toString());
                if(contactsBeingAdded.size() == 0)
                    binding.buttonCreateRoom.setEnabled(false);
            }

//            binding.recyclerContacts.getAdapter().getItemId(position);
            Log.i("COLOR", "number of people to be added: " + contactsBeingAdded.size());
        };

        mContactsModel.addContactObserver(mUserModel.getmJwt(), getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                rv.setAdapter(
                        new ContactRecyclerViewAdapter(contactList, listener, mContactsModel, mUserModel, true)
                );
                rv.getAdapter().notifyDataSetChanged();
                rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
                mContactsModel.connectGet(mUserModel.getmJwt());
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);
            }
        });

        mChatModel.addChatCreateResponseObserver(getViewLifecycleOwner(), result -> {
            Log.i("ADDCONTACT", "room created");
                    try {

                        addMembersToRoom(result.getInt("chatID"));
                    } catch (JSONException e) {
                        Log.i("ADDCONTACT", "failed to add members to room");
                        e.printStackTrace();
                    }
                });

        binding.buttonCreateRoom.setOnClickListener(button -> {
            if(creatingRoom){
                createNewRoom();
                Log.i("ADDCONTACT", "user clicked 'create room' button");
            } else {
                addMembersToRoom(chatId);
            }


        });

    }

        private void createNewRoom() {
            mChatModel.createChatRoom(mUserModel.getmJwt()); //user is automatically added
            //int newChatId = mChatModel.getRecentRoom();
//            addMembersToRoom(chatId);

        }

    private void addMembersToRoom(int chatId) {
        for(int i = 0; i < contactsBeingAdded.size(); i++) {
            Log.i("ADDCONTACT", "adding " + contactsBeingAdded.get(i) + " to chat room " + chatId);
            mChatModel.joinChatRoom(chatId, contactsBeingAdded.get(i), mUserModel.getmJwt());
        }
        navigateBackToChat();
    }

    private void navigateBackToChat() {
        NavController navController = Navigation.findNavController(getView());
        navController.popBackStack(R.id.navigation_chat, true);
        navController.navigate(R.id.navigation_chat);
    }

}
