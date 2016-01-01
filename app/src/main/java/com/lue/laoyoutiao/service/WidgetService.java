package com.lue.laoyoutiao.service;

import com.lue.laoyoutiao.network.NetworkException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Lue on 2015/12/24.
 */
public class WidgetService
{
    private DefaultHttpClient httpClient;
    private String host;
    private String returnFormat;
    private String appkey;
    private String auth;
    private String topten_url = "widget/topten";

    public WidgetService(DefaultHttpClient httpClient, String host, String returnFormat, String appkey, String auth)
    {
        this.httpClient = httpClient;
        this.host = host;
        this.returnFormat = returnFormat;
        this.appkey = appkey;
        this.auth = auth;
    }

    /**
     * 获取十大热门话题的信息
     * @return widget元数据
     * @throws JSONException
     * @throws NetworkException
     * @throws IOException
     */

//    public Widget getWidgetTopten() throws JSONException, NetworkException, IOException
//    {
//        String url = host + topten_url + returnFormat + appkey;
//
//    }
}
