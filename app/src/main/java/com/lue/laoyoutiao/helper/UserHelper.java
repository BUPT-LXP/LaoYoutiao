package com.lue.laoyoutiao.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.metadata.User;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
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

    private static final String STRING_User = "user";
    private static final String STRING_LOGIN = "login";
    private static final String STRING_LOGOUT = "logout";

    private static final String MY_INFO_LOCAL_PATH = "/my_user_info";
    private static final String MY_FACE_NAME = "/my_face.png";

    public UserHelper()
    {
        okHttpHelper = new OkHttpHelper();
    }

    public static String getMyFaceName()
    {
        return MY_FACE_NAME;
    }

    public static String getMyInfoLocalPath()
    {
        return MY_INFO_LOCAL_PATH;
    }

    /**
     * 用户登录，使用EventBus返回登录用户的元数据
     */
    public void user_Login()
    {
        final String url = BYR_BBS_API.buildUrl(STRING_User, STRING_LOGIN);

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


    public void save_UserFace_to_Local(final String face_url)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(face_url).build();
                    Response response = okHttpClient.newCall(request).execute();

                    InputStream inputStream = response.body().byteStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    inputStream.close();

                    String root_dic = ContextApplication.getLocal_filepath();

                    File dirFile = new File(root_dic);
                    if (!dirFile.exists())
                        dirFile.mkdirs();

                    File sub_dic = new File(root_dic+MY_INFO_LOCAL_PATH);
                    if(!sub_dic.exists())
                        sub_dic.mkdirs();

//                    File file = new File(path + MY_INFO_LOCAL_PATH+ MY_FACE_NAME);
                    File file = new File(sub_dic+ MY_FACE_NAME);

                    FileOutputStream fout = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                    fout.flush();
                    fout.close();

//                    EventBus.getDefault().post(bitmap);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
