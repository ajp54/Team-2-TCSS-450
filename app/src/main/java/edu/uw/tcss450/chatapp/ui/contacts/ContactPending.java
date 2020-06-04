package edu.uw.tcss450.chatapp.ui.contacts;

import java.io.Serializable;

public class ContactPending implements Serializable {
    private final String mUsername;

    public ContactPending(final Builder builder) {
        this.mUsername = builder.mUsername;


    }
    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mUsername;


        /**
         * Constructs a new Builder.
         *
         * @param user the contact's username
         */
        public Builder(String user) {
            this.mUsername = user;

        }

        public ContactPending build() {
            return new ContactPending(this);
        }

    }



    public String getUsername() {
        return mUsername;
    }


}
