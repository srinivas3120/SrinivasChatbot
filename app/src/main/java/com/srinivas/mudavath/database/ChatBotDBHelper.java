package com.srinivas.mudavath.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mudavath Srinivas on 02-03-2016.
 */

public class ChatBotDBHelper implements DBHelper {
    @Override
    public int getDBVersion() {
        return 1;
    }

    @Override
    public String getDBName() {
        return "srinivasChatBot";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createMessageTable(db);
    }

    private void createMessageTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MessageTable.TABLE_NAME + " ("
                        +MessageTable._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        +MessageTable.MESSAGE_ID+ " TEXT UNIQUE, "
                        +MessageTable.CREATED_AT+ " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        +MessageTable.MESSAGE+ " TEXT, "
                        +MessageTable.MESSAGE_TYPE+ " TEXT, " // s-self  r-receive
                        +MessageTable.MESSAGE_STATUS + " INTEGER DEFAULT 0 )" // 0-pending 1-sent
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
