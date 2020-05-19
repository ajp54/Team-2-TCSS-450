package edu.uw.tcss450.chatapp.ui.chat;

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

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatRoomViewHolder> {
    //Store all of the contacts to present
    private final List<ChatRoom> mChatRooms;

    private RecyclerViewClickListener mListener;

    public ChatRecyclerViewAdapter(List<ChatRoom> items, RecyclerViewClickListener listener) {
        this.mChatRooms = items;
        mListener = listener;
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
//        if (holder instanceof ChatRecyclerViewAdapter) {
//            ChatRecyclerViewAdapter rowHolder = (ChatRecyclerViewAdapter) holder;
//        }
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
            view.setOnClickListener(this);
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
