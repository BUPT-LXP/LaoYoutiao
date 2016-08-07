package com.lue.laoyoutiao.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.activity.BoardArticleListActivity;
import com.lue.laoyoutiao.adapter.FavoriteBoardListAdapter;
import com.lue.laoyoutiao.adapter.SectionListAdapter;
import com.lue.laoyoutiao.dialog.LoadingDialog;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.FavoriteHelper;
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
public class BoardFragment extends Fragment implements ExpandableListView.OnGroupExpandListener,
        SectionListAdapter.OnChildViewClickListener, SectionListAdapter.OnChildFavoriteImageClickListener
{
    private static final String TAG = "BoardFragment";

    private View view;
    private ExpandableListView listview_all_sections;
    private GridView gridview_favorite_boards;
    private RadioGroup viewGroup;
    private boolean is_sectionlist_showed = false ;
    private boolean is_in_background = false;

//    //显示正在加载分区的对话框
    private LoadingDialog loading_dialog;

    //收藏分区列表的数据源
    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    private FavoriteBoardListAdapter favoriteBoardListAdapter = null;
    private SectionListAdapter sectionListAdapter = null;

    //收藏版面列表是否正确展示
    private boolean isFavoriteShown = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //创建或者填充Fragment的UI，并且返回它。如果这个Fragment没有UI， 返回null
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_board, null);

        //初始化显示界面
        initRadioGroup();

        Map<String, Object> map = new HashMap<>();
        map.put("description", "+");
        map.put("threads_today_count", "添加收藏版面");
        listItems.add(map);

        //注册EventBus
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
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

        loading_dialog = new LoadingDialog(getActivity(), "正在获取内容，请稍侯...");
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
     * 获取由 BYR_BBS_API 在获取所有分区信息之后发送的消息，表示取消加载页面的显示
     * @param Get_Sections_Finished
     */
    public void onEventMainThread(final Event.Get_Sections_Finished Get_Sections_Finished)
    {
        if(loading_dialog.isShowing())
            loading_dialog.dismiss();
        ShowSections();
    }


    /**
     * 获取由 BYR_BBS_API 在获取所有收藏版面之后发送的消息，然后展示收藏版面列表
     * @param favorite_boards
     */
    public void onEventMainThread(final Event.My_Favorite_Boards favorite_boards)
    {
        Show_Favorites();
    }


    /*
    *显示分区列表
     */
    public void ShowSections()
    {

        List<Section> root_sections = BYR_BBS_API.ROOT_SECTIONS;

        sectionListAdapter = new SectionListAdapter(ContextApplication.getAppContext(), root_sections);

        listview_all_sections.setAdapter(sectionListAdapter);

        sectionListAdapter.setOnChildViewClickListener(this);
        sectionListAdapter.setChildFavoriteImageClickListener(this);

        listview_all_sections.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                onClickPosition(groupPosition, childPosition, -1);
                return false;
            }
        });

        is_sectionlist_showed = true;
    }

    /**
     * 实现 OnChildViewClickListener 中的 onClickPosition 方法
     * @param parentPosition 根分区位置
     * @param groupPosition 子分区或子版面位置
     * @param childPosition 子子版面位置
     */
    @Override
    public void onClickPosition(int parentPosition, int groupPosition, int childPosition)
    {
        String board_description;
        // childPosition == -1 表示点击的是根分区下的版面
        if(childPosition == -1)
        {
            int sub_section_size = BYR_BBS_API.ROOT_SECTIONS.get(parentPosition).getSub_section_size();
            board_description = BYR_BBS_API.ROOT_SECTIONS.get(parentPosition).getBoard_description(groupPosition - sub_section_size);
        }
        else
        {
            String sub_section_name = BYR_BBS_API.ROOT_SECTIONS.get(parentPosition).getSub_section_name(groupPosition);
            board_description = BYR_BBS_API.All_Sections.get(sub_section_name).getBoard_description(childPosition);
        }

        Intent intent = new Intent(this.getActivity(), BoardArticleListActivity.class);
        intent.putExtra("Board_Description", board_description);
        boolean is_favorite = !(BYR_BBS_API.Favorite_Boards.get(board_description) == null);
        intent.putExtra("Is_Favorite", is_favorite);
        startActivity(intent);
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
     * 实现 OnChildFavoriteImageClickListener 接口
     * @param board_description 版面描述，BYR_BBS_API.All_Boards 的 key
     * @param is_favorite  若为true，即表明已经被收藏，本次点击是要取消收藏该版面;若为false，即表明未被收藏，本次点击是要收藏该版面
     */
    @Override
    public void onClickImagePosition(String board_description, boolean is_favorite)
    {

        String url = null;
        if(is_favorite)
        {
            url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_FAVORITE, BYR_BBS_API.STRING_FAVORITE_DELETE, BYR_BBS_API.STRING_FAVORITE_TOP_LEVEL);
        }
        else
        {
            url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_FAVORITE, BYR_BBS_API.STRING_FAVORITE_ADD, BYR_BBS_API.STRING_FAVORITE_TOP_LEVEL);
        }

        String name = BYR_BBS_API.All_Boards.get(board_description).getName();
        HashMap<String, String> params_map = new HashMap<>();
        params_map.put("name", name);
        params_map.put("dir", "0");
        new FavoriteHelper().postFavorite(url, params_map, is_favorite);
    }

    /**
     * 响应 FavoriteHelper 发送的 添加/删除 收藏版面回馈通知
     * @param post_favorite_finished
     */
    public void onEventMainThread(Event.Post_Favorite_Finished post_favorite_finished)
    {

        boolean is_favorite = post_favorite_finished.getIs_favorite();
        //若为true，即表明已经被收藏，本次点击是要取消收藏该版面;若为false，即表明未被收藏，本次点击是要收藏该版面
        if(is_favorite)
            Toast.makeText(ContextApplication.getAppContext(), R.string.favorite_boards_cancel, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ContextApplication.getAppContext(), R.string.favorite_boards_success, Toast.LENGTH_SHORT).show();

        listItems.clear();
        Map<String, Object> map = new HashMap<>();
        map.put("description", "+");
        map.put("threads_today_count", "添加收藏版面");
        listItems.add(map);
        for (String key : BYR_BBS_API.Favorite_Boards.keySet())
        {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("description", key);
            map1.put("threads_today_count", BYR_BBS_API.Favorite_Boards.get(key).getThreads_today_count());
            listItems.add(map1);
        }

        if(favoriteBoardListAdapter == null)
            favoriteBoardListAdapter = new FavoriteBoardListAdapter(ContextApplication.getAppContext(), listItems);
        else
            favoriteBoardListAdapter.notifyDataSetChanged();
        if(sectionListAdapter != null && is_in_background)
        {
            sectionListAdapter.notifyDataSetChanged();
        }

    }


    /**
     * 展示收藏版面列表
     */
    public void Show_Favorites()
    {
        for (String key : BYR_BBS_API.Favorite_Boards.keySet())
        {
            Map<String, Object> map = new HashMap<>();
            map.put("description", key);
            map.put("threads_today_count", BYR_BBS_API.Favorite_Boards.get(key).getThreads_today_count());
            listItems.add(map);
        }

        if(listItems.size() > 1)
            isFavoriteShown = true;

        favoriteBoardListAdapter = new FavoriteBoardListAdapter(ContextApplication.getAppContext(), listItems);

        gridview_favorite_boards.setAdapter(favoriteBoardListAdapter);

        gridview_favorite_boards.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position > 0)
                {
                    String description = (String) listItems.get(position).get("description");
                    Intent intent = new Intent(getActivity(), BoardArticleListActivity.class);
                    intent.putExtra("Board_Description", description);
                    boolean is_favorite = !(BYR_BBS_API.Favorite_Boards.get(description) == null);
                    intent.putExtra("Is_Favorite", is_favorite);
                    startActivity(intent);
                }
                else
                {
                    viewGroup.check(R.id.radiobutton_all_sections);
                }
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        is_in_background = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        is_in_background = false;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }

}
