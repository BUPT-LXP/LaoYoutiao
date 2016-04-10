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
import com.lue.laoyoutiao.dialog.LoadingDialog;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.FavoriteHelper;
import com.lue.laoyoutiao.helper.SectionHelper;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.metadata.Section;
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

    private LoadingDialog loading_dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = inflater.inflate(R.layout.fragment_board, container, false);

        //初始化显示界面
        initRadioGroup();

        //获取所有收藏的版面
        getFavoriteBoards();

        //注册EventBus
        EventBus.getDefault().register(this);
        return view;
    }

    /**
     * 初始化显示界面
     */
    private void initRadioGroup()
    {
        listview_all_sections = (ExpandableListView)view.findViewById(R.id.expandablelistview_section);
        gridview_favorite_boards = (GridView)view.findViewById(R.id.grdiview_favorite_boards);

        listview_all_sections.setOnGroupExpandListener(this);

        loading_dialog = new LoadingDialog(getActivity());
        loading_dialog.setCanceledOnTouchOutside(false);

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
                        {
                            if(BYR_BBS_API.Is_GetSections_Finished)
                                ShowSections();
                            else
                            {
                                loading_dialog.show();
//                                while (!BYR_BBS_API.Is_GetSections_Finished)
//                                    Log.d(TAG, "Is_GetSections_Finished is false");
//                                loading_dialog.dismiss();
//                                ShowSections();
                            }
                        }

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
     * 获取由 BYR_BBS_API 在获取所有分区信息之后发送的消息，表示取消加载页面的显示
     * @param Get_Sections_Finished
     */
    public void onEventMainThread(final Event.Get_Sections_Finished Get_Sections_Finished)
    {
        if(loading_dialog.isShowing())
            loading_dialog.dismiss();
        ShowSections();
    }


    /*
    *显示分区列表
     */
    public void ShowSections()
    {
//        List<Section> root_sections = BYR_BBS_API.getM_byr_bbs_api().db_root_sections.getSections();

        List<Section> root_sections = BYR_BBS_API.ROOT_SECTIONS;

        final SectionListAdapter adapter= new SectionListAdapter(ContextApplication.getAppContext(), root_sections);

        listview_all_sections.setAdapter(adapter);

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
