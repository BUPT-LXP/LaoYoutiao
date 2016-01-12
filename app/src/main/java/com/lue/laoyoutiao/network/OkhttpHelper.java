package com.lue.laoyoutiao.network;

import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/1/9.
 */
public class OkHttpHelper
{
    private OkHttpClient okHttpClient;

    private static final String TAG = "OkHttpHelper";

    private BYR_BBS_API byr_bbs_api;

    private String response_result;

    public OkHttpHelper()
    {
        okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(2, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(3, TimeUnit.SECONDS);

        byr_bbs_api = new BYR_BBS_API();
    }


    /**
     * 注意，如果调用该方法，该方法在子线程中返回一个String类型的值。在使用EventBus的时候，会造成很多地方的EventBus
     * 订阅者相应该发布者，造成错误
     * 异步获取给定url的反馈, 当获取反馈后执行 new Callback(){}里的内容
     * @param url
     * @throws IOException
     */
    public void getEnqueue(String url) throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + byr_bbs_api.getAuth())
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                response_result = response.body().string();

                //通过EventBus发送事件，
                EventBus.getDefault().post(response_result);
            }
        });
    }



    /**
     * 异步获取给定url的反馈，注意不能在主线程中调用该方法，必须另开线程
     * @param url
     * @return
     * @throws IOException
     */
    public Response getExecute(String url) throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + byr_bbs_api.getAuth())
                .build();
        return okHttpClient.newCall(request).execute();
    }
}

