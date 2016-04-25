package com.lue.laoyoutiao.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/4/19.
 */
public class BoardHelper
{
    private static final String TAG = "BoardHelper";
    private OkHttpHelper okHttpHelper;

    public BoardHelper()
    {
        okHttpHelper = OkHttpHelper.getM_OkHttpHelper();
    }


    public void getSpecifiedBoard(String board_name, final int page)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        final String url = BYR_BBS_API.buildGETUrl(params, BYR_BBS_API.STRING_BOARD, board_name);
//        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_BOARD, board_name) + "&page=" + page;
        new Thread()
        {
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(response_result);
                    response_result = jsonObject.getString("article");
                    List<Article> articles = new Gson().fromJson(response_result, new TypeToken<List<Article>>() {}.getType());
                    EventBus.getDefault().post(new Event.Specified_Board_Articles(articles));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
