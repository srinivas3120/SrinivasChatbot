package com.srinivas.mudavath.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mudavath Srinivas on 02-03-2016.
 */
public class DatabaseMgr {
    private static String TAG = "DatabaseMgr";

    public static SQLiteDatabase sqLiteDb = null;
    private static DatabaseMgr instance = null;
    private static SQLiteHelper sqLiteHelper=null;

    public synchronized static DatabaseMgr getInstance() throws InstantiationException, IllegalAccessException {
        if (instance == null) {
            instance = new DatabaseMgr();
            instance.init();
        }
        return instance;
    }

    private synchronized static boolean init() throws IllegalAccessException, InstantiationException {
        sqLiteHelper = new SQLiteHelper();
        sqLiteDb = sqLiteHelper.getWritableDatabase();
        sqLiteDb.setPageSize(4 * 1024);
        return true;
    }

    public synchronized static int insertRow(String tableName, ContentValues contentValue) {
        Log.i(TAG, "insertRow(): + tableName [" + tableName + "] values [" + ((contentValue != null) ? contentValue.toString() : "") + "]");
        int retCode = -1;
        try {
            getInstance().sqLiteDb.beginTransaction();

            if (contentValue == null)
                return 0;

            retCode = (int) sqLiteDb.insertWithOnConflict(tableName, null, contentValue, SQLiteDatabase.CONFLICT_REPLACE);

        } catch (Exception e) {
            Log.e(TAG, "insertRow(): Exception [" + e + "] tableName [" + tableName + "] values [" + ((contentValue != null) ? contentValue.toString() : "") + "]");
            e.printStackTrace();
        } finally {
            if (sqLiteDb != null) {
                sqLiteDb.setTransactionSuccessful();
                sqLiteDb.endTransaction();
            }
        }
        return retCode;
    }


    public synchronized static int updateRow(String tableName, ContentValues values,
                                             String whereClause, String[] whereArgs) {
        Log.i(TAG, "updateRow(): tableName [" + tableName + "] values [" + ((values != null) ? values.toString() : "") +
                "] where [" + whereClause + "] where Args [" + ((whereArgs != null) ? whereArgs.toString() : ""));
        int result = -1;
        try {

            getInstance().sqLiteDb.beginTransaction();
            result = getInstance().sqLiteDb.update(tableName, values,
                    whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "updateRow(): Exception [" + e +
                    "] tableName [" + tableName + "] values [" + ((values != null) ? values.toString() : "") +
                    "] where [" + whereClause + "] where Args [" + ((whereArgs != null) ? whereArgs.toString() : ""));
            e.printStackTrace();
        } finally {
            if (sqLiteDb != null) {
                sqLiteDb.setTransactionSuccessful();
                sqLiteDb.endTransaction();
            }
        }
        return result;
    }


    public synchronized  static Cursor selectRows(String tableName,String[] columns, String selection,  String[] selectionArgs){
        Cursor cur = null;
        try {
            getInstance().sqLiteDb.beginTransaction();
                    cur=sqLiteDb.query(tableName, columns, selection, selectionArgs, null, null, null);
        }catch (Exception ex){

        }finally {
            if (sqLiteDb != null) {
                sqLiteDb.setTransactionSuccessful();
                sqLiteDb.endTransaction();
            }
        }
        return cur;
    }


    public synchronized  static Cursor selectRowsRawQuery(String rawQuery){
        Cursor cur = null;
        try {
            getInstance().sqLiteDb.beginTransaction();
            cur=sqLiteDb.rawQuery(rawQuery, null);

        }catch (Exception ex){

        }finally {
            if (sqLiteDb != null) {
                sqLiteDb.setTransactionSuccessful();
                sqLiteDb.endTransaction();
            }
        }
        return cur;
    }

}
