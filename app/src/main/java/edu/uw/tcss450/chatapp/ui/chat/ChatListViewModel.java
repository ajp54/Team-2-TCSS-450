package edu.uw.tcss450.chatapp.ui.chat;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import edu.uw.tcss450.chatapp.R;

public class ChatListViewModel extends AndroidViewModel {
    private MutableLiveData<List<ChatRoom>> mRoomList;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mRoomList = new MutableLiveData<>();
        mRoomList.setValue(new ArrayList<>());
    }

    public void addChatListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<ChatRoom>> observer) {
        mRoomList.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handleResult(final JSONObject result) {
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;
            if (root.has(getString.apply(R.string.keys_json_chat_room_response))) {
                JSONObject response =
                        root.getJSONObject(getString.apply(
                                R.string.keys_json_chat_room_response));
                if (response.has(getString.apply(R.string.keys_json_chat_room_data))) {
                    JSONArray data = response.getJSONArray(
                            getString.apply(R.string.keys_json_chat_room_data));

                    //TODO  change the people to be a more appropriate data type
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonChatRoom = data.getJSONObject(i);
                        mRoomList.getValue().add(new ChatRoom.Builder(
                                jsonChatRoom.getString(
                                        getString.apply(
                                                R.string.keys_json_chat_room_people)),
                                jsonChatRoom.getString(
                                        getString.apply(
                                                R.string.keys_json_chat_room_chatid)),
                                jsonChatRoom.getString(
                                        getString.apply(
                                                R.string.keys_json_chat_room_message)))
                                .build());
                    }
                } else {
                    Log.e("ERROR!", "No data array");
                }
            } else {
                Log.e("ERROR!", "No response");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }

        mRoomList.setValue(mRoomList.getValue());
    }

    public void connectGet(String jwt) {
        String url =
                "https://team-2-tcss-450-backend.herokuapp.com/chats/get";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleResult,
                this::handleError) {

            // TODO add a real jwt to the header
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
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

}
