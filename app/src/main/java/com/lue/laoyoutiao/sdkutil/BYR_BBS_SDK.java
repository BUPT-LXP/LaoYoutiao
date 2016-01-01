package com.lue.laoyoutiao.sdkutil;


import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


/**
 * Created by Lue on 2015/12/23.
 */
public class BYR_BBS_SDK
{
    private DefaultHttpClient httpClient;
    private String host = "http://api.byr.cn/";
    private static String appkey = "&appkey=" + "5773baf6666c1282849bb006db21da1c";
    private String auth;

    private String returnFormat = ".json?";

    private int timeout = 10000;

    public BYR_BBS_SDK(String username, String password)
    {

        httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        HttpConnectionParams.setSoTimeout(params, timeout);
        httpClient = new DefaultHttpClient(params);

        auth = username + ":" + password;
    }


}
