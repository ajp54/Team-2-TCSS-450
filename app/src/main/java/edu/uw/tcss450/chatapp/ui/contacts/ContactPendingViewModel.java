package edu.uw.tcss450.chatapp.ui.contacts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.io.RequestQueueSingleton;

public class ContactPendingViewModel extends AndroidViewModel {

    public MutableLiveData<List<ContactPending>> getmContactPendingList() {
        return mContactPendingList;
    }

    private MutableLiveData<List<ContactPending>> mContactPendingList;


    public ContactPendingViewModel(@NonNull Application application) {
        super(application);
        if (mContactPendingList == null) {
            mContactPendingList = new MutableLiveData<>();
            mContactPendingList.setValue(new ArrayList<>());
        }
    }

    public void addContactPendingObserver(String jwt,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ContactPending>> observer) {
        connectGet(jwt).observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handleContactPendingSuccess(final JSONObject response) {
        List<ContactPending> list = new ArrayList<ContactPending>();
        if (!response.has("memberId")) {
            throw new IllegalStateException("Unexpected response in ContactsViewModel: " + response);
        }
        try {
            Log.i("CONTACTS PENDING", "recieved a response");
            JSONArray memberIds = response.getJSONArray("rows");
            JSONArray myIds = new JSONArray();
            for(int i = 0; i < memberIds.length(); i++) {
                JSONObject contact = memberIds.getJSONObject(i);
                list.add(new ContactPending(new ContactPending.Builder("username")));
            }

            //inform observers of the change (setValue)
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mContactPendingList.setValue(list);
    }

    public MutableLiveData<List<ContactPending>> connectGet(String jwt) {
        System.out.println("thishishishishishishishishishishis");
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts?pending=true";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleResult,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);

        return mContactPendingList;
    }

    private void handleResult(final JSONObject response) {
        List<ContactPending> list = new ArrayList<ContactPending>();
//        chatIds = new ArrayList<Integer>();
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CONTACTS", "recieved a response");
            //list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray myContacts = response.getJSONArray("rows");
            Log.i("CONTACTS", "rows length: " + myContacts.length());
            //List<Integer> myIds = new ArrayList<Integer>();
            for(int i = 0; i < myContacts.length(); i++) {
                JSONObject contact = myContacts.getJSONObject(i);
                String username = contact.getString("username");
                Log.i("CONTACTS", "username: " + username);
//                chatIds.add(id);
                //ChatMessage recentMessage = mMessages.get(id).getValue().get(mMessages.get(id).getValue().size());
                list.add(new ContactPending(new ContactPending.Builder(username)));
            }
            mContactPendingList.setValue(list);

//            for(int i = 0; i < myIds.size(); i++) {
//                chatIds.add(myIds.getInt(i));
//                Log.i("CHATROOM", "ID added: " + chatIds.get(i));
//            }
            //inform observers of the change (setValue)
            //getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactsPendingViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

}
