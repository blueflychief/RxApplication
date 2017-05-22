package com.infinite.rxapplication;

import android.app.Application;
import android.util.Log;

/**
 * Created by lsq on 5/22/2017.
 */

public class App extends Application implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "--APP--";

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.i(TAG, "uncaughtException: " + t.getName());
        e.printStackTrace();
    }
}
