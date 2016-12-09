package com.mohawk.osama.capstone;

import android.app.Application;

/**
 * Created by Osama on 11/24/2016.
 * Used to store and access user id across all activities
 */
public class MyApplication extends Application {
    private String userID;
    private static MyApplication singleInstance = null;

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserID() {
        return this.userID;
    }

    /**
     * Sets user id.
     *
     * @param userID the user id
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
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
