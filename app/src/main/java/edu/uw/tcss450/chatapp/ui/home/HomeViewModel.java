package edu.uw.tcss450.chatapp.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private MutableLiveData<List<Notification>> mNotificationList;
    private MutableLiveData<Integer> mListAddResponse;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mNotificationList = new MutableLiveData<>();
        mNotificationList.setValue(new ArrayList<>());
        mListAddResponse = new MutableLiveData<>();
        mListAddResponse.setValue(new Integer(0));
    }

    public void addListCreateResponseObserver(List<Notification> list,
                                @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<Notification>> observer) {
        setList(list).observe(owner, observer);
    }

    public void addMessageObserver(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super Integer> observer) {
        mListAddResponse.observe(owner, observer);
    }

    public void addNotification(String username, String message) {
        List newList = new ArrayList();
        newList.add(new Notification(username, message));
        Log.i("HOMEMODEL", "added notification with message: " + message);
        mNotificationList.getValue().add(0, new Notification(username, message));
        Log.i("HOMEMODEL", "recycler list size: " + mNotificationList.getValue().size());
        mListAddResponse.setValue(new Integer(1));
    }

    private MutableLiveData<List<Notification>> setList(List<Notification> list) {
        List<Notification> listCopy1 = new ArrayList<>(list);
        mNotificationList.setValue(listCopy1);
        return mNotificationList;
    }

    public List<Notification> getList() {
        return mNotificationList.getValue();
    }
}
