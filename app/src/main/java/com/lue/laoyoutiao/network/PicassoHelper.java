package com.lue.laoyoutiao.network;

import android.content.Context;
import android.graphics.Bitmap;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.lue.laoyoutiao.global.ContextApplication;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * Created by Lue on 2016/5/26.
 */
public class PicassoHelper
{
    private volatile static PicassoHelper m_picassoHelper;
    private Context context;

    private PicassoHelper()
    {
        context = ContextApplication.getAppContext();
    }

    /**
     * 使用单例模式
     * @return
     */
    public static PicassoHelper getPicassoHelper()
    {
        if(m_picassoHelper == null)
        {
            synchronized(PicassoHelper.class)
            {
                if(m_picassoHelper == null)
                {
                    m_picassoHelper = new PicassoHelper();
                }
            }
        }
        return m_picassoHelper;
    }

    /**
     * 获取图片
     * @param url 图片链接
     * @return
     */
    public Bitmap getBitmap(String url)
    {
        OkHttpClient client = OkHttpHelper.getM_OkHttpHelper().getOkHttpClient();

        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();

        Bitmap bitmap= null;
        try
        {
            bitmap = picasso.load(url).get();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }
}
