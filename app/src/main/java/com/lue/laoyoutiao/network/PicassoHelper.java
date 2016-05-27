package com.lue.laoyoutiao.network;

import android.content.Context;
import android.graphics.Bitmap;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.lue.laoyoutiao.global.ContextApplication;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;

import okhttp3.OkHttpClient;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Lue on 2016/5/26.
 */
public class PicassoHelper
{
    private volatile static PicassoHelper m_picassoHelper;
    private Picasso picasso;

    private PicassoHelper()
    {
        Context context = ContextApplication.getAppContext();
        OkHttpClient client = OkHttpHelper.getM_OkHttpHelper().getOkHttpClient();
        OkHttp3Downloader downloader = new OkHttp3Downloader(client);
        picasso = new Picasso.Builder(context).downloader(downloader).build();
    }

    /**
     * 使用单例模式
     *
     * @return
     */
    public static PicassoHelper getPicassoHelper()
    {
        if (m_picassoHelper == null)
        {
            synchronized (PicassoHelper.class)
            {
                if (m_picassoHelper == null)
                {
                    m_picassoHelper = new PicassoHelper();
                }
            }
        }
        return m_picassoHelper;
    }

    /**
     * 获取图片
     * @param url  图片链接
     * @param zoom 缩小的比例
     * @return 图片
     */
    public Bitmap getBitmap(String url, int zoom)
    {
        if(url == null)
        {
            return null;
        }

        Bitmap bitmap_large = null;
        try
        {
            bitmap_large = picasso.load(url).memoryPolicy(NO_CACHE, NO_STORE).get();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (bitmap_large != null)
        {
            if (zoom > 1)
            {
                Bitmap bitmap_small = Bitmap.createScaledBitmap(bitmap_large,
                        bitmap_large.getWidth() / zoom, bitmap_large.getHeight() / zoom, true);
                if (!bitmap_large.isRecycled())
                    bitmap_large.recycle();

                return bitmap_small;
            }
            else
            {
                return bitmap_large;
            }
        }
        return null;
    }


    /**
     * 加载图片
     * @param url 链接
     * @return 图片
     */
    public RequestCreator loadurl(String url)
    {
        return picasso.load(url);
    }
}
