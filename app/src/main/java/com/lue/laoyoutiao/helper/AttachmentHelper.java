package com.lue.laoyoutiao.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lue.laoyoutiao.network.OkHttpHelper;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lue on 2016/5/9.
 */
public class AttachmentHelper
{
    private static final String TAG = "AttachmentHelper";
    private OkHttpHelper okHttpHelper;

    public AttachmentHelper()
    {
        this.okHttpHelper = OkHttpHelper.getM_OkHttpHelper();
    }

    public Bitmap get_Attachment_Image(final String url)
    {
        Bitmap bitmap = null;
        try
        {
            Response response = okHttpHelper.getExecute(url);
            //将 Response 转换成输入流
            InputStream inputStream = response.body().byteStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return bitmap;
    }
}
