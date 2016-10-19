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

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Appdata";
    public static String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table history" +
                        "(id integer primary key, title text,url text, timestamp text)"
        );




        db.execSQL(
                "create table bookmarks" +
                        "(id integer primary key, title text,url text, timestamp text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        onCreate(db);
    }

    public long insertContact  (boolean isHistory,String title, String url, String timestamp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_URL,url);
        contentValues.put(COLUMN_TIMESTAMP,timestamp);
        if(isHistory){
            return db.insert("history", null, contentValues);
        }
        else {
            return db.insert("bookmarks", null, contentValues);
        }
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from history where id="+id+"", null );
        return res;
    }

//    public int numberOfRows(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
//        return numRows;
//    }

    public boolean updateContact (boolean isHistory,Integer id, String title, String url, String timestamp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_URL,url);
        contentValues.put(COLUMN_TIMESTAMP,timestamp);
        if(isHistory){
            db.update("history", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        }
        else{
            db.update("bookmarks", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        }
        return true;
    }

    public Integer deleteContact (boolean isHistory,Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(isHistory){
            return db.delete("history",
                    "id = ? ",
                    new String[] { Integer.toString(id) });
        }
        else{
            return db.delete("bookmarks",
                    "id = ? ",
                    new String[] { Integer.toString(id) });
        }

    }

    public ArrayList<String> getAllData(boolean isHistory)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if(isHistory){
            res =  db.rawQuery( "select * from history", null );

        }
        else {
           res =  db.rawQuery( "select * from bookmarks", null );
        }
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(COLUMN_TITLE)));
            res.moveToNext();
        }
        return array_list;
    }
}
