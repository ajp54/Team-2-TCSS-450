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

    List<ContactPending> list = new ArrayList<ContactPending>();

    public MutableLiveData<List<ContactPending>> getmContactPendingList() {
        return mContactPendingList;
    }

    public MutableLiveData<List<ContactPending>> mContactPendingList;
    List<String> people;


    public ContactPendingViewModel(@NonNull Application application) {
        super(application);
        if (mContactPendingList == null) {
            mContactPendingList = new MutableLiveData<>();
            mContactPendingList.setValue(new ArrayList<>());
            people =  new ArrayList<String>();
        }
    }

    public void addContactPendingObserver(String jwt,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ContactPending>> observer) {
        mContactPendingList.observe(owner, observer);
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

    public void connectAccept(String jwt, int position) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts";
        JSONObject body = new JSONObject(); try {
            body.put("username", people.get(position));
        } catch (JSONException e) {
            e.printStackTrace(); }
        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                this::handleAcceptResult,
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

    }

    public void connectReject(String jwt, int position) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts?username=" + people.get(position);
        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                this::handleRejectResult,
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

    }

    private void handleResult(final JSONObject response) {
        list.clear();

        boolean hasNewInfo = false;
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CONTACTS PENDING", "recieved a response");
            //list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray myContacts = response.getJSONArray("rows");
            Log.i("CONTACTS PENDING", "rows length: " + myContacts.length());
            //List<Integer> myIds = new ArrayList<Integer>();
            for(int i = 0; i < myContacts.length(); i++) {
                JSONObject contact = myContacts.getJSONObject(i);
                String username = contact.getString("username");
                Log.i("CONTACTS PENDING", "username: " + username);

                if(!people.contains(username)){
                    people.add(username);
                    hasNewInfo = true;
                }

                list.add(new ContactPending(new ContactPending.Builder(username)));
            }
            if(hasNewInfo) {
                mContactPendingList.setValue(list);
            }

        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactsPendingViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handleAcceptResult(final JSONObject response) {
        people.clear();
        List<ContactPending> tempList = list;
        list.clear();

        boolean hasNewInfo = false;
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CONTACTS ACCEPT", "recieved a response");

            JSONArray myContact = response.getJSONArray("rows");
            Log.i("CONTACTS ACCEPT", "rows length: " + myContact.length());
            JSONObject contact = myContact.getJSONObject(0);
            String username = contact.getString("username");

            for(int i = 0; i < tempList.size(); i++) {
                String tempUsername = tempList.get(i).getUsername();
                Log.i("CONTACTS ACCEPT", "username: " + tempUsername);

                if(tempUsername == username) {
                    tempList.remove(i);
                } else {
                    list.add(new ContactPending(new ContactPending.Builder(username)));
                }

            }
            if(hasNewInfo) {
                mContactPendingList.setValue(list);
            }
            list = tempList;

        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactsPendingViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }

    }

    private void handleRejectResult(final JSONObject response) {
        people.clear();
        List<ContactPending> tempList = list;
        list.clear();

        boolean hasNewInfo = false;
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CONTACTS REJECT", "recieved a response");

            JSONArray myContact = response.getJSONArray("rows");
            Log.i("CONTACTS REJECT", "rows length: " + myContact.length());
            JSONObject contact = myContact.getJSONObject(0);
            String username = contact.getString("username");

            for(int i = 0; i < tempList.size(); i++) {
                String tempUsername = tempList.get(i).getUsername();
                Log.i("CONTACTS REJECT", "username: " + tempUsername);

                if(tempUsername == username) {
                    tempList.remove(i);
                } else {
                    list.add(new ContactPending(new ContactPending.Builder(username)));
                }

            }
            if(hasNewInfo) {
                mContactPendingList.setValue(list);
            }
            list = tempList;

        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactsPendingViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

}
