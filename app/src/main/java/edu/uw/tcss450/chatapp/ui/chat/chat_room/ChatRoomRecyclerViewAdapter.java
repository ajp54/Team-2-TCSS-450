package edu.uw.tcss450.chatapp.ui.chat.chat_room;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentChatMessageBinding;
import edu.uw.tcss450.chatapp.utils.ThemeChanger;
//import edu.uw.tcss450.chatapp.databinding.FragmentChatMessageBinding;

public class ChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.MessageViewHolder> {

    private final List<ChatMessage> mMessages;
    private final String mEmail;
    public ChatRoomRecyclerViewAdapter(List<ChatMessage> messages, String email) {
        this.mMessages = messages;
        mEmail = email;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.setMessage(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentChatMessageBinding binding;

        public MessageViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentChatMessageBinding.bind(view);
        }

        void setMessage(final ChatMessage message) {
            final Resources res = mView.getContext().getResources();
            final MaterialCardView card = binding.cardRoot;

            int standard = (int) res.getDimension(R.dimen.chat_margin);
            int extended = (int) res.getDimension(R.dimen.chat_margin_sided);

            int primaryColor;
            int accentColor;
            ThemeChanger theme = new ThemeChanger();
            Log.i("RECYCLER", "theme:" + theme.getTheme());
            switch (theme.getTheme()) {
                case 0:
                    primaryColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.orangeColorPrimary, null),
                            16);
                    accentColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.orangeColorPrimary, null),
                            16);
                    break;
                case 1:
                    primaryColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.orangeDarkColorAccent, null),
                            16);
                    accentColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.orangeDarkColorAccent, null),
                            16);
                    break;
                case 2:
                    primaryColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.blueLightColorAccent, null),
                            16);
                    accentColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.blueLightColorAccent, null),
                            16);
                    break;
                default:
                    primaryColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.blueDarkColorAccent, null),
                            16);
                    accentColor = ColorUtils.setAlphaComponent(
                            res.getColor(R.color.blueDarkColorAccent, null),
                            16);
                    break;
            }
            Log.i("RECYCLER", "primary color used:" + primaryColor);

            if (mEmail.equals(message.getSender())) {
                //This message is from the user. Format it as such
                binding.textMessage.setText(message.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                //Set the left margin
                layoutParams.setMargins(extended, standard, standard, standard);
                // Set this View to the right (end) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.END;

                card.setCardBackgroundColor(primaryColor);

                binding.textMessage.setTextColor(
                        res.getColor(R.color.colorOffWhite, null));

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(primaryColor);

                //Round the corners on the left side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCornerSize(0)
                                .setTopRightCornerSize(0)
                                .build());

                card.requestLayout();
            } else {
                //This message is from another user. Format it as such
                binding.textMessage.setText(message.getSender() +
                        ": " + message.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                //Set the right margin
                layoutParams.setMargins(standard, standard, extended, standard);
                // Set this View to the left (start) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.START;


                card.setCardBackgroundColor(accentColor);

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(accentColor);

                binding.textMessage.setTextColor(
                        res.getColor(R.color.colorOffWhite, null));

                //Round the corners on the right side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCornerSize(0)
                                .setTopLeftCornerSize(0)
                                .build());
                card.requestLayout();
            }
        }
    }
}

