package edu.uw.tcss450.chatapp.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class NewMessageCountViewModel extends ViewModel {
    private MutableLiveData<Map<Integer, Integer>> mNewMessageCount;
    private MutableLiveData<Integer> mTotalMessageCount;

    public NewMessageCountViewModel() {
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(new HashMap<>());
        mTotalMessageCount = new MutableLiveData<>();
        mTotalMessageCount.setValue(0);
    }

    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        mTotalMessageCount.observe(owner, observer);
    }



    public void increment(int chatId) {
        if(mNewMessageCount.getValue().containsKey(chatId)) {
            int curCount = mNewMessageCount.getValue().get(chatId);
            mNewMessageCount.getValue().put(chatId, curCount++);
        } else {
            mNewMessageCount.getValue().put(chatId, 1);
        }
        int total = mTotalMessageCount.getValue();
        mTotalMessageCount.setValue(total+1);
    }

    public void reset(int chatId) {
        if (mNewMessageCount.getValue().containsKey(chatId)) {
            int oldValue = mNewMessageCount.getValue().get(chatId);
            if (oldValue > 0) {
                mNewMessageCount.getValue().put(chatId, 0);
                int total = mTotalMessageCount.getValue();
                mTotalMessageCount.setValue(total - oldValue);
            }
        }
    }
}
