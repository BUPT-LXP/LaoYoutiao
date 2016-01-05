package com.lue.laoyoutiao.sdkutil;


import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Lue on 2015/12/23.
 */
public class BYR_BBS_SDK
{
    private static String host = "http://api.byr.cn";


    private static String returnFormat = ".json?";
    private static String appkey = "&appkey=" + "7a282a1a9de5b450";

    private String auth;

    /**
     *
     * @param username
     * @param password
     *
     */
    public BYR_BBS_SDK(String username, String password)
    {
        byte[] encodeauth = (username + ":" + password).getBytes();
        this.auth = Base64.encodeToString(encodeauth, Base64.NO_WRAP);
    }

    /**
     *
     * @param strings
     * @return 构建好的API
     * Example: param   : widget topten
     *          result  : "/widget/topten"
     *          return  : "http://api.byr.cn/widget/topten.json?&appkey=7a282a1a9de5b450"
     */
    public static String buildAPI(String... strings)
    {
        String result = "";
        for (String s : strings)
        {
            result = result + "/"+ s ;
        }
        return host + result + returnFormat + appkey;
    }

    public void Get(String... strings)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = buildAPI(strings);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + this.auth)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                Log.e(" ", "");
            }

            @Override
            public void onResponse(Response response) throws IOException
            {

            }
        });
    }

}
