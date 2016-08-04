package com.lue.laoyoutiao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.FavoriteHelper;
import com.lue.laoyoutiao.helper.MyDataBaseHelper;
import com.lue.laoyoutiao.helper.SectionHelper;
import com.lue.laoyoutiao.helper.UserHelper;

public class WelcomeActivity extends Activity
{
    private SharedPreferences My_SharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        new Thread()
        {
            @Override
            public void run()
            {
                My_SharedPreferences = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
                /*读取 My_SharedPreferences 中保存的Login_Success变量
                若成功登陆过，则跳转至MainActivity
                否则，则跳转至LoginActivity  */
                boolean login_success = My_SharedPreferences.getBoolean("Login_Success", false);

                if (login_success)
                {
                    MyDataBaseHelper.getInstance().QueryTopTen();

                    UserHelper userHelper = new UserHelper();
                    userHelper.user_Login();

                    //获取所有根分区
                    SectionHelper sectionHelper = new SectionHelper();
                    sectionHelper.getRootSections();

                    //获取所有收藏的版面
                    FavoriteHelper favoriteHelper = new FavoriteHelper();
                    favoriteHelper.getFavoriteBoards();

                    try
                    {
                        Thread.sleep(2000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

                }
                else
                {
                    try
                    {
                        Thread.sleep(2000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }

                finish();
            }
        }.start();
    }
}