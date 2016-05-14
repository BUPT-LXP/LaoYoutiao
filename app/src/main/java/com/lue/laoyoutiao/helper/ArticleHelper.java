package com.lue.laoyoutiao.helper;

import android.graphics.Bitmap;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/4/26.
 */
public class ArticleHelper
{
    private static final String TAG = "ArticleHelper";
    private OkHttpHelper okHttpHelper;
    private UserHelper userHelper;

    public ArticleHelper()
    {
        okHttpHelper = OkHttpHelper.getM_OkHttpHelper();
        userHelper = new UserHelper();
    }


    public void getThreadsInfo(String board_name, int article_id, final int page)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        final String url = BYR_BBS_API.buildGETUrl(params, BYR_BBS_API.STRING_THREADS, board_name, article_id+"");

        new Thread()
        {
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();
                    response_result = response_result.replaceAll("file", "files");
                    JSONObject jsonObject = JSON.parseObject(response_result);
                    response_result = jsonObject.getString("article");
                    List<Article> articles = new Gson().fromJson(response_result, new TypeToken<List<Article>>() {}.getType());

                    List<Bitmap> faces = new ArrayList<Bitmap>();
                    for(Article article : articles)
                    {
                        Bitmap face = userHelper.get_UserFace(article.getUser().getId(), article.getUser().getFace_url());
                        faces.add(face);
                    }
                    int reply_count = jsonObject.getInteger("reply_count");
                    EventBus.getDefault().post(new Event.Read_Articles_Info(articles, faces, reply_count));

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
