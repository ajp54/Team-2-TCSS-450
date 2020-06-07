package edu.uw.tcss450.chatapp.ui.chat;

import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentChatRoomCardBinding;
import edu.uw.tcss450.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatRoomViewModel;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatRoomViewHolder> {
    private ChatRoomViewModel mChatModel;
    private UserInfoViewModel mUserModel;
    private View parentView;

    String myEmail;
    String myJwt;

    //Store all of the contacts to present
    private final List<ChatRoom> mChatRooms;

    private RecyclerViewClickListener mListener;

    public ChatRecyclerViewAdapter(List<ChatRoom> items, RecyclerViewClickListener listener, ChatRoomViewModel chatModel, UserInfoViewModel userModel, View view) {
        this.mChatRooms = items;
        mChatModel = chatModel;
        mUserModel = userModel;
        mListener = listener;
        parentView = view;
    }

    @NonNull
    @Override
    public ChatRecyclerViewAdapter.ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRecyclerViewAdapter.ChatRoomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_room_card, parent, false), mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewAdapter.ChatRoomViewHolder holder, int position) {
        holder.setChatRoom(mChatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }



    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the ChatRoom Recycler View.
     */
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public FragmentChatRoomCardBinding binding;
        private RecyclerViewClickListener mListener;

        public ChatRoomViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mView = view;
            binding = FragmentChatRoomCardBinding.bind(view);
            mListener = listener;
            binding.buittonMore.setOnClickListener(this::handleMoreOrLess);
            binding.buttonDelete.setOnClickListener(button -> deleteRoom());
            binding.buttonEdit.setOnClickListener(button -> navigateToContactJoin());
            view.setOnClickListener(this);
        }

        /**
         * When the button is clicked in the more state, expand the card to display
         * the blog preview and switch the icon to the less state.  When the button
         * is clicked in the less state, shrink the card and switch the icon to the
         * more state.
         *
         * @param button the button that was clicked
         */
        private void handleMoreOrLess(final View button) {
            if (binding.buttonEdit.getVisibility() == View.GONE) {
                binding.buttonEdit.setVisibility(View.VISIBLE);
                binding.buttonDelete.setVisibility(View.VISIBLE);
                binding.buittonMore.setImageIcon(
                        Icon.createWithResource(
                                mView.getContext(),
                                R.drawable.ic_less_grey_24dp));
            } else {
                binding.buttonEdit.setVisibility(View.GONE);
                binding.buttonDelete.setVisibility(View.GONE);
                binding.buittonMore.setImageIcon(
                        Icon.createWithResource(
                                mView.getContext(),
                                R.drawable.ic_more_grey_24dp));
            }
        }

        private void deleteRoom() {
            int chatId = Integer.parseInt(mChatRooms.get(this.getAdapterPosition()).getChatID());
            Log.i("CHATRECYCLER", "removing " + mUserModel.getEmail() + " from chat room " + chatId);
            mChatModel.deleteChatMember(chatId, mUserModel.getEmail(), mUserModel.getmJwt());

        }

        private void navigateToContactJoin() {
            int chatId = Integer.parseInt(mChatRooms.get(this.getAdapterPosition()).getChatID());
//        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
            Navigation.findNavController(parentView).navigate(ChatListFragmentDirections
                    .actionNavigationChatToContactJoinFragment(false, chatId));
        }

        void setChatRoom(final ChatRoom chatRoom) {
            Log.i("RECYCLER", "getting chat room information");
            binding.textPeople.setText(chatRoom.getPeople());
            binding.textMessage.setText(chatRoom.getRecentMessage());

        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    public void replaceItem(final ChatRoom newItem, final int pos) {
        mChatRooms.remove(pos);
        mChatRooms.add(pos, newItem);

        notifyItemChanged(pos);
        notifyDataSetChanged();
    }
}
