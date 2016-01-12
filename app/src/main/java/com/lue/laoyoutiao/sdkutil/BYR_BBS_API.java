package com.lue.laoyoutiao.sdkutil;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.lue.laoyoutiao.global.ContextApplication;

/**
 * Created by Lue on 2015/12/23.
 */
public class BYR_BBS_API
{
    private static String TAG = "BYR_BBS_API";
    private static String host = "http://api.byr.cn";


    private static String returnFormat = ".json?";
    private static String appkey = "&appkey=" + "7a282a1a9de5b450";

    private static String auth;

    private SharedPreferences My_SharedPreferences;

    public BYR_BBS_API()
    {
        My_SharedPreferences = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
        String username = My_SharedPreferences.getString("username", "guset");
        String password = My_SharedPreferences.getString("password", "");
        setAuth(username, password);
    }


    public void setAuth(String username, String password)
    {
        byte[] encodeauth = (username + ":" + password).getBytes();
        auth = Base64.encodeToString(encodeauth, Base64.NO_WRAP);
    }

    public String getAuth()
    {
        return auth;
    }


    /**
     * 构建URL
     * @param strings
     * @return 构建好的URL
     * Example: param   : widget topten
     *          result  : "/widget/topten"
     *          return  : "http://api.byr.cn/widget/topten.json?&appkey=7a282a1a9de5b450"
     */
    public static String buildUrl(String... strings)
    {
        String result = "";
        for (String s : strings)
        {
            result = result + "/"+ s ;
        }
        return host + result + returnFormat + appkey;
    }


}
