package edu.uw.tcss450.chatapp.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentChatBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentChatRoomCardBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsCardBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatMessage;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomSendViewModel;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private ChatRoomViewModel mChatModel;
    private UserInfoViewModel mUserModel;

    private List<Integer> chatIds;
    List<ChatRoom> chatRooms;
    private boolean editMode = false;
//    private ChatRoom roomBeingEdited;
//    private View roomBeingEdited;
//    private int positionBeingEdited;
    List<Integer> positionsBeingDeleted;
    List<View> roomsBeingDeleted;

    /**
     * Class constructor
     */
    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        chatIds = mChatModel.getChatIdList();

//        Log.i("CHATLIST", "instantiated chatIds, char map length:" + chatIds.size());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChatModel = provider.get(ChatRoomViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
        chatRooms = mChatModel.getChatIds(mUserModel.getEmail(), mUserModel.getmJwt()).getValue();
        positionsBeingDeleted = new ArrayList<Integer>();
        roomsBeingDeleted = new ArrayList<View>();

//        mChatModel.getFirstMessages(HARD_CODED_CHAT_ID, mUserModel.getmJwt());
//        Log.i("CHATLIST", "instantiated chatIds");

    }



    @Override
    public void onStart() {
        super.onStart();
//        if(chatIds != null) {
//            for (int i = 0; i < chatIds.size(); i++) {
//                mChatModel.getFirstMessages(chatIds.get(i), mUserModel.getmJwt());
//                //add recent messages to the chat room cards
//                List<ChatMessage> messages = mChatModel.getMessageListByChatId(chatIds.get(i));
//                String recentMessage = messages.get(messages.size()-1).getMessage();
//                binding.recyclerChatRooms
//            }
//        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentChatBinding binding = FragmentChatBinding.bind(getView());

        final RecyclerView rv = binding.recyclerChatRooms;

        //if the user taps a chat room
        ChatRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position) -> {
            if (!editMode) {
                Log.i("CHATLIST", "Entering room at position " + position);
                navigateToChat(chatIds.get(position));
            } else {
                FragmentChatRoomCardBinding cardBinding = FragmentChatRoomCardBinding.bind(v);
                    if (cardBinding.imageSelected.getVisibility() == View.VISIBLE) {
                        Log.i("CHATLIST", "remove the marker");
                        cardBinding.imageSelected.setVisibility(View.INVISIBLE);
                        positionsBeingDeleted.remove(new Integer(position));
                        roomsBeingDeleted.remove(v);
                        if (positionsBeingDeleted.size() == 0) {
                            binding.buttonNewChat.setEnabled(false);
                        }
                    } else {
                        cardBinding.imageSelected.setVisibility(View.VISIBLE);
                        positionsBeingDeleted.add(new Integer(position));
                        roomsBeingDeleted.add(v);
                        binding.buttonNewChat.setEnabled(true);
                    }
            }
        };

        mChatModel.addChatRoomObserver(mUserModel.getmJwt(), mUserModel.getEmail(), getViewLifecycleOwner(), chatList -> {
            Log.i("CHATLIST", "updating recycler view");
            if (!chatList.isEmpty()) {
                rv.setAdapter(
                        new ChatRecyclerViewAdapter(chatList, listener, mChatModel, mUserModel, getView())
                );
                rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
                rv.getAdapter().notifyDataSetChanged();
            }
                chatIds = mChatModel.getChatIdList();
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);

        });

        //update the recycler view when the names have been retrieved
        mChatModel.addFillNamesResponseObserver(getViewLifecycleOwner(), result -> {
            if (rv.getAdapter() != null) {
                rv.getAdapter().notifyDataSetChanged();
            }
        });

        //update the recycler view when a new chat room gets created
        mChatModel.addChatCreateResponseObserver(getViewLifecycleOwner(), result -> {
            Log.i("CHATLIST", "Room created. Updating recycler view");
            if (rv.getAdapter() != null) {
                rv.getAdapter().notifyDataSetChanged();
                mChatModel.getChatIds(mUserModel.getEmail(), mUserModel.getmJwt());
            }
        });

        //also the 'Delete' button
        binding.buttonNewChat.setOnClickListener(button -> {
            if(!editMode) {
//                int chatId
                navigateToContactJoin(true, 0);
            } else {
                deleteRooms();
            }
        });

        //also the cancel button
        binding.buttonEditChat.setOnClickListener(button -> {
            if (!editMode) {
                editMode = true;
                binding.buttonEditChat.setText(getString(R.string.label_cancel));
                binding.buttonNewChat.setText(getString(R.string.label_delete));
                binding.buttonNewChat.setEnabled(false);
                binding.textEditRoom.setVisibility(View.VISIBLE);

            } else {
                editMode = false;
                binding.buttonEditChat.setText(getString(R.string.label_edit));
                binding.buttonNewChat.setText(getString(R.string.label_new_chat));
                binding.textEditRoom.setVisibility(View.GONE);
                binding.buttonNewChat.setEnabled(true);
                if (roomsBeingDeleted.size() > 0) {
                    for(View v : roomsBeingDeleted) {
                        FragmentChatRoomCardBinding oldCardBinding = FragmentChatRoomCardBinding.bind(v);
                        oldCardBinding.imageSelected.setVisibility(View.INVISIBLE);
                    }
                }
                positionsBeingDeleted.clear();
                roomsBeingDeleted.clear();

            }
        });

//        binding.buttonDeleteChat.setOnClickListener(button -> {
//            deleteRoom();
//        });


    }

    private void makeNewRoom(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mChatModel.createChatRoom(mUserModel.getmJwt());
        onViewCreated(view, savedInstanceState);
    }

    private void navigateToChat(final int chatId) {
//        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
        Navigation.findNavController(getView()).navigate(ChatListFragmentDirections
                .actionNavigationChatToChatRoomFragment2(chatId));
    }

    private void navigateToContactJoin(boolean creatingRoom, int chatId) {
//        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
        Navigation.findNavController(getView()).navigate(ChatListFragmentDirections
                .actionNavigationChatToContactJoinFragment(creatingRoom, chatId));
    }

    private void deleteRooms() {
        for(int pos : positionsBeingDeleted) {
            mChatModel.deleteChatMember(chatIds.get(pos), mUserModel.getEmail(), mUserModel.getmJwt());
        }
    }

}
