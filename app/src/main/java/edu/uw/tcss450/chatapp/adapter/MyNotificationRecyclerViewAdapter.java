package edu.uw.tcss450.chatapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.ui.home.Notification;

public class MyNotificationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static int TYPE_REQUEST = 1;
    private static int TYPE_MESSAGE = 2;

    private List<Notification> mNotifications;

    public DetailsAdapterListener onClickListener;

//    public ItemClickListener onItemClickListener;

    private Context mContext;


    public MyNotificationRecyclerViewAdapter(Context context, List<Notification> items) {
        this.mNotifications = items;
        this.mContext = context;
//        this.onClickListener = listener;
//        this.onItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_REQUEST) { // for call layout
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification_request, viewGroup, false);
            return new RequestViewHolder(view);

        } else { // for email layout
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification_message, viewGroup, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_REQUEST) {
            ((RequestViewHolder) viewHolder).setRequestDetails(mNotifications.get(position));
        } else {
            ((MessageViewHolder) viewHolder).setMessageDetails(mNotifications.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mNotifications.get(position).getMessage())) {
            return TYPE_REQUEST;
        } else {
            return TYPE_MESSAGE;
        }
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private ImageButton button_accept;
        private ImageButton button_reject;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_request_name);
            button_accept = itemView.findViewById(R.id.button_accept);
            button_reject = itemView.findViewById(R.id.button_reject);

            button_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.acceptOnClick(v, getAdapterPosition());
                }
            });
            button_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.rejectOnClick(v, getAdapterPosition());
                }
            });
        }

        private void setRequestDetails(Notification notification) {
            txtName.setText(notification.getUsername());
        }

    }
    class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private LinearLayout item;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_message);
            item = itemView.findViewById(R.id.message_item);

//            item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(v, getAdapterPosition());
//                }
//            });
        }

        private void setMessageDetails(Notification notification) {
            txtName.setText(notification.getMessage());
        }


    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface DetailsAdapterListener {

        void acceptOnClick(View v, int position);

        void rejectOnClick(View v, int position);
    }
}
