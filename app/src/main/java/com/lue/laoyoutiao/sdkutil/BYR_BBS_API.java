package com.lue.laoyoutiao.sdkutil;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;

import com.lue.laoyoutiao.global.ContextApplication;

/**
 * Created by Lue on 2015/12/23.
 */
public class BYR_BBS_API
{
    private static String TAG = "BYR_BBS_API";

    //北邮人论坛网址
    private static String host = "http://api.byr.cn";

    //返回格式以及appkey
    private static String returnFormat = ".json?";
    private static String appkey = "&appkey=" + "7a282a1a9de5b450";

    //由用户名及密码组成的认证信息
    private static String auth;

    /**主要用到的API**/

    //用户接口
    public static final String STRING_USER = "user";
    public static final String STRING_LOGIN = "login";
    public static final String STRING_LOGOUT = "logout";

    //widget接口
    public static final String STRING_WIDGET = "widget";
    public static final String STRING_TOPTEN = "topten";

    //分区接口
    public static final String STRING_SECTION = "section";

    //收藏夹接口
    public static final String STRING_FAVORITE = "favorite";

    //老邮条在本地的储存目录
    public static final String ROOT_FOLDER = "/LaoYouTiao";
    public final static String LOCAL_FILEPATH = Environment.getExternalStorageDirectory().getPath() + ROOT_FOLDER;
    public static final String MY_INFO_FOLDER = "/my_user_info";
    public static final String MY_FACE_NAME = "/my_face.png";

    //本地 SharedPreferences
    private SharedPreferences My_SharedPreferences;

    public BYR_BBS_API()
    {
        My_SharedPreferences = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
        String username = My_SharedPreferences.getString("username", "guset");
        String password = My_SharedPreferences.getString("password", "");
        setAuth(username, password);
    }


    /**
     * 根据用户名和密码设置认证信息，使用Base64进行编码
     * @param username
     * @param password
     */
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
