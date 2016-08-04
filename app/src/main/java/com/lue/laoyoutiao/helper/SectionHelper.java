package com.lue.laoyoutiao.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.metadata.Section;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.threadpool.ThreadPool;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import de.greenrobot.event.EventBus;
import okhttp3.Response;

/**
 * Created by Lue on 2016/1/22.
 */
public class SectionHelper
{
    private OkHttpHelper okHttpHelper;

    private List<Section> sections;

    private static final String TAG = "SectionHelper";


    public SectionHelper()
    {
        okHttpHelper = OkHttpHelper.getM_OkHttpHelper();
    }

    /**
     * 获取所有根分区
     */
    public void getRootSections()
    {
        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_SECTION);
        ExecutorService singleThreadExecutor = ThreadPool.getSingleTaskExecutor();
        singleThreadExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(response_result);
                    response_result = jsonObject.getString("section");
                    sections = new Gson().fromJson(response_result, new TypeToken<List<Section>>() {}.getType());

                    for(Section section : sections)
                        BYR_BBS_API.ROOT_SECTIONS.add(section);

                    EventBus.getDefault().post(new Event.All_Root_Sections(sections));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });


    }

}
