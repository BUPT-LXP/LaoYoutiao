package com.lue.laoyoutiao.global;

import android.app.Application;
import android.content.Context;

/**
 * Created by Lue on 2016/1/9.
 */
public class ContextApplication extends Application
{
    private static Context context;


//    private final static String LOCAL_FILEPATH = Environment.getExternalStorageDirectory().getPath() + "/LaoYouTiao";

//    private final static String LOCAL_FILEPATH = Environment.getExternalStorageDirectory().getPath() ;

    public void onCreate()
    {
        super.onCreate();
        ContextApplication.context = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return ContextApplication.context;
    }

//    public static String getLocalFilepath()
//    {
//        return LOCAL_FILEPATH;
//    }
}
