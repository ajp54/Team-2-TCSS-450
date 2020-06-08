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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.io.RequestQueueSingleton;

public class ContactsViewModel extends AndroidViewModel {
    public MutableLiveData<List<Contact>> getmContactList() {
        return mContactList;
    }

    private MutableLiveData<JSONObject> mUpdateContactsResponse;

    private List<Contact> list = new ArrayList<Contact>();

    private MutableLiveData<List<Contact>> mContactList;
    List<String> people;


    public ContactsViewModel(@NonNull Application application) {
        super(application);
        if (mContactList == null) {
            mContactList = new MutableLiveData<>();
            mContactList.setValue(new ArrayList<>());
            mUpdateContactsResponse = new MutableLiveData<>();
            mUpdateContactsResponse.setValue(new JSONObject());
            people = new ArrayList<String>();
        }
    }

    public void addContactObserver(String jwt,
                                   @NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<Contact>> observer) {
        mContactList.observe(owner, observer);
    }

    public void addUpdateContactsResponseObserver(@NonNull LifecycleOwner owner,
                                                  @NonNull Observer<? super JSONObject> observer) {
        mUpdateContactsResponse.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handelChatIdSuccess(final JSONObject response) {
        List<Contact> list = new ArrayList<Contact>();
        if (!response.has("memberId")) {
            throw new IllegalStateException("Unexpected response in ContactsViewModel: " + response);
        }
        try {
            Log.i("CONTACTS", "recieved a response");
            JSONArray memberIds = response.getJSONArray("rows");
            JSONArray myIds = new JSONArray();
            for(int i = 0; i < memberIds.length(); i++) {
                JSONObject contact = memberIds.getJSONObject(i);
                list.add(new Contact(new Contact.Builder("username", "first", "last")));
            }

            //inform observers of the change (setValue)
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
            mContactList.setValue(list);
    }

    public MutableLiveData<List<Contact>> connectGet(String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts?pending=false";
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

        return mContactList;
    }

    public void connectAdd(final String jwt, final String username) {
//        String url = "https://team-2-tcss-450-backend.herokuapp.com/contacts";
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts";
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleResult,
                this::handleEditError) {

            // TODO add a real jwt to the header
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy( 10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request); }

    public void connectRemove(final String jwt, final int position) {
//        String url = "https://team-2-tcss-450-backend.herokuapp.com/contacts";
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contacts?username=" + people.get(position);

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                this::handleRemoveResult,
                this::handleEditError) {

            // TODO add a real jwt to the header
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy( 10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request); }

    private void handleResult(final JSONObject response) {
        list.clear();
//        chatIds = new ArrayList<Integer>();
        boolean hasNewInfo = false;
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
                String first = contact.getString("firstname");
                String last = contact.getString("lastname");
                Log.i("CONTACTS", "username: " + username);
//                chatIds.add(id);
                //ChatMessage recentMessage = mMessages.get(id).getValue().get(mMessages.get(id).getValue().size());
                //list.add(new Contact(new Contact.Builder(username, "First", "Last")));
                if(!people.contains(username)){
                    people.add(username);
                    hasNewInfo = true;
                }
                list.add(new Contact(new Contact.Builder(username, first, last)));
            }
            if(hasNewInfo) {
                mContactList.setValue(list);
            }

//            for(int i = 0; i < myIds.size(); i++) {
//                chatIds.add(myIds.getInt(i));
//                Log.i("CHATROOM", "ID added: " + chatIds.get(i));
//            }
            //inform observers of the change (setValue)
            //getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Result ContactsViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mUpdateContactsResponse.setValue(response);
    }

    private void handleRemoveResult(final JSONObject response) {
        List<Contact> tempList = new ArrayList<>(list);

        list.clear();
        boolean hasNewInfo = false;
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CONTACTS", "recieved a response");
            //list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray myContacts = response.getJSONArray("rows");
            Log.i("CONTACTS", "rows length: " + myContacts.length());

            JSONObject contact = myContacts.getJSONObject(0);
            String username = contact.getString("username");
            //List<Integer> myIds = new ArrayList<Integer>();
            for(int i = 0; i < tempList.size(); i++) {
                String tempUsername = tempList.get(i).getUsername();
                String tempFirst = tempList.get(i).getFirstName();
                String tempLast = tempList.get(i).getLastName();
                Log.i("CONTACTS", "username: " + username);
//                chatIds.add(id);
                //ChatMessage recentMessage = mMessages.get(id).getValue().get(mMessages.get(id).getValue().size());
                //list.add(new Contact(new Contact.Builder(username, "First", "Last")));
                if(!people.contains(tempUsername)){
                    people.add(tempUsername);
                    hasNewInfo = true;
                }
                if(tempUsername.equals(username)) {
                    people.remove(tempUsername);
                } else {
                    list.add(new Contact(new Contact.Builder(tempUsername, tempFirst, tempLast)));
                }
            }
            if(hasNewInfo) {
                mContactList.setValue(list);
            }

//            for(int i = 0; i < myIds.size(); i++) {
//                chatIds.add(myIds.getInt(i));
//                Log.i("CHATROOM", "ID added: " + chatIds.get(i));
//            }
            //inform observers of the change (setValue)
            //getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Result ContactsViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mUpdateContactsResponse.setValue(response);
    }

    private void handleAddResult(final JSONObject response) {
        List<Contact> tempList = new ArrayList<>(list);

        list.clear();
        boolean hasNewInfo = false;
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CONTACTS", "recieved a response");
            //list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray myContacts = response.getJSONArray("rows");
            Log.i("CONTACTS", "rows length: " + myContacts.length());

            JSONObject contact = myContacts.getJSONObject(0);
            String username = contact.getString("username");
            //List<Integer> myIds = new ArrayList<Integer>();
            for(int i = 0; i < tempList.size(); i++) {
                String tempUsername = tempList.get(i).getUsername();
                String tempFirst = tempList.get(i).getFirstName();
                String tempLast = tempList.get(i).getLastName();
                Log.i("CONTACTS", "username: " + username);
//                chatIds.add(id);
                //ChatMessage recentMessage = mMessages.get(id).getValue().get(mMessages.get(id).getValue().size());
                //list.add(new Contact(new Contact.Builder(username, "First", "Last")));
                if(!people.contains(tempUsername)){
                    people.add(tempUsername);
                    hasNewInfo = true;
                }
                if(tempUsername.equals(username)) {
                    people.remove(tempUsername);
                } else {
                    list.add(new Contact(new Contact.Builder(tempUsername, tempFirst, tempLast)));
                }
            }
            if(hasNewInfo) {
                mContactList.setValue(list);
            }

//            for(int i = 0; i < myIds.size(); i++) {
//                chatIds.add(myIds.getInt(i));
//                Log.i("CHATROOM", "ID added: " + chatIds.get(i));
//            }
            //inform observers of the change (setValue)
            //getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Result ContactsViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mUpdateContactsResponse.setValue(response);
    }

    private void handleEditError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }

}
