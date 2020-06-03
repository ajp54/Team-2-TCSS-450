package edu.uw.tcss450.chatapp.ui.chat.chat_room;

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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.chatapp.R;
import edu.uw.tcss450.chatapp.io.RequestQueueSingleton;
import edu.uw.tcss450.chatapp.ui.chat.ChatRoom;

public class ChatRoomViewModel extends AndroidViewModel {

    /**
     * A Map of Lists of Chat Messages.
     * The Key represents the Chat ID
     * The value represents the List of (known) messages for that that room.
     */
    private Map<Integer, MutableLiveData<List<ChatMessage>>> mMessages;
    private MutableLiveData<List<ChatRoom>> mRoomList;

    private MutableLiveData<JSONObject> mChatCreateResponse;

    private List<Integer> chatIds;
    private String mJwt;
    private String mEmail;
    private int recentRoom;

    public ChatRoomViewModel(@NonNull Application application) {
        super(application);
        mMessages = new HashMap<>();
        mRoomList = new MutableLiveData<>();
        mRoomList.setValue(new ArrayList<>());

        mChatCreateResponse = new MutableLiveData<>();
        mChatCreateResponse.setValue(new JSONObject());
    }

    /**
     * Register as an observer to listen to a specific chat room's list of messages.
     * @param chatId the chatid of the chat to observer
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addMessageObserver(int chatId,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ChatMessage>> observer) {
        getOrCreateMapEntry(chatId).observe(owner, observer);
    }

    /**
     * Register as an observer to listen to a specific chat room's list of messages.
     * @param jwt the user's jwt
     * @param email the user's email
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addChatRoomObserver(String jwt, String email,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ChatRoom>> observer) {
        getChatIds(email, jwt).observe(owner, observer);
    }

    public void addChatCreateResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mChatCreateResponse.observe(owner, observer);
    }

    /**
     * Return a reference to the List<> associated with the chat room. If the View Model does
     * not have a mapping for this chatID, it will be created.
     *
     * WARNING: While this method returns a reference to a mutable list, it should not be
     * mutated externally in client code. Use public methods available in this class as
     * needed.
     *
     * @param chatId the id of the chat room List to retrieve
     * @return a reference to the list of messages
     */
    public List<ChatMessage> getMessageListByChatId(final int chatId) {
        return getOrCreateMapEntry(chatId).getValue();
    }

    private MutableLiveData<List<ChatMessage>> getOrCreateMapEntry(final int chatId) {
        if(!mMessages.containsKey(chatId)) {
            mMessages.put(chatId, new MutableLiveData<>(new ArrayList<>()));
            ChatRoom room = new ChatRoom(new ChatRoom.Builder("people", Integer.toString(chatId), "recent message"));
            List<ChatRoom> newList= mRoomList.getValue();
            newList.add(room);
            mRoomList.setValue(newList);
            Log.i("VIEW_MODEL", "added message to hash table with id: " + chatId);
        }
        return mMessages.get(chatId);
    }

    /**
     * Makes a request to the web service to get the first batch of messages for a given Chat Room.
     * Parses the response and adds the ChatMessage object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent requests to the web service for a given chat room should be made from
     * getNextMessages()
     *
     * @param chatId the chatroom id to request messages of
     * @param jwt the users signed JWT
     */
    public void getFirstMessages(final int chatId, final String jwt) {
        mJwt = jwt;
        String url = getApplication().getResources().getString(R.string.base_url) +
                "messages/" + chatId;
        //Log.i("CHATROOM", "url: " + url);
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handelMessageSuccess,
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

        //code here will run
    }

    /**
     * Makes a request to the web service to get the next batch of messages for a given Chat Room.
     * This request uses the earliest known ChatMessage in the associated list and passes that
     * messageId to the web service.
     * Parses the response and adds the ChatMessage object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent calls to this method receive earlier and earlier messages.
     *
     * @param chatId the chatroom id to request messages of
     * @param jwt the users signed JWT
     */
    public void getNextMessages(final int chatId, final String jwt) {
        mJwt = jwt;
        String url = getApplication().getResources().getString(R.string.base_url) +
                "messages/" +
                chatId +
                "/" +
                mMessages.get(chatId).getValue().get(0).getMessageId();

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handelMessageSuccess,
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

        //code here will run
    }

    public  MutableLiveData<List<ChatRoom>> getChatIds(String email, final String jwt) {
        mEmail = email;
        mJwt = jwt;
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chat_list/";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handelChatIdSuccess,
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

        //for (int i = 0; i < mRoomList.getValue().size())
        //addChatMembers(1, jwt);

        return mRoomList;

        //code here will run
    }


    public void addChatMembers(final int chatId, final String jwt) {

        mJwt = jwt;
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/" + chatId;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handelChatMembersSuccess,
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

        //code here will run
    }

    /**
     * When a chat message is received externally to this ViewModel, add it
     * with this method.
     * @param chatId
     * @param message
     */
    public void addMessage(final int chatId, final ChatMessage message) {
        List<ChatMessage> list = getMessageListByChatId(chatId);
        list.add(message);
        getOrCreateMapEntry(chatId).setValue(list);
    }

//    public void addRecentMessages() {
//        if (mRoomList != null && mRoomList.getValue().size() > 0) {
//            for (int i = 0; i < mRoomList.getValue().size(); i++) {
//                ChatRoom curRoom = mRoomList.getValue().get(i);
//                if (mMessages != null && mMessages.size() > 0) {
//                    List<ChatMessage> messages = mMessages.get(curRoom.getChatID()).getValue();
//                    String message = messages.get(messages.size() - 1).getMessage();
//                    Log.i("CHATROOM", "recent message: " + message);
//                    curRoom.setRecentMessage(messages.get(messages.size() - 1).getMessage());
//                }
//            }
//        }
//    }

    public void createChatRoom(String jwt) {
        mJwt = jwt;
        JSONObject params = new JSONObject();
        try {
            //input your API parameters
            params.put("name","chatroom");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/";
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                this::handelChatCreateSuccess,
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

    //the user's email will work in place of the username
    public void joinChatRoom(int chatId, String username, String jwt) {
        Log.i("CHATROOM", "user " + username +  " joining room " + chatId);
        mEmail = username;
        mJwt = jwt;
        JSONObject params = new JSONObject();
        try {
            //input your API parameters
            params.put("username",username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getApplication().getResources().getString(R.string.base_url) +
                "chats/" + chatId;
        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                params,
                this::handelJoinChatSuccess,
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

    private void handelMessageSuccess(final JSONObject response) {
        List<ChatMessage> list;
        if (!response.has("chatId")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray messages = response.getJSONArray("rows");
            for(int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                ChatMessage cMessage = new ChatMessage(
                        message.getInt("messageid"),
                        message.getString("message"),
                        message.getString("email"),
                        message.getString("timestamp")
                );
                if (!list.contains(cMessage)) {
                    // don't add a duplicate
                    list.add(0, cMessage);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("Chat message already received",
                            "Or duplicate id:" + cMessage.getMessageId());
                }

            }
            //inform observers of the change (setValue)
            getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handelChatIdSuccess(final JSONObject response) {
        List<ChatRoom> list = new ArrayList<ChatRoom>();
        chatIds = new ArrayList<Integer>();
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CHATROOM", "recieved a response");
            //list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray ids = response.getJSONArray("rows");
            Log.i("CHATROOM", "rows length: " + ids.length());
            //List<Integer> myIds = new ArrayList<Integer>();
            for(int i = 0; i < ids.length(); i++) {
                JSONObject idObject = ids.getJSONObject(i);
                int id = idObject.getInt("chatid");
                chatIds.add(id);
                //ChatMessage recentMessage = mMessages.get(id).getValue().get(mMessages.get(id).getValue().size());
                list.add(new ChatRoom(new ChatRoom.Builder("people", Integer.toString(id), "message")));
            }
            if (mRoomList.getValue().size() == 0 || chatIds.size() != mRoomList.getValue().size()) {
                mRoomList.setValue(list);
            }
//            else {
//                for (int i = 0; i < list.size(); i++) {
//                    if (!chatIds.contains(list.get(i).getChatID())) {
//                        mRoomList.getValue().add(list.get(i));
//                    }
//                }
//            }

//            for(int i = 0; i < myIds.size(); i++) {
//                chatIds.add(myIds.getInt(i));
//                Log.i("CHATROOM", "ID added: " + chatIds.get(i));
//            }
            //inform observers of the change (setValue)
            //getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        for(int i = 0; i < chatIds.size(); i++) {
            addChatMembers(chatIds.get(i), mJwt);
        }
//        addRecentMessages();
    }

    /**
     * adds the names of the members to the chat rooms.
     * @param response The JSON object containing the response from the backend.
     */
    private void handelChatMembersSuccess(final JSONObject response) {
        if (!response.has("rows")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            Log.i("CHATROOM", "recieved a response");
            //list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray names = response.getJSONArray("rows");
            Log.i("CHATROOM", "names length: " + names.length());
            //List<Integer> myIds = new ArrayList<Integer>();
            String allNames = "";
            for(int i = 0; i < names.length(); i++) {
                JSONObject name = names.getJSONObject(i);
                if (i < names.length()-1) {
                    allNames = allNames + name.getString("firstname") + ", ";
                } else {
                    allNames = allNames + name.getString("firstname");
                }
            }
            int chatId = Integer.parseInt(response.getString("chatId"));
            Log.i("CHATROOM", "all names: " + allNames);
            int index = chatIds.indexOf(chatId);
            Log.i("CHATROOM", "chatId index: " + index);
            if (mRoomList.getValue() != null && mRoomList.getValue().get(index) != null) {
                mRoomList.getValue().get(index).setPeople(allNames);
            }
            //Log.i("CHATROOM", "getting names from charRoom object: " + mRoomList.getValue().get(0).getPeople());

//            for(int i = 0; i < myIds.size(); i++) {
//                chatIds.add(myIds.getInt(i));
//                Log.i("CHATROOM", "ID added: " + chatIds.get(i));
//            }
            //inform observers of the change (setValue)
            //getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handelChatCreateSuccess(final JSONObject response) {
        int chatId = 0;
        if (!response.has("sucess")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
//            Log.i("CHATROOM", "recieved a response");
            //list = getMessageListByChatId(response.getInt("chatId"));
//            JSONObject result = response.getJSONObject("sucess");
//            Log.i("CHATROOM", "names length: " + names.length());
            //List<Integer> myIds = new ArrayList<Integer>();
            if (response.getBoolean("sucess") == false)
                Log.e("CHATROOM", "failed to create a new chat room");
            else {
                Log.i("CHATROOM", "created a new chat room successfully");
                chatId = response.getInt("chatID");
                recentRoom = chatId;
                joinChatRoom(chatId, mEmail, mJwt);
            }
            //Log.i("CHATROOM", "getting names from charRoom object: " + mRoomList.getValue().get(0).getPeople());

        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mChatCreateResponse.setValue(response);
    }

    private void handelJoinChatSuccess(final JSONObject response) {
        if (!response.has("sucess")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        } else {
            Log.i("CHATROOM", "User has been added successfully");
        }
    }

    private void handleError(final VolleyError error) {
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

    public List<Integer> getChatIdList() {
        return chatIds;
    }

    public int getRecentRoom() {
        return recentRoom;
    }
}
