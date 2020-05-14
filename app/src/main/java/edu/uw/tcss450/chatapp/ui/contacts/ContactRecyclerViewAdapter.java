package edu.uw.tcss450.chatapp.ui.contacts;

import android.graphics.drawable.Icon;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsCardBinding;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>{
    //Store all of the contacts to present
    private final List<Contact> mContacts;

    public ContactRecyclerViewAdapter(List<Contact> items) {
        this.mContacts = items;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contacts_card, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Contact Recycler View.
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentContactsCardBinding binding;

        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactsCardBinding.bind(view);
        }


        void setContact(final Contact contact) {
            Log.i("RECYCLER", "getting Contact information");
            binding.textName.setText(contact.getUsername());
            binding.textUsername.setText(contact.getFirstName() + " " + contact.getLastName());

        }

    }
}
