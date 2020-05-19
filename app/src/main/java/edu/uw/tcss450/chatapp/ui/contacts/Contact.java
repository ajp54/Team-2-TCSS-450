package edu.uw.tcss450.chatapp.ui.contacts;

import java.io.Serializable;

public class Contact implements Serializable {
    private final String mUsername;
    private final String mFirstName;
    private final String mLastName;

    public Contact(final Builder builder) {
        this.mUsername = builder.mUsername;
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;

    }
    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mUsername;
        private  String mFirstName;
        private  String mLastName;


        /**
         * Constructs a new Builder.
         *
         * @param user the contact's username
         * @param first the contact's first name
         * @param last the contact's last name
         */
        public Builder(String user, String first, String last) {
            this.mUsername = user;
            this.mFirstName = first;
            this.mLastName = last;
        }

        public Contact build() {
            return new Contact(this);
        }

    }



    public String getUsername() {
        return mUsername;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

}
