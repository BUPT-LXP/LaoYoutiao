package com.lue.laoyoutiao.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RadioGroup;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.fragment.BoardFragment;
import com.lue.laoyoutiao.fragment.MineFragment;
import com.lue.laoyoutiao.fragment.ToptenFragment;


public class MainActivity extends FragmentActivity
{
    private final String TAG = "MainActivity";
    private ToptenFragment toptenFragment;
    private BoardFragment boardFragment;
    private MineFragment mineFragment;
    private RadioGroup radioGroup;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public static final String APP_KEY = "5773baf6666c1282849bb006db21da1c";
    public static final String CLIENT_SECRET = "9e75be7878e3e488ad4cc09d937d8408";
    public static final String API_HEADER = "http://api.byr.cn";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置默认fragment
        initView();


//        Intent intent = getIntent();
//        String username = intent.getStringExtra("username");
//        String password = intent.getStringExtra("password");

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

