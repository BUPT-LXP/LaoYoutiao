package com.lue.laoyoutiao.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lue on 2016/7/31.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper
{
    private String TOP_TEN_TABLE = "TOP_TEN_TABLE";


    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        if(!tabIsExist(TOP_TEN_TABLE))
        {
            String sql = "create table if not exists " + TOP_TEN_TABLE + " (topten text primary key, content text)";
            db.execSQL(sql);

            ContentValues values = new ContentValues();
            values.put("topten", "topten");
            values.put("content", "hahaha");
            db.insert(TOP_TEN_TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS " + TOP_TEN_TABLE;
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean tabIsExist(String tabName){
        boolean result = false;
        if(tabName == null){
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();//此this是继承SQLiteOpenHelper类得到的
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
            cursor.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }
}
