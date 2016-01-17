package com.lue.laoyoutiao.fragment;

//import android.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.eventtype.Event;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_mine, container, false);

        init();

        //注册EventBus
        EventBus.getDefault().register(this);

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


    public void onEventMainThread(Event.My_User_Info user_me)
    {
        if (user_me.getMe().getUser_name() != null)
        {
            tv_my_username.setText(user_me.getMe().getUser_name());
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
