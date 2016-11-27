package com.mohawk.osama.capstone;

import android.app.Application;

/**
 * Created by Osama on 11/24/2016.
 */
public class MyApplication extends Application {
    private String userID;
    private static MyApplication singleInstance = null;

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public static MyApplication getInstance()
    {
        return singleInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleInstance = this;
    }
}
