package com.lue.laoyoutiao.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.fragment.BoardFragment;
import com.lue.laoyoutiao.fragment.MineFragment;
import com.lue.laoyoutiao.fragment.ToptenFragment;


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    private ToptenFragment toptenFragment;
    private BoardFragment boardFragment;
    private MineFragment mineFragment;


    private RadioGroup radioGroup;

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

    @Override
    public void onStart()
    {
        super.onStart();

        /*设置默认fragment，注意这里必须在onStart()里，不然会造成Fragment中的onCreateView不执行，不知道是怎么回事
            参考http://stackoverflow.com/questions/17229500/
            oncreateview-in-fragment-is-not-called-immediately-even-after-fragmentmanager#  解决的 */
        initView();

//        getTopten();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
//        EventBus.getDefault().unregister(this);
    }


    /**
     * 初始化布局
     */
    public void initView()
    {
        fragmentManager = getSupportFragmentManager();

        toptenFragment = new ToptenFragment();
        boardFragment = new BoardFragment();
        mineFragment = new MineFragment();

        showFragment(0);

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
                        break;
                    case R.id.borad:
                        showFragment(1);
                        break;
                    case R.id.mine:
                        showFragment(2);
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
     * 获取当天的十大热门话题
     */
//    public void getTopten()
//    {
//        WidgetHelper widgetHelper = new WidgetHelper();
//
//        try
//        {
//            widgetHelper.getTopten();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 相应 WidgetHelper 发布的当天十大热门话题
//     * @param topten_article_list
//     */
//    public void onEventMainThread(List<Article> topten_article_list)
//    {
//        ArrayList<String> titles = new ArrayList<String>();
//
//        for(Article article : topten_article_list)
//        {
//            titles.add(article.getTitle());
//        }
//
//        toptenFragment.setToptenList(titles);
//    }

}



