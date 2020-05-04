package edu.uw.tcss450.chatapp.io;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * A type of {@link RequestQueue}.
 */
public class RequestQueueSingleton {

    private static RequestQueueSingleton instance;
    private static Context context;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    /**
     * Class constructor
     *
     * @param context
     *
     * @author Charles Bryan
     * @version 1.0
     */
    private RequestQueueSingleton(Context context) {
        RequestQueueSingleton.context = context;
        mRequestQueue = getmRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Gets an instance of RequestQueueSingleton.
     *
     * @param context The application's Context Object.
     * @return An instance of the RequestQueueSingleton.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    /**
     * Gets the RequestQueue attached to this RequestQueueSingleton Object.
     *
     * @return A RequestQueue Object.
     *
     * @author Charles Bryan
     * @version 1.0
     */
    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds a new request to the RequestQueue
     *
     * @param req a request
     * @param <T> generic type
     *
     * @author Charles Bryan
     * @version 1.0
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getmRequestQueue().add(req);
    }

    /**
     * Gets the ImageLoader attached to this RequestQueueSingleton Object.
     *
     * @return An ImageLoader Object
     */
    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }
}
