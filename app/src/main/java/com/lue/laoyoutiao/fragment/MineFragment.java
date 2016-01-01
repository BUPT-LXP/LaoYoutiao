package com.lue.laoyoutiao.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lue.laoyoutiao.R;

/**
 * Created by Lue on 2015/12/30.
 */
public class MineFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }
}
