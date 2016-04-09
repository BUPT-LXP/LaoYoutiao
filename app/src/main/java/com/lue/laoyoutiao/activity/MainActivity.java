package com.lue.laoyoutiao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.fragment.BoardFragment;
import com.lue.laoyoutiao.fragment.MineFragment;
import com.lue.laoyoutiao.fragment.ToptenFragment;
import com.lue.laoyoutiao.helper.SectionHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    private ToptenFragment toptenFragment;
    private BoardFragment boardFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;

    private RadioGroup radioGroup;

    private int checked_id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



//        //获取所有根分区
        SectionHelper sectionHelper = new SectionHelper();
        sectionHelper.getRootSections();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt("checked_id", checked_id);
        Log.v(TAG, "onSaveInstanceState");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.v(TAG, "onStart");
        /*设置默认fragment，注意这里必须在onStart()里，不然会造成Fragment中的onCreateView不执行，不知道是怎么回事
            参考http://stackoverflow.com/questions/17229500/
            oncreateview-in-fragment-is-not-called-immediately-even-after-fragmentmanager#  解决的 */
        initView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        startService(new Intent(this, BYR_BBS_API.class));
        Log.v(TAG, "onResume");
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.v(TAG, "onStop");
    }



    /**
     * 初始化布局
     */
    public void initView()
    {
        fragmentManager = getSupportFragmentManager();

        showFragment(checked_id);

        radioGroup = (RadioGroup) findViewById(R.id.tab_menu);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.topten:
                        showFragment(0);
                        checked_id = 0;
                        break;
                    case R.id.borad:
                        showFragment(1);
                        checked_id = 1;
                        break;
                    case R.id.mine:
                        showFragment(2);
                        checked_id = 2;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显隐藏所有的fragment，再显示本次需要显示的，使用“隐藏-显示”可以保存切换之前该fragment的状态
     * @param id
     */
    public void showFragment(int id)
    {
        // fragmentTransaction 必须是局部变量，若改成全局的话会造成
        // java.lang.IllegalStateException: commit already called 异常
        // 参考：http://blog.csdn.net/knxw0001/article/details/9363411
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id)
        {
            case 0:
                if(toptenFragment != null)
                {
                    fragmentTransaction.show(toptenFragment);
                }
                else
                {
                    toptenFragment = new ToptenFragment();
                    fragmentTransaction.add(R.id.main_content, toptenFragment);
                }
                break;
            case 1:
                if (boardFragment != null)
                {
                    fragmentTransaction.show(boardFragment);

                }
                else
                {
                    boardFragment = new BoardFragment();
                    fragmentTransaction.add(R.id.main_content, boardFragment);
                }

                break;
            case 2:
                if (mineFragment != null)
                {
                    fragmentTransaction.show(mineFragment);
                }
                else
                {
                    mineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.main_content, mineFragment);
                }
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }


    /**
     * 隐藏所有的fragment
     * @param fragmentTransaction
     */
    public void hideFragments(FragmentTransaction fragmentTransaction)
    {
        if (toptenFragment != null)
          fragmentTransaction.hide(toptenFragment);

        if (boardFragment != null)
          fragmentTransaction.hide(boardFragment);

        if (mineFragment != null)
           fragmentTransaction.hide(mineFragment);
    }

    /**
     * 重写按下返回键的响应
     */
    @Override
    public void onBackPressed()
    {
        stopService(new Intent(this, BYR_BBS_API.class));
        moveTaskToBack(true);//true对任何Activity都适用
    }

}



