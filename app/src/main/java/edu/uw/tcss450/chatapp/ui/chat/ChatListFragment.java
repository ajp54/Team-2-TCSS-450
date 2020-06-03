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
    private View roomBeingEdited;
    private int positionBeingEdited;

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
                if (roomBeingEdited == null) {
                    if (cardBinding.imageSelected.getVisibility() == View.VISIBLE) {
                        Log.i("CHATLIST", "remove the marker");
                        cardBinding.imageSelected.setVisibility(View.INVISIBLE);
                        roomBeingEdited = null;
                        binding.buttonDeleteChat.setVisibility(View.GONE);
                        binding.buttonNewChat.setEnabled(false);
                        binding.buttonEditChat.setEnabled(true);
                    } else {
                        cardBinding.imageSelected.setVisibility(View.VISIBLE);
                        roomBeingEdited = v;
                        positionBeingEdited = position;
                        binding.buttonDeleteChat.setVisibility(View.VISIBLE);
                        binding.buttonNewChat.setEnabled(true);
                        binding.buttonEditChat.setEnabled(true);
                    }
                } else {
                    if (cardBinding.imageSelected.getVisibility() == View.VISIBLE) {
                        Log.i("CHATLIST", "remove the marker");
                        cardBinding.imageSelected.setVisibility(View.INVISIBLE);
                        roomBeingEdited = null;
                        binding.buttonDeleteChat.setVisibility(View.GONE);
                        binding.buttonNewChat.setEnabled(false);
                        binding.buttonEditChat.setEnabled(true);
                    } else {
                        FragmentChatRoomCardBinding oldCardBinding = FragmentChatRoomCardBinding.bind(roomBeingEdited);
                        oldCardBinding.imageSelected.setVisibility(View.INVISIBLE);
                        cardBinding.imageSelected.setVisibility(View.VISIBLE);
                        roomBeingEdited = v;
                        positionBeingEdited = position;
                        binding.buttonDeleteChat.setVisibility(View.VISIBLE);
                        binding.buttonNewChat.setEnabled(true);
                        binding.buttonEditChat.setEnabled(true);
                    }
                }

            }
        };

        mChatModel.addChatRoomObserver(mUserModel.getmJwt(), mUserModel.getEmail(), getViewLifecycleOwner(), chatList -> {
            if (!chatList.isEmpty()) {
                rv.setAdapter(
                        new ChatRecyclerViewAdapter(chatList, listener)
                );
                rv.getAdapter().notifyDataSetChanged();
                rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
                chatIds = mChatModel.getChatIdList();
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);
            }
        });

        //also the 'add people' button
        binding.buttonNewChat.setOnClickListener(button -> {
            if(!editMode) {
                navigateToContactJoin(true, 0);
            } else {
                navigateToContactJoin(false, chatIds.get(positionBeingEdited));
            }
        });

        //also the cancel button
        binding.buttonEditChat.setOnClickListener(button -> {
            if (!editMode) {
                editMode = true;
                binding.buttonEditChat.setText("Cancel");
                binding.buttonNewChat.setText("Add People");
                binding.buttonNewChat.setEnabled(false);
                binding.textEditRoom.setVisibility(View.VISIBLE);

            } else {
                editMode = false;
                binding.buttonEditChat.setText("Edit Chat");
                binding.buttonNewChat.setText("New Chat");
                binding.textEditRoom.setVisibility(View.GONE);
                binding.buttonDeleteChat.setVisibility(View.INVISIBLE);
                binding.buttonNewChat.setEnabled(true);
                if (roomBeingEdited != null) {
                    FragmentChatRoomCardBinding oldCardBinding = FragmentChatRoomCardBinding.bind(roomBeingEdited);
                    oldCardBinding.imageSelected.setVisibility(View.INVISIBLE);
                }
                roomBeingEdited = null;

            }
        });

        binding.buttonDeleteChat.setOnClickListener(button -> {
            deleteRoom();
        });


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

    private void deleteRoom() {
        mChatModel.deleteChatMember(chatIds.get(positionBeingEdited), mUserModel.getEmail(), mUserModel.getmJwt());
    }

}
