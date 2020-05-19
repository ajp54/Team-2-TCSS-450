package edu.uw.tcss450.chatapp.ui.chat;

import java.io.Serializable;

public class ChatRoom implements Serializable {

    private final String mPeople;
    private final String mChatID;
    private final String mRecentMessage;

    //TODO  change the people to be a more appropriate data type
    public ChatRoom(final ChatRoom.Builder builder) {
        this.mPeople = builder.mPeople;
        this.mChatID = builder.mChatID;
        this.mRecentMessage = builder.mRecentMessage;

    }
    /**
     * Helper class for building the chat room.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mPeople;
        private  String mChatID;
        private  String mRecentMessage;


        /**
         * Constructs a new Builder.
         *
         * @param people an array or people in the chat room.
         * @param chatID the id of the chat room.
         * @param message the most recent message sent inside the chat room.
         */
        public Builder(String people, String chatID, String message) {
            this.mPeople = people;
            this.mChatID = chatID;
            this.mRecentMessage = message;
        }

        public ChatRoom build() {
            return new ChatRoom(this);
        }

    }


    public String getPeople() {
        return mPeople;
    }

    public String getChatID() {
        return mChatID;
    }

    public String getRecentMessage() {
        return mRecentMessage;
    }
}
