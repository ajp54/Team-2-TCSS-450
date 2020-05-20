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
    private int chatFlag = 0;

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
        chatRooms = mChatModel.getChatIds(1, mUserModel.getmJwt()).getValue();

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

        //TODO add correct chatId
        ChatRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position) -> {
            navigateToChat(chatIds.get(position));
        };

        mChatModel.addChatRoomObserver(mUserModel.getmJwt(), getViewLifecycleOwner(), chatList -> {
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
    }

    private void navigateToChat(final int chatId) {
//        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
        Navigation.findNavController(getView()).navigate(ChatListFragmentDirections
                .actionNavigationChatToChatRoomFragment2(chatId));
    }
}
