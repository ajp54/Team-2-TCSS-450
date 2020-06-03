package edu.uw.tcss450.chatapp.ui.chat.contact_join;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
            binding.buttonCreateRoom.setText("Add People");
        } else {
            binding.buttonCreateRoom.setText("Create Room");
        }


        final RecyclerView rv = binding.recyclerContacts;

        //this is for navigating somewhere when the card is tapped
        ContactRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position) -> {
            FragmentContactsCardBinding cardBinding = FragmentContactsCardBinding.bind(v);
            if(cardBinding.imageSelected.getVisibility() == View.INVISIBLE) {
                cardBinding.imageSelected.setVisibility(View.VISIBLE);
//                String name = cardBinding.textUsername.getText().toString();
                contactsBeingAdded.add(cardBinding.textUsername.getText().toString());
            } else {
                cardBinding.imageSelected.setVisibility(View.INVISIBLE);
//                String name = cardBinding.textUsername.getText().toString();
                contactsBeingAdded.remove(cardBinding.textUsername.getText().toString());
            }

//            binding.recyclerContacts.getAdapter().getItemId(position);
            Log.i("COLOR", "number of people to be added: " + contactsBeingAdded.size());
        };

        mContactsModel.addContactObserver(mUserModel.getmJwt(), getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                rv.setAdapter(
                        new ContactRecyclerViewAdapter(contactList, listener)
                );
                rv.getAdapter().notifyDataSetChanged();
                rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
                mContactsModel.connectGet(mUserModel.getmJwt());
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);
            }
        });

        mChatModel.addChatCreateResponseObserver(getViewLifecycleOwner(), result -> {
                    try {
                        addMembersToRoom(result.getInt("chatID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })  ;

        binding.buttonCreateRoom.setOnClickListener(button -> {
            if(creatingRoom){
                createNewRoom();
            } else {
                addMembersToRoom(chatId);
            }

        });

    }

        private void createNewRoom() {
            mChatModel.createChatRoom(mUserModel.getmJwt()); //user is automatically added
            //int chatId = mChatModel.getRecentRoom();

        }

    private void addMembersToRoom(int chatId) {
        for(int i = 0; i < contactsBeingAdded.size(); i++) {
            Log.i("ADDCONTACT", "adding " + contactsBeingAdded.get(i) + " to chat room " + chatId);
            mChatModel.joinChatRoom(chatId, contactsBeingAdded.get(i), mUserModel.getmJwt());
        }
    }

}
