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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import edu.uw.tcss450.chatapp.R;

public class ContactsViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contact>> mContactList;

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>();
        mContactList.setValue(new ArrayList<>());
    }

    public void addBlogListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<Contact>> observer) {
        mContactList.observe(owner, observer);
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
            if (root.has(getString.apply(R.string.keys_json_contacts_username))) {
                JSONObject username =
                        root.getJSONObject(getString.apply(
                                R.string.keys_json_contacts_username));
                if (username.has(getString.apply(R.string.keys_json_contacts_first))) {
                    JSONObject first =
                            root.getJSONObject(getString.apply(
                                    R.string.keys_json_contacts_first));
                    if (first.has(getString.apply(R.string.keys_json_contacts_last))) {
                        JSONObject last =
                                root.getJSONObject(getString.apply(
                                        R.string.keys_json_contacts_last));

                    } else {
                        Log.e("ERROR!", "No last name");
                    }

                } else {
                    Log.e("ERROR!", "No first name");
                }
            } else {
                Log.e("ERROR!", "No username");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }

        mContactList.setValue(mContactList.getValue());
    }

    public void connectGet() {
        String url =
                "https://https://team-2-tcss-450-backend.herokuapp.com/contacts/get";
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
                headers.put("Authorization", "placeholder for jwt");
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
