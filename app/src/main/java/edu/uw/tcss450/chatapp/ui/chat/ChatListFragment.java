package edu.uw.tcss450.chatapp.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentChatBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomRecyclerViewAdapter;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomSendViewModel;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    //The chat ID for "global" chat
    private static final int HARD_CODED_CHAT_ID = 1;

    private ChatRoomViewModel mChatModel;
    private UserInfoViewModel mUserModel;

    /**
     * Class constructor
     */
    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatRoomViewModel.class);
        mChatModel.getFirstMessages(HARD_CODED_CHAT_ID, mUserModel.getmJwt());
        Log.i("CHATLIST", "char map length:" + mChatModel.getChatMap().size());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentChatBinding binding = FragmentChatBinding.bind(getView());

//        List<ChatRoom> rooms = new ArrayList<ChatRoom>();
//        List<Integer> chatIDs = new ArrayList<Integer>(mChatModel.getChatMap().keySet());
//        Log.i("CHATLIST", "chat ids length:" + chatIDs.size());
//        for(int chatID : chatIDs) {
//            ChatRoom room = new ChatRoom(new ChatRoom.Builder("People", Integer.toString(chatID), "recent message."));
//            rooms.add(room);
//            Log.i("CHATLIST", "Added chat id:" + chatID);
//        }
//
//        binding.recyclerChatRooms.setAdapter(new ChatRecyclerViewAdapter(rooms));





//        final RecyclerView rv = binding.recyclerChatRooms;
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
//        rv.setAdapter(new ChatRecyclerViewAdapter(mChatModel.getChatMap()));
//                mChatModel.getMessageListByChatId(HARD_CODED_CHAT_ID),
//                mUserModel.getEmail()));

        mChatModel.addChatRoomObserver(1, getViewLifecycleOwner(), chatList -> {
            if (!chatList.isEmpty()) {
                binding.recyclerChatRooms.setAdapter(
                        new ChatRecyclerViewAdapter(chatList)
                );
                //TODO add wait capabilities
                //binding.layoutWait.setVisibility(View.GONE);
            }
        });
    }
}
