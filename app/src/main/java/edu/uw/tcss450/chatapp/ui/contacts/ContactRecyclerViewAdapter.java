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
import edu.uw.tcss450.chatapp.ui.chat.ChatRecyclerViewAdapter;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>{
    //Store all of the contacts to present
    private final List<Contact> mContacts;

    private RecyclerViewClickListener mListener;

    public ContactRecyclerViewAdapter(List<Contact> items, RecyclerViewClickListener listener) {
        this.mContacts = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ContactRecyclerViewAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactRecyclerViewAdapter.ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contacts_card, parent, false), mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ContactRecyclerViewAdapter.ContactViewHolder holder, int position) {
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
    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public FragmentContactsCardBinding binding;
        private RecyclerViewClickListener mListener;

        public ContactViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mView = view;
            binding = FragmentContactsCardBinding.bind(view);
            mListener = listener;
            view.setOnClickListener(this);
        }


        void setContact(final Contact contact) {
            Log.i("RECYCLER", "getting Contact information");
            binding.textName.setText(contact.getFirstName() + " " + contact.getLastName());
            binding.textUsername.setText(contact.getUsername());

        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }

    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    public void replaceItem(final Contact newItem, final int pos) {
        mContacts.remove(pos);
        mContacts.add(pos, newItem);

        notifyItemChanged(pos);
        notifyDataSetChanged();
    }
}
