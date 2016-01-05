package com.lue.laoyoutiao.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.fragment.BoardFragment;
import com.lue.laoyoutiao.fragment.MineFragment;
import com.lue.laoyoutiao.fragment.ToptenFragment;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


public class MainActivity extends FragmentActivity
{
    private final String TAG = "MainActivity";
    private ToptenFragment toptenFragment;
    private BoardFragment boardFragment;
    private MineFragment mineFragment;
    private RadioGroup radioGroup;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

//    public static final String APP_KEY = "5773baf6666c1282849bb006db21da1c";
    public static final String APP_KEY = "7a282a1a9de5b450";
    public static final String CLIENT_SECRET = "9e75be7878e3e488ad4cc09d937d8408";
    public static final String API_HEADER = "http://api.byr.cn";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置默认fragment
        initView();


        HttpTest();


    }

    public void HttpTest()
    {
        final TextView textView = (TextView)findViewById(R.id.topten_text_test);
        OkHttpClient okHttpClient = new OkHttpClient();


        String username = "guest";
        String password = "";
        byte[] encodepassword = (username + ":" + password).getBytes();

        //传入的参数中flags一定要用Base64.NO_WRAP
        String string = Base64.encodeToString(encodepassword, Base64.NO_WRAP);


        Request request = new Request.Builder()
                .url("http://api.byr.cn/widget/topten.json?appkey=5773baf6666c1282849bb006db21da1c")
                .addHeader("Authorization", "Basic " + string)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {

            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                String string = response.body().string();
                textView.setText(string);
            }
        });

    }


    public void initView()
    {
        fragmentManager = getFragmentManager();

        setFragment(0);

        radioGroup = (RadioGroup) findViewById(R.id.tab_menu);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.topten:
                        setFragment(0);
                        break;
                    case R.id.borad:
                        setFragment(1);
                        break;
                    case R.id.mine:
                        setFragment(2);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void setFragment(int id)
    {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (id)
        {
            case 0:
                if (toptenFragment == null)
                {
                    toptenFragment = new ToptenFragment();
                }
                //使用当前Fragment的布局替代main_content的控件
                fragmentTransaction.replace(R.id.main_content, toptenFragment);
                break;
            case 1:
                if (boardFragment == null)
                {
                    boardFragment = new BoardFragment();
                }
                fragmentTransaction.replace(R.id.main_content, boardFragment);
                break;
            case 2:
                if (mineFragment == null)
                {
                    mineFragment = new MineFragment();
                }
                fragmentTransaction.replace(R.id.main_content, mineFragment);
                break;
        }
        fragmentTransaction.commit();
    }
}

