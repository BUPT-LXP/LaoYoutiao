package com.lue.laoyoutiao.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.metadata.User;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/1/13.
 */
public class UserHelper
{
    private OkHttpHelper okHttpHelper;

    private static final String TAG = "UserHelper";


    public UserHelper()
    {
        okHttpHelper = OkHttpHelper.getM_OkHttpHelper();
    }


    /**
     * 用户登录，使用EventBus返回登录用户的元数据
     */
    public void user_Login()
    {
        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_USER, BYR_BBS_API.STRING_LOGIN);

        new Thread()
        {
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();
                    User user_me = new Gson().fromJson(response_result, new TypeToken<User>()
                    {
                    }.getType());

                    save_UserFace_to_Local(user_me.getFace_url());

                    EventBus.getDefault().post(new Event.My_User_Info(user_me));

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 根据用户头像的url，获取用户的头像bitmap，并将该图片保存到本地
     *
     * @param face_url
     */
    public void save_UserFace_to_Local(final String face_url)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    //获取 Response
//                    OkHttpClient okHttpClient = new OkHttpClient();
//                    Request request = new Request.Builder().url(face_url).build();
//                    Response response = okHttpClient.newCall(request).execute();
                    Response response = okHttpHelper.getExecute(face_url);

                    //将 Response 转换成输入流
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    //创建本地储存文件夹及对应文件
                    String root_dic = BYR_BBS_API.LOCAL_FILEPATH;
                    File dirFile = new File(root_dic + BYR_BBS_API.MY_INFO_FOLDER);
                    if (!dirFile.exists())
                        dirFile.mkdirs();
                    File file = new File(dirFile + BYR_BBS_API.MY_FACE_NAME);

                    //将bitmap保存到本地文件中
                    FileOutputStream fout = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                    fout.flush();
                    fout.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public Bitmap get_UserFace(final String user_id, final String face_url)
    {
        Bitmap user_face = null;
        try
        {
            //获取 Response
//            OkHttpClient okHttpClient = new OkHttpClient();
//            Request request = new Request.Builder().url(face_url).build();
//            Response response = okHttpClient.newCall(request).execute();
            Response response = okHttpHelper.getExecute(face_url);

            //将 Response 转换成输入流
            InputStream inputStream = response.body().byteStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            user_face = BitmapFactory.decodeStream(inputStream, null, options);

//            final int REQUIRED_SIZE = (int) ContextApplication.getAppContext().getResources().getDimension(R.dimen.user_face_scale);
//            int insamplesize = (options.outWidth / REQUIRED_SIZE);
//            if(insamplesize <= 0)
//                insamplesize = 1;

            //为什么先把图片的高度和宽度解析出来之后然后按比例缩放有问题。。。暂时只能按固定比例缩放了。。。
            options.inSampleSize = 2;


            options.inJustDecodeBounds = false;
            user_face = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return user_face;
    }
}
