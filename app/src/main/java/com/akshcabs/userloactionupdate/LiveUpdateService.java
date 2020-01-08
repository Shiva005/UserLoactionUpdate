package com.akshcabs.userloactionupdate;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class LiveUpdateService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LiveUpdateService(String name) {
        super(name);
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
