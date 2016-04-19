package com.lue.laoyoutiao.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.adapter.BoardCommonArticleListAdapter;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.BoardHelper;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGAMeiTuanRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2015/12/30.
 */

public class BoardArticleListActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate
{
    private BGARefreshLayout mBGARefreshLayout;
    private ExpandableListView mEpListLiew_Top_Articles;
    private ListView mListView_Articles;
    private static Context mContext;

    private List<Article> top_Articles = new ArrayList<>();
    private List<Article> common_Articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_article_list);

        mContext = ContextApplication.getAppContext();

        Intent intent = getIntent();
        String board_description = intent.getStringExtra("Board_Description");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(board_description);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //初始化视图，并为 BGARefreshLayout 配置若干属性
        initView();

        //获取指定版面的文章目录
        BoardHelper boardHelper = new BoardHelper();
        boardHelper.getSpecifiedBoard(BYR_BBS_API.All_Boards.get(board_description).getName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //注册EventBus
        EventBus.getDefault().register(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_board_article_list, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 初始化视图，并为 BGARefreshLayout 配置若干属性
     */
    public void initView()
    {
        mBGARefreshLayout = (BGARefreshLayout)findViewById(R.id.layout_board_article_list);
        mEpListLiew_Top_Articles = (ExpandableListView)findViewById(R.id.eplistview_top_articles);
        mListView_Articles = (ListView)findViewById(R.id.listview_board_articles);

        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGAMeiTuanRefreshViewHolder refreshViewHolder = new BGAMeiTuanRefreshViewHolder(this, true);
        refreshViewHolder.setPullDownImageResource(R.mipmap.bga_refresh_mt_pull_down);
        refreshViewHolder.setChangeToReleaseRefreshAnimResId(R.drawable.bga_refresh_mt_change_to_release_refresh);
        refreshViewHolder.setRefreshingAnimResId(R.drawable.bga_refresh_mt_refreshing);
        // 设置下拉刷新和上拉加载更多的风格
        mBGARefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    /**
     * 响应BoardHelper发出的指定版面下的文章列表
     * @param articles 文章列表，默认的数量为20
     */
    public void onEventMainThread(final Event.Specified_Board_Articles articles)
    {
        for(Article article : articles.getArticles())
        {
            if(article.is_top())
                top_Articles.add(article);
            else
                common_Articles.add(article);
        }

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(Article article : common_Articles)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", article.getTitle());
            map.put("post_time", article.getPost_time());
            map.put("last_reply_time", article.getLast_reply_time());
            map.put("reply_count", article.getReply_count());
            listItems.add(map);
        }

        BoardCommonArticleListAdapter adapter = new BoardCommonArticleListAdapter(ContextApplication.getAppContext(), listItems);

        mListView_Articles.setAdapter(adapter);

    }


    /**
     * 判断当前网络是否可用
     * @return 是否可用
     */
    private static boolean isNetWorkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if( info != null && info.isConnected() )
            {
                //当前网络是连接的
                if(info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        if(isNetWorkAvailable())
        {
            // 如果网络可用，则加载网络数据
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(3000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        else
        {
            // 网络不可用，结束下拉刷新
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            mBGARefreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        if(isNetWorkAvailable())
        {
            // 如果网络可用，则加载网络数据
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(3000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
            return true;
        }
        else
        {
            // 网络不可用，结束下拉刷新
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
//            mBGARefreshLayout.endRefreshing();
            return false;
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
