package com.lue.laoyoutiao.fragment;


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
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.io.File;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_mine, container, false);

        init();

        read_My_Local_Info();

        return view;
    }

    /**
     * 初始化各组件
     */
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

    /**
     * 读取本地存取的登录用户信息
     */
    public void read_My_Local_Info()
    {
        String file_path = BYR_BBS_API.LOCAL_FILEPATH+ BYR_BBS_API.MY_INFO_FOLDER+ BYR_BBS_API.MY_FACE_NAME;
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


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
