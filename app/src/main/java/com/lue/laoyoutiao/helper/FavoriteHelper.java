package com.lue.laoyoutiao.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/1/23.
 */
public class FavoriteHelper
{
    private OkHttpHelper okHttpHelper;

    private List<Board> boards;

    private static final String TAG = "FavoriteHelper";

    public FavoriteHelper()
    {
        okHttpHelper = new OkHttpHelper();
    }

    public void getFavoriteBoards()
    {
        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_FAVORITE, "0");

        new Thread()
        {
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(response_result);
                    response_result = jsonObject.getString("board");
                    boards = new Gson().fromJson(response_result, new TypeToken<List<Board>>(){}.getType());
                    EventBus.getDefault().post(new Event.My_Favorite_Boards(boards));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
