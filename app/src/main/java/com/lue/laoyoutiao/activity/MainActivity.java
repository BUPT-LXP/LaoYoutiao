package com.lue.laoyoutiao.activity;

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
import com.lue.laoyoutiao.helper.FavoriteHelper;


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

        //获取所有收藏的版面
        FavoriteHelper favoriteHelper = new FavoriteHelper();
        favoriteHelper.getFavoriteBoards();
    }

    //以下两个方法测试改变状态栏的颜色，发现如果不要自定义状态栏的话是正常的，但是如果自定义状态栏了之后就会有问题
//    public static void initSystemBar(Activity activity)
//    {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//        {
//            setTranslucentStatus(activity, true);
//        }
//        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//        tintManager.setStatusBarTintEnabled(true);
//        // 使用颜色资源
//        tintManager.setStatusBarTintResource(R.color.my_default_theme_color);
//
//    }
//
//    @TargetApi(19)
//    private static void setTranslucentStatus(Activity activity, boolean on)
//    {
//        Window win = activity.getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//
//        if (on)
//        {
//            winParams.flags |= bits;
//        }
//        else
//        {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }

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
     *
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
                if (toptenFragment != null)
                {
                    fragmentTransaction.show(toptenFragment);
                } else
                {
                    toptenFragment = new ToptenFragment();
                    fragmentTransaction.add(R.id.main_content, toptenFragment);
                }
                break;
            case 1:
                if (boardFragment != null)
                {
                    fragmentTransaction.show(boardFragment);

                } else
                {
                    boardFragment = new BoardFragment();
                    fragmentTransaction.add(R.id.main_content, boardFragment);
                }

                break;
            case 2:
                if (mineFragment != null)
                {
                    fragmentTransaction.show(mineFragment);
                } else
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
     *
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
        moveTaskToBack(true);//true对任何Activity都适用
    }

}



