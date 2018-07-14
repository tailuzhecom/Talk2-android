package com.example.young.talk2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by young on 18-5-24.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_CHATRECORD = "create table chat_record ("
            + "id integer primary key autoincrement, "
            + "from_ text,"
            + "to_ text,"
            + "content text)";

    public static final String CREATE_TALKING = "create table talking ("
            + "friend text , "
            +"msg text, "
            +"seq integer primary key autoincrement)";

    private Context mContext;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHATRECORD);
        db.execSQL(CREATE_TALKING);
        Log.d(TAG, "onCreate: create table success.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists chat_record");
        db.execSQL("drop table if exists talking");
        onCreate(db);
    }
}