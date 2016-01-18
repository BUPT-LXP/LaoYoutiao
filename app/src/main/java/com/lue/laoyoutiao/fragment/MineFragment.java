package com.lue.laoyoutiao.fragment;

//import android.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.UserHelper;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2015/12/30.
 */
public class MineFragment extends Fragment
{
    private View view;

    private LinearLayout linearLayout_my_info;
    private ImageView iv_my_face;
    private TextView tv_my_username;
    private TextView tv_reply_me;
    private TextView tv_my_secret_message;
    private TextView tv_my_favorite;
    private TextView tv_my_settings;

    private static final String MY_INFO_LOCAL = "/LaoYoutiao";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_mine, container, false);

        init();

        //注册EventBus
//        EventBus.getDefault().register(this);

//        UserHelper userHelper = new UserHelper();
//        userHelper.user_Login();

        read_My_Local_Info();

        return view;
    }

    public void init()
    {
        linearLayout_my_info = (LinearLayout)view.findViewById(R.id.my_info);
        iv_my_face = (ImageView)view.findViewById(R.id.my_face);
        tv_my_username = (TextView)view.findViewById(R.id.my_username);
        tv_reply_me = (TextView)view.findViewById(R.id.textview_reply_me);
        tv_my_secret_message = (TextView)view.findViewById(R.id.textview_my_secret_message);
        tv_my_favorite = (TextView)view.findViewById(R.id.textview_my_favorite);
        tv_my_settings = (TextView)view.findViewById(R.id.textview_my_settings);
    }

    public void read_My_Local_Info()
    {
        String file_path = ContextApplication.getLocal_filepath()+ UserHelper.getMyInfoLocalPath()+ UserHelper.getMyFaceName();
//        String file_path = ContextApplication.getLocal_filepath()+MY_INFO_LOCAL+"/my_face.png";
        File file = new File(file_path);
        if(file.exists())
        {
            Bitmap bitmap = BitmapFactory.decodeFile(file_path);
            iv_my_face.setImageBitmap(bitmap);
        }
        SharedPreferences sp = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
        String my_username = sp.getString("username", "guset");
        tv_my_username.setText(my_username);
    }



//    public void onEventMainThread(Event.My_User_Info user_me)
//    {
//        if (user_me.getMe().getUser_name() != null)
//        {
//            UserHelper userHelper = new UserHelper();
//            userHelper.save_UserFace_to_Local(user_me.getMe().getFace_url());
//
//            tv_my_username.setText(user_me.getMe().getUser_name());
//        }
//    }
//
//    public void onEventMainThread(Bitmap my_face)
//    {
//        iv_my_face.setImageBitmap(my_face);
//    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
