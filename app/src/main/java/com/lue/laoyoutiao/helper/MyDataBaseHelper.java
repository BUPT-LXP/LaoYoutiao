package com.lue.laoyoutiao.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.threadpool.ThreadPool;

import java.util.List;
import java.util.concurrent.ExecutorService;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/7/31.
 */
public class MyDataBaseHelper
{
    //数据库相关
    private static final String DB_NAME = "LocalInfos.db";
    public static final String TOP_TEN_TABLE = "TOP_TEN_TABLE";
    private MySQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase database;
    private ContentValues contentValues;

    private static MyDataBaseHelper dataBaseHelper;
    private ExecutorService fixedTaskExecutor;

    private MyDataBaseHelper()
    {
        sqLiteOpenHelper = new MySQLiteOpenHelper(ContextApplication.getAppContext(), DB_NAME, null, 1);
        database = sqLiteOpenHelper.getWritableDatabase();
        contentValues = new ContentValues();
        fixedTaskExecutor = ThreadPool.getFixedTaskExecutor();
    }

    public static MyDataBaseHelper getInstance()
    {
        if(dataBaseHelper == null)
        {
            synchronized (MyDataBaseHelper.class)
            {
                if(dataBaseHelper == null)
                {
                    dataBaseHelper = new MyDataBaseHelper();
                }
            }
        }
        return dataBaseHelper;
    }

    public void SaveTopTen(final String TopTenString)
    {
        fixedTaskExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                contentValues.put("topten", "topten");
                contentValues.put("content", TopTenString);
                database.update(TOP_TEN_TABLE, contentValues, "topten=?", new String[]{"topten"});
            }
        });
    }


    public void QueryTopTen()
    {
        fixedTaskExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                final Cursor cursor = database.query(TOP_TEN_TABLE, null, null, null, null, null, null);
                if(cursor.getCount() > 0)
                {
                    cursor.moveToNext();
                    String topten = cursor.getString(1);
                    List<Article> articleList = new Gson().fromJson(topten, new TypeToken<List<Article>>(){}.getType());
                    EventBus.getDefault().postSticky(new Event.Topten_ArticleList(articleList, true));
                }
                cursor.close();
            }
        });
    }
}
