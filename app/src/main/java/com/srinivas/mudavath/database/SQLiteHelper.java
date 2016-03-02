package com.srinivas.mudavath.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mudavath Srinivas on 02-03-2016.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(){
        super(Env.appContext, Env.dbHelper.getDBName(), null, Env.dbHelper.getDBVersion());
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Env.dbHelper.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Env.dbHelper.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public String getDatabaseName() {
        return Env.dbHelper.getDBName();
    }
}
