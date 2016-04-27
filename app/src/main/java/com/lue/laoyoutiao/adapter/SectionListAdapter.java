package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    private List<Section> sectionlist;
    private int parent_size = 0;

    private OnChildViewClickListener childViewClickListener;  // 点击子ExpandableListView子项的监听
    private OnChildFavoriteImageClickListener childFavoriteImageClickListener; //点击收藏按钮的监听

    public SectionListAdapter(Context context, List<Section> sectionlist)
    {
        this.context = context;
        this.sectionlist = sectionlist;
        this.parent_size = sectionlist.size();
    }

    /**
     *
     * @param childFavoriteImageClickListener 点击子ExpandableListView子项的监听
     */
    public void setChildFavoriteImageClickListener(OnChildFavoriteImageClickListener childFavoriteImageClickListener)
    {
        this.childFavoriteImageClickListener = childFavoriteImageClickListener;
    }

    /**
     * 列表视图
     */
    class ViewHolder
    {
        public TextView textview_description;
        public ImageView image_favorite;

        public ViewHolder(View v)
        {
            textview_description = (TextView)v.findViewById(R.id.textview_section_description);
            image_favorite = (ImageView)v.findViewById(R.id.imageview_favorite);

            image_favorite.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(childFavoriteImageClickListener != null)
                    {
                        String description = textview_description.getText().toString();
                        //判断该版面是否已经被收藏
                        boolean is_favorite = BYR_BBS_API.All_Boards.get(description).getIs_favorite();
                        if(is_favorite)
                        {
                            image_favorite.setBackgroundResource(R.mipmap.board_favorite_unpressed);
                            childFavoriteImageClickListener.onClickImagePosition(description, true);
                        }
                        else
                        {
                            image_favorite.setBackgroundResource(R.mipmap.board_favorite_pressed);
                            childFavoriteImageClickListener.onClickImagePosition(description, false);
                        }

                    }
                }
            });
        }

        public void setSection(Section section)
        {
            textview_description.setText(section.getDescription());
            image_favorite.setVisibility(View.INVISIBLE);
        }

        public void setBoard(Board board)
        {
            textview_description.setText(board.getDescription());
            if( board.getIs_favorite() )
                image_favorite.setBackgroundResource(R.mipmap.board_favorite_pressed);
            else
                image_favorite.setBackgroundResource(R.mipmap.board_favorite_unpressed);
            image_favorite.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getGroupCount()
    {
        return this.parent_size;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return sectionlist.get(groupPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if(convertView == null || convertView.getTag(R.id.tag_first) == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_section, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag(R.id.tag_first);
        }

        holder.setSection(sectionlist.get(groupPosition));

        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition)
    {
        Section section = sectionlist.get(groupPosition);
        int sub_section_size = section.getSub_section_size();
        int boards_size = section.getBoards_size();
        return sub_section_size + boards_size;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        Section section = sectionlist.get(groupPosition);
        int sub_section_size = section.getSub_section_size();
        if (childPosition < sub_section_size)
        {
            return BYR_BBS_API.All_Sections.get(section.getSub_section_name(childPosition));
        }
        else
        {
//            return BYR_BBS_API.All_Boards.get(section.getBoard_name(childPosition - sub_section_size));
            return BYR_BBS_API.All_Boards.get(section.getBoard_description(childPosition - sub_section_size));
        }

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Section parent_section = sectionlist.get(groupPosition);
        int sub_section_size = parent_section.getSub_section_size();

        if(childPosition < sub_section_size)
        {
            final Section sub_section = (Section)getChild(groupPosition, childPosition);
            ArrayList<Section> sub_section_list = new ArrayList<>();
            sub_section_list.add(sub_section);


            final ExpandableListView sub_elview = getExpandableListView();
            final SectionListAdapter adapter = new SectionListAdapter(context, sub_section_list);
            adapter.setChildFavoriteImageClickListener(childFavoriteImageClickListener);
            sub_elview.setAdapter(adapter);

            /**
             * 点击子ExpandableListView子项时，调用回调接口
             */
            sub_elview.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
            {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupIndex, int childIndex, long id)
                {
                    if(childViewClickListener != null)
                    {
                        childViewClickListener.onClickPosition(groupPosition, childPosition, childIndex);
                    }
                    return false;
                }
            });


            /**
             * 子ExpandableListView展开时，因为group只有一项
             * 所以子ExpandableListView的总高度=（子ExpandableListView的child数量 + 1 ）* 每一项的高度 + 子ExpandableListView的child数量 * 分割线高度
             */
            sub_elview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
            {
                @Override
                public void onGroupExpand(int groupPosition)
                {
                    int itemHeight = (sub_section.getSub_section_size() + sub_section.getBoards_size() + 1)
                                     * (int) context.getResources().getDimension(R.dimen.parent_expandable_list_height);
                    int dividerHeight = (sub_section.getSub_section_size() + sub_section.getBoards_size()) * sub_elview.getDividerHeight();

                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            itemHeight + dividerHeight );
                    sub_elview.setLayoutParams(lp);
                }
            });

            /**
             * 子ExpandableListView关闭时，此时只剩下group这一项，
             *         所以子ExpandableListView的总高度即为一项的高度
             */
            sub_elview.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener()
            {
                @Override
                public void onGroupCollapse(int groupPosition)
                {
                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) context.getResources().getDimension(R.dimen.parent_expandable_list_height));
                    sub_elview.setLayoutParams(lp);
                }
            });

            sub_elview.setPadding((int) context.getResources().getDimension(R.dimen.child_paddingg_left_relative_to_parent),0,0,0);

            return sub_elview;
        }
        else
        {
            final Board board = (Board)getChild(groupPosition, childPosition);

            ViewHolder holder = null;
            if(convertView == null || convertView.getTag(R.id.tag_first) == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_section, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(R.id.tag_first,holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag(R.id.tag_first);
            }

            holder.setBoard(board);

            convertView.setPadding((int) context.getResources().getDimension(R.dimen.child_paddingg_left_relative_to_parent),0,0,0);

            return convertView;
        }

    }



    /**
     * 动态创建子ExpandableListView
     */
    public ExpandableListView getExpandableListView()
    {
        ExpandableListView mExpandableListView = new ExpandableListView(context);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.parent_expandable_list_height));
        mExpandableListView.setLayoutParams(lp);

        mExpandableListView.setDivider(new ColorDrawable(Color.RED));
        mExpandableListView.setDividerHeight(2);// 取消group项的分割线
//        mExpandableListView.setChildDivider(null);// 取消child项的分割线
//        mExpandableListView.setGroupIndicator(null);// 取消展开折叠的指示图标
        return mExpandableListView;
    }


    @Override
    public boolean hasStableIds()
    {
        return false;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    public void setOnChildViewClickListener(OnChildViewClickListener childViewClickListener)
    {
        this.childViewClickListener = childViewClickListener;
    }



    /**
     * 点击子ExpandableListView子项的回调接口
     */
    public interface OnChildViewClickListener
    {
        void onClickPosition(int parentPosition, int groupPosition, int childPosition);
    }

    /**
     *  点击收藏按钮的回调接口
     */
    public interface OnChildFavoriteImageClickListener
    {
        /**
         *
         * @param board_description 版面描述，BYR_BBS_API.All_Boards 的 key
         * @param is_favorite 若为true，即表明已经被收藏，本次点击是要取消收藏该版面;若为false，即表明未被收藏，本次点击是要收藏该版面
         */
        void onClickImagePosition(String board_description, boolean is_favorite);
    }

}
