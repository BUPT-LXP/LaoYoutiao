package com.lue.laoyoutiao.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
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
    private TextView tv_my_userid;
    private TextView tv_my_username;
    private TextView tv_reply_me;
    private TextView tv_my_secret_message;
    private TextView tv_my_favorite;
    private TextView tv_my_settings;

    //为了避免Fragment之间切换时每次都会调用onCreateView方法，导致每次Fragment的布局都重绘，因此设置一个变量保存状态
    private boolean loaded_flag = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mine, null);

        init();

        read_My_Local_Info();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
//        if(!loaded_flag)
//        {
//            loaded_flag = true;
//            return view;
//        }
//        else
//            return null;
        return view;
    }

    /**
     * 初始化各组件
     */
    public void init()
    {
        linearLayout_my_info = (LinearLayout)view.findViewById(R.id.my_info);
        iv_my_face = (ImageView)view.findViewById(R.id.my_face);
        tv_my_userid = (TextView)view.findViewById(R.id.my_userid);
        tv_my_username = (TextView)view.findViewById(R.id.username);
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
//        SharedPreferences sp = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
//        String my_username = sp.getString("username", "guset");
        tv_my_userid.setText(BYR_BBS_API.Me.getId());
        tv_my_userid.setText(BYR_BBS_API.Me.getUser_name());
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ((ViewGroup)view.getParent()).removeView(view);
    }
}
