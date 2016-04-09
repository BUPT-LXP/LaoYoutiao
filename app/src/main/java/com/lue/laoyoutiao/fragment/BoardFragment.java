package com.lue.laoyoutiao.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.adapter.FavoriteBoardListAdapter;
import com.lue.laoyoutiao.adapter.SectionListAdapter;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.FavoriteHelper;
import com.lue.laoyoutiao.helper.SectionHelper;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2015/12/30.
 */
public class BoardFragment extends Fragment implements ExpandableListView.OnGroupExpandListener, SectionListAdapter.OnChildViewClickListener
{
    private static final String TAG = "BoardFragment";

    private View view;
    private ExpandableListView listview_all_sections;
    private GridView gridview_favorite_boards;
    private RadioGroup viewGroup;
    private boolean is_sectionlist_showed = false ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_board, container, false);

        initRadioGroup();

        //获取所有根分区
//        getRootSection();

        //获取所有收藏的版面
        getFavoriteBoards();



        //注册EventBus
        EventBus.getDefault().register(this);
        return view;
    }

    private void initRadioGroup()
    {
        listview_all_sections = (ExpandableListView)view.findViewById(R.id.expandablelistview_section);
        gridview_favorite_boards = (GridView)view.findViewById(R.id.grdiview_favorite_boards);



        listview_all_sections.setOnGroupExpandListener(this);

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

                        //显示分区列表
                        if(!is_sectionlist_showed)
                            ShowSections();
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


    /*
    *显示分区列表
     */
    public void ShowSections()
    {
        final SectionListAdapter adapter= new SectionListAdapter(ContextApplication.getAppContext(), BYR_BBS_API.ROOT_SECTIONS);

        listview_all_sections.setAdapter(adapter);

//        ViewGroup.LayoutParams layoutParams = this.listview_all_sections.getLayoutParams();
//        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParams.height = (int) ContextApplication.getAppContext().getResources().getDimension(R.dimen.parent_expandable_list_height);
//        listview_all_sections.setLayoutParams(layoutParams);

//        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                (int) ContextApplication.getAppContext().getResources().getDimension(R.dimen.parent_expandable_list_height));
//        listview_all_sections.setLayoutParams(lp);

        adapter.setOnChildViewClickListener(this);

        is_sectionlist_showed = true;
    }

    @Override
    public void onClickPosition(int parentPosition, int groupPosition, int childPosition)
    {
        Toast.makeText(ContextApplication.getAppContext(), "hehehe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGroupExpand(int groupPosition)
    {
        for(int i=0; i < BYR_BBS_API.ROOT_SECTIONS.size(); i++)
        {
            if(i != groupPosition)
                listview_all_sections.collapseGroup(i);
        }
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
     * 响应 FavoriteHelper 发布的所有根分区信息
     * @param myFavoriteBoards
     */
    public void onEventMainThread(Event.My_Favorite_Boards myFavoriteBoards)
    {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        for(Board board : myFavoriteBoards.getBoards())
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("description", board.getDescription());
            map.put("threads_today_count", board.getThreads_today_count());
            listItems.add(map);
        }

        FavoriteBoardListAdapter adapter = new FavoriteBoardListAdapter(ContextApplication.getAppContext(), listItems);

        gridview_favorite_boards.setAdapter(adapter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
