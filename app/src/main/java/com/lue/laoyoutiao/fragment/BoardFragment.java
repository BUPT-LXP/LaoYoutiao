package com.lue.laoyoutiao.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.adapter.FavoriteBoardListAdapter;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.FavoriteHelper;
import com.lue.laoyoutiao.helper.SectionHelper;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.metadata.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2015/12/30.
 */
public class BoardFragment extends Fragment
{
    private View view;
    private ListView listview_all_sections;
    private GridView gridview_favorite_boards;
    private RadioGroup viewGroup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_board, container, false);

        initRadioGroup();

//        getRootSection();

        getFavoriteBoards();

        //注册EventBus
        EventBus.getDefault().register(this);
        return view;
    }

    private void initRadioGroup()
    {
        listview_all_sections = (ListView)view.findViewById(R.id.listview_all_sections);
        gridview_favorite_boards = (GridView)view.findViewById(R.id.grdiview_favorite_boards);

        viewGroup = (RadioGroup)view.findViewById(R.id.borad_show_model);
        viewGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radiobutton_all_sections:
                        gridview_favorite_boards.setVisibility(View.GONE);
                        listview_all_sections.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radiobutton_favorite_boards:
                        listview_all_sections.setVisibility(View.GONE);
                        gridview_favorite_boards.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 获取所有根分区
     */
    private void getRootSection()
    {
        SectionHelper sectionHelper = new SectionHelper();
        sectionHelper.getRootSections();
    }

    /**
     * 响应 SectionHelper 发布的所有根分区信息
     * @param Root_Sections
     */
    public void onEventMainThread(Event.All_Root_Sections Root_Sections)
    {
        ArrayList<String> section_titles = new ArrayList<String>();
        for(Section section : Root_Sections.getSections())
        {
            section_titles.add(section.getDescription());
        }

        final ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(ContextApplication.getAppContext(),
                R.layout.section_list_item, section_titles);
        listview_all_sections.setAdapter(arrayAdapter);
    }


    public void ShowSections()
    {

    }

    /**
     * 获取所有收藏的版面
     */
    private void getFavoriteBoards()
    {
        FavoriteHelper favoriteHelper = new FavoriteHelper();
        favoriteHelper.getFavoriteBoards();
    }

    /**
     * 响应 SectionHelper 发布的所有根分区信息
     * @param Root_Sections
     */
    public void onEventMainThread(Event.My_Favorite_Boards Root_Sections)
    {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        for(Board board : Root_Sections.getBoards())
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("description", board.getDescription());
            map.put("threads_today_count", board.getThreads_today_count());
            listItems.add(map);
        }

        FavoriteBoardListAdapter adapter = new FavoriteBoardListAdapter(ContextApplication.getAppContext(), listItems);

        gridview_favorite_boards.setAdapter(adapter);
    }

//    public void setSectionTitles(ArrayList<String> titles)
//    {
//        final ArrayAdapter<String> arrayAdapter;
//        arrayAdapter = new ArrayAdapter<String>(ContextApplication.getAppContext(),
//                R.layout.section_list_item, titles);
//        listview_all_sections.setAdapter(arrayAdapter);
//    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
