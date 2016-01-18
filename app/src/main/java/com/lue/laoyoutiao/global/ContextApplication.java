package com.lue.laoyoutiao.global;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

/**
 * Created by Lue on 2016/1/9.
 */
public class ContextApplication extends Application
{
    private static Context context;


    private final static String local_filepath = Environment.getExternalStorageDirectory().getPath() + "/LaoYouTiao";

//    private final static String local_filepath = Environment.getExternalStorageDirectory().getPath() ;

    public void onCreate()
    {
        super.onCreate();
        ContextApplication.context = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return ContextApplication.context;
    }

    public static String getLocal_filepath()
    {
        return local_filepath;
    }
}
