package com.apps.my.liener;

/**
 * Created by rahul on 10/10/16.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper dbHelper;
    public static final String DATABASE_NAME = "Appdata";
    public static String COLUMN_ID = "id";
    public static String COLUMN_DATA = "data";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    private HashMap hp;
    private static final String TAG = DBHelper.class.getSimpleName();

    DbListener hl, bl;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.hl = null;
        this.bl = null;
    }

    public static DBHelper init(Context context){
        if(dbHelper==null){
            dbHelper=new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table history" +
                        "(id integer primary key, data text)"
        );


        db.execSQL(
                "create table bookmarks" +
                        "(id integer primary key, data text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        onCreate(db);
    }

    public void deleteAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL(
                "create table history" +
                        "(id integer primary key, title text,url text, timestamp text)"
        );
    }

    public void deleteAllBookmarks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        db.execSQL(
                "create table bookmarks" +
                        "(id integer primary key, title text,url text, timestamp text)"
        );
    }

    public long insertPage(boolean isHistory, Page page) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATA, page.toString());
        Log.d(TAG, "insertPage() called with: db "  + "isHistory = [" + isHistory + "]");
        if (isHistory) {
            long page_data = db.insert("history", null, contentValues);
            Log.d(TAG, "insertPage() called with: isHistory = [" + isHistory + "], page = [" + page + "]");
            if(hl!=null){
                Log.d(TAG, "inside hl insertPage() called with: isHistory = [" + isHistory + "], title = [");
                hl.onDataChanged();
            }
            return page_data;
        } else {
            long page_data = db.insert("bookmarks", null, contentValues);
            if(bl!=null)
                bl.onDataChanged();
            return page_data;
        }
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from history where id=" + id + "", null);
        return res;
    }

//    public int numberOfRows(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
//        return numRows;
//    }

    public boolean updateContact(boolean isHistory, Integer id, Page page) {
        //Log.d(TAG, "updateContact() called with: " + "isHistory = [" + isHistory + "], id = [" + id + "], title = [" + title + "], url = [" + url + "], timestamp = [" + timestamp + "]");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATA, page.toString());
//        contentValues.put(COLUMN_TITLE, title);
//        contentValues.put(COLUMN_URL, url);
//        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        if (isHistory) {
            db.update("history", contentValues, "id = ? ", new String[]{Integer.toString(id)});
            if(hl!=null){
                Log.d(TAG, "inside hl insertPage() called with: isHistory = [" + isHistory + "], title = [");
                hl.onDataChanged();
            }
        } else {
            db.update("bookmarks", contentValues, "id = ? ", new String[]{Integer.toString(id)});
            if(bl!=null)
                bl.onDataChanged();
        }
        return true;
    }

    public Integer deleteContact(boolean isHistory, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isHistory) {
            return db.delete("history",
                    "id = ? ",
                    new String[]{Integer.toString(id)});
        } else {
            return db.delete("bookmarks",
                    "id = ? ",
                    new String[]{Integer.toString(id)});
        }

    }

    public ArrayList<Page> getAllData(boolean isHistory) {
        ArrayList<Page> array_list = new ArrayList<Page>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if (isHistory) {
            res = db.rawQuery("select * from history", null);
        } else {
            res = db.rawQuery("select * from bookmarks", null);
        }
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(new Page(res.getString(res.getColumnIndex(COLUMN_DATA))));
            res.moveToNext();
        }
        return array_list;
    }

    public void onHistoryChangedListener(DbListener hl){
        this.hl = hl;
//        if(this.hl!=null){
//            Log.d(TAG, "if not null called with: hl = [" + hl + "]");
//        }
//        else {
//            Log.d(TAG, "if  null onHistoryChangedListener() called with: hl = [" + hl + "]");
//        }
//        if(hl!=null){
//            Log.d(TAG, "if not null called with: hl = [" + hl + "]");
//        }
//        else {
//            Log.d(TAG, "if  null onHistoryChangedListener() called with: hl = [" + hl + "]");
//        }
//        Log.d(TAG, "onHistoryChangedListener() called with: hl = [" + hl + "]");
    }

    public void onBookmarkChangedListener(DbListener bl){
        this.bl = bl;
        Log.d(TAG, "onHistoryChangedListener() called with: bl = [" + bl + "]");
    }
}
