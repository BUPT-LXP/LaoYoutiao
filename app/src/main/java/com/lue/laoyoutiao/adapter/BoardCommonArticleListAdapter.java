package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.util.List;
import java.util.Map;

/**
 * Created by Lue on 2016/4/19.
 */
public class BoardCommonArticleListAdapter extends BaseAdapter
{
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;

    class ViewHolder
    {
        public TextView textview_title;
        public TextView textview_post_time;
        public TextView textview_last_reply_time;
        public TextView textview_reply_count;
    }

    public BoardCommonArticleListAdapter(Context context, List<Map<String, Object>> listItems)
    {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.listItems = listItems;
    }


    @Override
    public int getCount()
    {
        return listItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //自定义视图
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();

            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.list_item_board_common_article, null);

            //获取控件对象
            viewHolder.textview_title = (TextView)convertView.findViewById(R.id.textview_common_article_title);
            viewHolder.textview_post_time = (TextView)convertView.findViewById(R.id.textview_common_article_post_time);
            viewHolder.textview_last_reply_time = (TextView)convertView.findViewById(R.id.textview_common_article_last_reply_time);
            viewHolder.textview_reply_count = (TextView)convertView.findViewById(R.id.textview_common_article_reply_count);

            //设置控件集到convertView
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.textview_title.setText((String)listItems.get(position).get("title"));
        int timestamp = (int)listItems.get(position).get("post_time");
        String time = BYR_BBS_API.timeStamptoDate(timestamp, false);
        viewHolder.textview_post_time.setText(time + "发布");
        timestamp = (int)listItems.get(position).get("last_reply_time");
        time = BYR_BBS_API.timeStamptoDate(timestamp, false);
        viewHolder.textview_last_reply_time.setText(time + "更新");
        viewHolder.textview_reply_count.setText((int)(listItems.get(position).get("reply_count")) + "人回复");

        return convertView;
    }
}
