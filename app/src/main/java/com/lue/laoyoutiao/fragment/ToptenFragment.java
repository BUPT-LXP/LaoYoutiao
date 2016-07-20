package com.lue.laoyoutiao.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.activity.ReadArticleActivity;
import com.lue.laoyoutiao.adapter.ToptenArticleListAdapter;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.WidgetHelper;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2015/12/30.
 */
public class ToptenFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate, AdapterView.OnItemClickListener
{
    private View view;
    private BGARefreshLayout mBGARefreshLayout;
    private ListView listview_topten;
    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    private List<Article> articleList = new ArrayList<>();
    private ToptenArticleListAdapter adapter ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_topten, container, false);

        mBGARefreshLayout = (BGARefreshLayout)view.findViewById(R.id.layout_topten_article_list);
        listview_topten = (ListView)view.findViewById(R.id.topten_list);

        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder holder = new BGANormalRefreshViewHolder(ContextApplication.getAppContext(), false);
        mBGARefreshLayout.setRefreshViewHolder(holder);

        listview_topten.setOnItemClickListener(this);

        getTopten();

        //注册EventBus
        EventBus.getDefault().register(this);

        return view;
    }

    /**
     * 获取当天的十大热门话题
     */
    public void getTopten()
    {
        WidgetHelper widgetHelper = new WidgetHelper();

        widgetHelper.getTopten();
    }

    /**
     * 响应 WidgetHelper 发布的当天十大热门话题
     * @param topten_article_list
     */
    public void onEventMainThread(Event.Topten_ArticleList topten_article_list)
    {
        listItems.clear();
        articleList.clear();

        for(Article article : topten_article_list.getTopten_list())
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("board", article.getBoard_name());
            map.put("title", article.getTitle());
            listItems.add(map);

            articleList.add(article);
        }

        if(adapter == null)
        {
            adapter = new ToptenArticleListAdapter(ContextApplication.getAppContext(), listItems);
            listview_topten.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();

        if(mBGARefreshLayout.getCurrentRefreshStatus() == BGARefreshLayout.RefreshStatus.REFRESHING)
            mBGARefreshLayout.endRefreshing();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        if(BYR_BBS_API.isNetWorkAvailable())
        {
            // 如果网络可用，则加载网络数据
            getTopten();
        }
        else
        {
            // 网络不可用，结束下拉刷新
            Toast.makeText(ContextApplication.getAppContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            mBGARefreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Article article = articleList.get(position);

        Intent intent = new Intent(getActivity(), ReadArticleActivity.class);
        intent.putExtra("board_name", article.getBoard_name());
        intent.putExtra("article_id", article.getId());
        startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
