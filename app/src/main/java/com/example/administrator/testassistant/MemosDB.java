package com.example.administrator.testassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/4/12.
 * 记事本数据库类
 */
public class MemosDB extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "memos";
    public static final String CONTENT = "content";
    public static final String ID = "_id";
    public static final String TIME = "time";
    public static final String ALARM_ID="alarm_id";

    //新建数据库
    public MemosDB(Context context){
        super(context,"test2",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建表
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CONTENT+ " TEXT NOT NULL,"
                + TIME + " TEXT NOT NULL,"
                +ALARM_ID+" INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
