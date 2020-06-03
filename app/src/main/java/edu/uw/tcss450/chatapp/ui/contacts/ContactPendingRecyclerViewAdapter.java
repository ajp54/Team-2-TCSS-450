package edu.uw.tcss450.chatapp.ui.contacts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.databinding.FragmentContactsPendingCardBinding;

public class ContactPendingRecyclerViewAdapter extends RecyclerView.Adapter<ContactPendingRecyclerViewAdapter.ContactPendingViewHolder> {

    private final List<ContactPending> mContactsPending;

    private RecyclerViewClickListener mListener;

    public ContactPendingRecyclerViewAdapter(List<ContactPending> items, RecyclerViewClickListener listener) {
        this.mContactsPending = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ContactPendingRecyclerViewAdapter.ContactPendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactPendingRecyclerViewAdapter.ContactPendingViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contacts_pending_card, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactPendingRecyclerViewAdapter.ContactPendingViewHolder holder, int position) {
        holder.setContact(mContactsPending.get(position));
    }

    @Override
    public int getItemCount() { return mContactsPending.size(); }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Contact Recycler View.
     */
    public class ContactPendingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public FragmentContactsPendingCardBinding binding;
        private RecyclerViewClickListener mListener;

        public ContactPendingViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mView = view;
            binding = FragmentContactsPendingCardBinding.bind(view);
            mListener = listener;
            view.setOnClickListener(this);
        }


        void setContact(final ContactPending contactPending) {
            Log.i("RECYCLER", "getting Contact information");
            binding.textName.setText(contactPending.getUsername());
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }

    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }


}
