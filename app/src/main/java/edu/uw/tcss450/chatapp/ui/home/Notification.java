package edu.uw.tcss450.chatapp.ui.home;

import java.io.Serializable;

public class Notification implements Serializable {
    private final String mUsername;
    private final String mMessage;


//    public static class Builder {
//        private final String mUsername;
//        private final String mMessage;
//
//        public Builder(String username, String message) {
//            this.mUsername = username;
//            this.mMessage = message;
//        }
//
//        public NotificationBuilder build() {
//            return new NotificationBuilder(this);
//        }
//    }

    public Notification(String username, String message) {
        this.mUsername = username;
        this.mMessage = message;
    }

//    private NotificationBuilder(final Builder builder) {
//        this.mUsername = builder.mUsername;
//        this.mMessage = builder.mMessage;
//    }

    public String getUsername() {
        return mUsername;
    }

    public String getMessage() {
        return mMessage;
    }
}
