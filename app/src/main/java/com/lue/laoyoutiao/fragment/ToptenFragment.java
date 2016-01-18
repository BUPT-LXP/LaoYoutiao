package com.lue.laoyoutiao.fragment;

//import android.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.WidgetHelper;
import com.lue.laoyoutiao.metadata.Article;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2015/12/30.
 */
public class ToptenFragment extends Fragment
{
    private View view;
    private ListView listview_topten;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_topten, container, false);

        listview_topten = (ListView)view.findViewById(R.id.topten_list);

        getTopten();

        //注册EventBus
        EventBus.getDefault().register(this);

        return view;
    }

    public void setToptenList(ArrayList<String> titles)
    {
        listview_topten = (ListView)view.findViewById(R.id.topten_list);

        final ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(ContextApplication.getAppContext(),
                R.layout.article_list_item, titles);

        listview_topten.setAdapter(arrayAdapter);
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
     * 相应 WidgetHelper 发布的当天十大热门话题
     * @param topten_article_list
     */
    public void onEventMainThread(Event.Topten_ArticleList topten_article_list)
    {
        ArrayList<String> titles = new ArrayList<String>();

        for(Article article : topten_article_list.getTopten_list())
        {
            titles.add(article.getTitle());
        }

        setToptenList(titles);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
