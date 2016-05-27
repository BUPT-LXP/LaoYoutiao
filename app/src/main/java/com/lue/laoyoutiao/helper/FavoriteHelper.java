package com.lue.laoyoutiao.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.threadpool.ThreadPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import de.greenrobot.event.EventBus;
import okhttp3.Response;

/**
 * Created by Lue on 2016/1/23.
 */
public class FavoriteHelper
{
    private OkHttpHelper okHttpHelper;

    private List<Board> boards;

    private static final String TAG = "FavoriteHelper";
    private static ExecutorService singleTaskExecutor;

    public FavoriteHelper()
    {
        okHttpHelper = OkHttpHelper.getM_OkHttpHelper();
        singleTaskExecutor = ThreadPool.getSingleTaskExecutor();
    }

    /**
     * 获取收藏版面
     */
    public void getFavoriteBoards()
    {
        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_FAVORITE, "0");

        singleTaskExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(response_result);
                    response_result = jsonObject.getString("board");
                    boards = new Gson().fromJson(response_result, new TypeToken<List<Board>>(){}.getType());

                    for(Board board : boards)
                    {
                        board.setIs_favorite(true);
                        BYR_BBS_API.Favorite_Boards.put(board.getDescription(), board);
                        BYR_BBS_API.All_Boards.put(board.getDescription(), board);
                    }

                    EventBus.getDefault().post(new Event.My_Favorite_Boards(boards));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 添加/删除 收藏版面
     * @param url 链接
     * @param params_map 包含了两个参数 ： name 要删除的版面或自定义目录，版面为版面name，如Flash；dir 是否为自定义目录 0不是，1是
     * @param is_favorite  若为true，即表明已经被收藏，本次点击是要取消收藏该版面;若为false，即表明未被收藏，本次点击是要收藏该版面
     */
    public void postFavorite(final String url, final HashMap<String, String> params_map , final boolean is_favorite)
    {
        singleTaskExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.postExecute(url, params_map);
                    SharedPreferences sp = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
                    String description = sp.getString(params_map.get("name"), "null");

                    if(response.isSuccessful())
                    {

                        if(!description.equals("null"))
                        {
                            Board board = BYR_BBS_API.All_Boards.get(description);
                            board.setIs_favorite(!is_favorite);
                            BYR_BBS_API.All_Boards.put(description, board);
                            //将该版面从Favorite_Boards 中删除 或 添加进去
                            if(is_favorite)
                            {
                                BYR_BBS_API.Favorite_Boards.remove(description);
                            }
                            else
                            {
                                BYR_BBS_API.Favorite_Boards.put(description, board);
                            }
                        }
                    }

                    EventBus.getDefault().post(new Event.Post_Favorite_Finished(description, is_favorite));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

    }
}
