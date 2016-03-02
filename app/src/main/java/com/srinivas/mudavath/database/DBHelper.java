package com.srinivas.mudavath.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mudavath Srinivas on 02-03-2016.
 */
public interface DBHelper {
    public int getDBVersion();
    public String getDBName();
    public void onCreate(SQLiteDatabase db);
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
