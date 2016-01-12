package com.lue.laoyoutiao.global;

import android.app.Application;
import android.content.Context;

/**
 * Created by Lue on 2016/1/9.
 */
public class ContextApplication extends Application
{
    private static Context context;

    public void onCreate()
    {
        super.onCreate();
        ContextApplication.context = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return ContextApplication.context;
    }
}
