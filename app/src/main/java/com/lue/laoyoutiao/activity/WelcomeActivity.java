package com.lue.laoyoutiao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.global.ContextApplication;

public class WelcomeActivity extends Activity
{
    private SharedPreferences My_SharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        new Thread()
        {
            public void run()
            {

                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                My_SharedPreferences = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);

                /*读取 My_SharedPreferences 中保存的Login_Success变量
                若成功登陆过，则跳转至MainActivity
                否则，则跳转至LoginActivity  */
                boolean login_success = My_SharedPreferences.getBoolean("Login_Success", false);
                if(login_success)
                {
                    String username = My_SharedPreferences.getString("username", "guset");
                    String password = My_SharedPreferences.getString("password", "");

                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
                else
                {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
                finish();
            }
        }.start();


    }
}
