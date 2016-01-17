package com.lue.laoyoutiao.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.metadata.User;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.squareup.okhttp.Response;

import java.io.IOException;

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

    public UserHelper()
    {
        okHttpHelper = new OkHttpHelper();
    }

    public void UserLogin() throws IOException
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
                    User user_me = new Gson().fromJson(response_result, new TypeToken<User>() {}.getType());

                    EventBus.getDefault().post(new Event.My_User_Info(user_me));

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
