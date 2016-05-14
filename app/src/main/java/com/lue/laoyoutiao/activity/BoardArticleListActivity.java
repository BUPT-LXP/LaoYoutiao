package com.lue.laoyoutiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.adapter.BoardCommonArticleListAdapter;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.ArticleHelper;
import com.lue.laoyoutiao.helper.BoardHelper;
import com.lue.laoyoutiao.helper.FavoriteHelper;
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

public class BoardArticleListActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, AdapterView.OnItemClickListener
{
    private BGARefreshLayout mBGARefreshLayout;
    private ExpandableListView mEpListLiew_Top_Articles;
    private ListView mListView_Articles;
    private static Context mContext;
    private Menu menu;

    private boolean is_favorite = false;
    private int page_number = 1;  //当前页码
    public BoardHelper boardHelper = null;
    private String board_description;
    private String board_name;
    public List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>(); //普通文章相对应Adapter的数据
    //保存当前显示的所有文章信息
    private List<Article> articleList = new ArrayList<>();

    public BoardCommonArticleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_article_list);

        mContext = ContextApplication.getAppContext();

        Intent intent = getIntent();
        board_description = intent.getStringExtra("Board_Description");
        board_name = BYR_BBS_API.All_Boards.get(board_description).getName();
        is_favorite = intent.getBooleanExtra("Is_Favorite", false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(board_description);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //初始化视图，并为 BGARefreshLayout 配置若干属性
        initView();

        //获取指定版面的文章目录
        boardHelper = new BoardHelper();
        boardHelper.getSpecifiedBoard(BYR_BBS_API.All_Boards.get(board_description).getName(), page_number);

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

    /**
     * 添加ActionBar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_board_article_list, menu);
        if(is_favorite)
            menu.findItem(R.id.action_favorite).setIcon(android.R.drawable.btn_star_big_on);
        else
            menu.findItem(R.id.action_favorite).setIcon(android.R.drawable.btn_star_big_off);
        this.menu = menu;
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

        mListView_Articles.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Article article = articleList.get(position);
        ArticleHelper helper = new ArticleHelper();
        helper.getThreadsInfo(board_name, article.getId(), 1);

        Intent intent = new Intent(this, ReadArticleActivity.class);
        intent.putExtra("board_name", board_name);
        intent.putExtra("article_id", article.getId());
        startActivity(intent);
    }

    /**
     * 响应BoardHelper发出的指定版面下的文章列表
     * @param articles 文章列表，默认的数量为30
     */

    public void onEventMainThread(final Event.Specified_Board_Articles articles)
    {
        List<Article> top_Articles = new ArrayList<>();
        List<Article> common_Articles = new ArrayList<>();

        for(Article article : articles.getArticles())
        {
            if(article.is_top())
                top_Articles.add(article);
            else
                common_Articles.add(article);
        }

        if(page_number ==1)
        {
            listItems.clear();
            articleList.clear();
        }

        for(Article article : common_Articles)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", article.getTitle());
            map.put("post_time", article.getPost_time());
            map.put("last_reply_time", article.getLast_reply_time());
            map.put("reply_count", article.getReply_count());
            listItems.add(map);


            articleList.add(article);
        }

        if(page_number ==1)
        {
            if(adapter == null)
            {
                adapter = new BoardCommonArticleListAdapter(ContextApplication.getAppContext(), listItems);
                mListView_Articles.setAdapter(adapter);
            }
            else
            {
                adapter.notifyDataSetChanged();
            }

        }
        else
        {
            adapter.notifyDataSetChanged();
        }

        if(mBGARefreshLayout.getCurrentRefreshStatus() == BGARefreshLayout.RefreshStatus.REFRESHING)
            mBGARefreshLayout.endRefreshing();
        if(mBGARefreshLayout.isLoadingMore())
            mBGARefreshLayout.endLoadingMore();

    }


    /**
     * 为Action Button 添加响应事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //当点击不同的menu item 是执行不同的操作
        switch (id) {
            case R.id.action_favorite:
                Post_Favorite(item);
                break;
            case R.id.action_search:
//                openSettings();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Post_Favorite(MenuItem item)
    {
        String url = null;
        if(is_favorite)
        {
            url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_FAVORITE, BYR_BBS_API.STRING_FAVORITE_DELETE, BYR_BBS_API.STRING_FAVORITE_TOP_LEVEL);
//            Toast.makeText(this, "取消收藏版面", Toast.LENGTH_SHORT).show();
            item.setIcon(android.R.drawable.btn_star_big_off);
        }
        else
        {
            url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_FAVORITE, BYR_BBS_API.STRING_FAVORITE_ADD, BYR_BBS_API.STRING_FAVORITE_TOP_LEVEL);
//            Toast.makeText(this, "收藏版面成功", Toast.LENGTH_SHORT).show();
            item.setIcon(android.R.drawable.btn_star_big_on);
        }
        String name = BYR_BBS_API.All_Boards.get(board_description).getName();
        HashMap<String, String> params_map = new HashMap<>();
        params_map.put("name", name);
        params_map.put("dir", "0");
        new FavoriteHelper().postFavorite(url, params_map, is_favorite);
        is_favorite = !is_favorite;

    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        if(BYR_BBS_API.isNetWorkAvailable())
        {
            // 如果网络可用，则加载网络数据
            page_number = 1 ;
            boardHelper.getSpecifiedBoard(board_name, page_number);
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
        if(BYR_BBS_API.isNetWorkAvailable())
        {
            // 如果网络可用，则加载网络数据
            page_number ++ ;
            boardHelper.getSpecifiedBoard(board_name, page_number);
            return true;
        }
        else
        {
            // 网络不可用，结束下拉刷新
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            mBGARefreshLayout.endLoadingMore();
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
