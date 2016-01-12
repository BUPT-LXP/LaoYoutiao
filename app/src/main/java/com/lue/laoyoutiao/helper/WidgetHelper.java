package com.lue.laoyoutiao.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/1/6.
 */
public class WidgetHelper
{
    private OkHttpHelper okHttpHelper;

    private List<Article> articleList;

    private static final String TAG = "WidgetHelper";
    private static final String WIDGET_STRING = "widget";
    private static final String TOPTEN_STRING = "topten";


    public WidgetHelper()
    {
        okHttpHelper = new OkHttpHelper();

        EventBus.getDefault().register(this);
    }


    /**
     * 获取当日十大热门话题内容
     * @throws IOException
     */
    public void getTopten() throws IOException
    {
        final String url = BYR_BBS_API.buildUrl(WIDGET_STRING, TOPTEN_STRING);

        new Thread()
        {
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();

                    int article_index = response_result.indexOf("[");
                    response_result = response_result.substring(article_index , response_result.length() -1);

                    articleList = new Gson().fromJson(response_result, new TypeToken<List<Article>>() {}.getType());

                    EventBus.getDefault().post(articleList);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

//        okHttpHelper.getEnqueue(url);
    }

    public void onEventMainThread(String response_result)
    {
        int article_index = response_result.indexOf("[");
        response_result = response_result.substring(article_index , response_result.length() -1);

        articleList = new Gson().fromJson(response_result, new TypeToken<List<Article>>() {}.getType());

        EventBus.getDefault().post(articleList);
    }


}
