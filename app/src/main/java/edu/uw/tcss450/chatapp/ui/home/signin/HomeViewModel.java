package edu.uw.tcss450.chatapp.ui.home.signin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.chatapp.ui.chat.ChatRoom;
import edu.uw.tcss450.chatapp.ui.chat.chat_room.ChatMessage;
import edu.uw.tcss450.chatapp.ui.home.Notification;

public class HomeViewModel extends AndroidViewModel {
    private MutableLiveData<List<Notification>> mNotificationList;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mNotificationList = new MutableLiveData<>();
        mNotificationList.setValue(new ArrayList<>());
    }

    public void addMessageObserver(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<Notification>> observer) {
        mNotificationList.observe(owner, observer);
    }

    public void addNotification(String username, String message) {
        List newList = new ArrayList( mNotificationList.getValue());
        newList.add(new Notification(username, message));
        mNotificationList.setValue(newList);
    }
}
