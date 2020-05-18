package edu.uw.tcss450.chatapp.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    private List<Integer> chatIds;

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
        //mChatModel.getFirstMessages(HARD_CODED_CHAT_ID, mUserModel.getmJwt());
        mChatModel.getChatIds(124, mUserModel.getmJwt());
        chatIds = mChatModel.getChatIdList();

        for(int i = 0; i < chatIds.size(); i++) {
            mChatModel.getFirstMessages(chatIds.get(i), mUserModel.getmJwt());
        }
        //Log.i("CHATLIST", "char map length:" + mChatModel.getChatMap().size());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentChatBinding binding = FragmentChatBinding.bind(getView());

        //TODO add correct chatId
        ChatRecyclerViewAdapter.RecyclerViewClickListener listener = (v, position) -> {
            navigateToChat(1);
        };

        mChatModel.addChatRoomObserver(1, getViewLifecycleOwner(), chatList -> {
            if (!chatList.isEmpty()) {
                binding.recyclerChatRooms.setAdapter(
                        new ChatRecyclerViewAdapter(chatList, listener)
                );
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
