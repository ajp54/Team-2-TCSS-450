package edu.uw.tcss450.chatapp.ui.home.register;

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

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * a {@link androidx.lifecycle.ViewModel} for the register fragment
 */
public class RegisterViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    /**
     * Class constructor
     *
     * @param application application currently running.
     */
    public RegisterViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Adds an observer to the MutableLiveData.
     *
     * @param owner the application's LifecycleOwner.
     * @param observer observer of the parent class.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Handles Volley errors.
     *
     * @param error the error that needs to be handled.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:\"" + data +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * Attempts to send the user's registration data to the server.
     *
     * @param fname user's first name
     * @param lname user's last name
     * @param email user's email
     * @param password user's password
     *
     * @author Charles Bryan
     * @version 1.0
     */
    public void connect(final String fname, final String lname, final String email,
                        final String password) {
        //String url = "https://cfb3-lab4-backend-2020sp.herokuapp.com/auth";
        //String url = "http://localhost:5000/auth";
        String url = "https://team-2-tcss-450-backend.herokuapp.com/auth";
        JSONObject body = new JSONObject(); try {
            body.put("first", fname);
            body.put("last", lname);
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace(); }
        Request request = new JsonObjectRequest( Request.Method.POST,
                url,
                body, mResponse::setValue, this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy( 10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request); }
}
