package edu.uw.tcss450.chatapp.ui.chat.chat_room;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.chatapp.databinding.FragmentChatRoomBinding;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsCardBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment {

    //The chat ID for "global" chat
    //private static final int HARD_CODED_CHAT_ID = 1;

    private ChatRoomViewModel mChatModel;
    private UserInfoViewModel mUserModel;
    private ChatRoomSendViewModel mSendModel;

    private List<Integer> ChatIds;
    private int chatId;

    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        chatId = args.getInt("chatId", 1);
//        Log.i("CHATROOM", "user selected chatID: " + chatId);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatRoomViewModel.class);
        mChatModel.getChatIds(mUserModel.getEmail(), mUserModel.getmJwt());
        mChatModel.getFirstMessages(chatId, mUserModel.getmJwt());
        ChatIds = mChatModel.getChatIdList();
        mSendModel = provider.get(ChatRoomSendViewModel.class);
        Log.i("CHATROOM", "created the chat room");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChatRoomBinding binding = FragmentChatRoomBinding.bind(getView());

        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.recyclerMessages;
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
        rv.setAdapter(new ChatRoomRecyclerViewAdapter(
                        mChatModel.getMessageListByChatId(chatId),
                        mUserModel.getEmail()));


        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatModel.getNextMessages(chatId, mUserModel.getmJwt());
        });

        mChatModel.addMessageObserver(chatId, getViewLifecycleOwner(),
                list -> {
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    rv.getAdapter().notifyDataSetChanged();
                    rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                });

        //Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(chatId,
                    mUserModel.getmJwt(),
                    binding.editMessage.getText().toString());
        });
        //when we get the response back from the server, clear the edittext
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                binding.editMessage.setText(""));

    }
}
