package com.srinivas.mudavath.database;

import android.content.Context;

/**
 * Created by Mudavath Srinivas on 02-03-2016.
 */
public class Env {
    public static Context appContext;
    public static DBHelper dbHelper;
    public static String logFilePath;
    public static boolean isDebugMode;

    public static void init(Context appContext, DBHelper dbHelper,String logFilePath, boolean isDebugMode) {
        Env.appContext = appContext;
        Env.dbHelper = dbHelper;
        Env.logFilePath = logFilePath;
        Env.isDebugMode = isDebugMode;
    }
}
