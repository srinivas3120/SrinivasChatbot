package com.srinivas.mudavath.network;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Mudavath Srinivas on 01-03-2016.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance=null;
    public static final String TAG = MyApplication.class
            .getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    public static MyApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}
