package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.metadata.Section;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lue on 2016/1/26.
 */
public class SectionListAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private LayoutInflater listContainer;

    private List<Section> sectionlist;
    private List<Board> boardlist;

    public SectionListAdapter(Context context, List<Section> sectionlist, List<Board> boardlist)
    {
        this.context = context;
        this.sectionlist = sectionlist;
        this.boardlist = boardlist;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return sectionlist.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return sectionlist != null? sectionlist.size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.section_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setSection(sectionlist.get(groupPosition));

        return convertView;
    }

    class ViewHolder
    {
        public TextView description;
        public ImageView image_favorite;

        public ViewHolder(View v)
        {
            description = (TextView)v.findViewById(R.id.textview_section_description);
            image_favorite = (ImageView)v.findViewById(R.id.imageview_favorite);
        }

        public void setSection(Section section)
        {
            description.setText(section.getDescription());
            image_favorite.setVisibility(View.INVISIBLE);
        }

        public void setBoard(Board board)
        {
            description.setText(board.getDescription());
            image_favorite.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getChildrenCount(int groupPosition)
    {
        Section section = sectionlist.get(groupPosition);
        int sub_section_size = section.getSub_section_size();
        int boards_size = section.getBoards_size();
        return sub_section_size+boards_size;
    }



    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        Section section = sectionlist.get(groupPosition);
        int sub_section_size = section.getSub_section_size();
        if(childPosition < sub_section_size)
        {
            return BYR_BBS_API.All_Sections.get(sectionlist.get(groupPosition).getSub_section_name(childPosition));
        }
        else
        {
            return BYR_BBS_API.All_Boards.get(sectionlist.get(groupPosition).getBoard_name(childPosition));
        }
    }



    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Section parent_section = sectionlist.get(groupPosition);
        int sub_section_size = parent_section.getSub_section_size();
        if(childPosition < sub_section_size)
        {
            final Section sub_section = (Section)getChild(groupPosition, childPosition);

            List<String> sub_sub_section_names = sub_section.getSub_section_names();
            List<String> sub_sub_board_names = sub_section.getBoards_names();

            List<Section> sub_sub_sections = new ArrayList<>();
            List<Board> sub_sub_boards = new ArrayList<>();
            for(String name : sub_sub_section_names)
            {
                sub_sub_sections.add(BYR_BBS_API.All_Sections.get(name));
            }
            for(String name : sub_sub_board_names)
            {
                sub_sub_boards.add(BYR_BBS_API.All_Boards.get(name));
            }

            final ExpandableListView sub_elview = getExpandableListView();
            final SectionListAdapter adapter = new SectionListAdapter(context, sub_sub_sections,sub_sub_boards);
            sub_elview.setAdapter(adapter);

            return sub_elview;
        }
        else
        {
            final Board board = (Board)getChild(groupPosition, childPosition);

            ViewHolder holder = null;
            if(convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.section_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.setBoard(boardlist.get(groupPosition));

            return convertView;
        }

    }

    /**
     * 动态创建子ExpandableListView
     */
    public ExpandableListView getExpandableListView()
    {
        ExpandableListView mExpandableListView = new ExpandableListView(context);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context
                .getResources().getDimension(
                        R.dimen.parent_expandable_list_height));
        mExpandableListView.setLayoutParams(lp);
        mExpandableListView.setDividerHeight(0);// 取消group项的分割线
        mExpandableListView.setChildDivider(null);// 取消child项的分割线
        mExpandableListView.setGroupIndicator(null);// 取消展开折叠的指示图标
        return mExpandableListView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}
