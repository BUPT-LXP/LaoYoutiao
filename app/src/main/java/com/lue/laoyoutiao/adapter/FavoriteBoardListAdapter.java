package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lue.laoyoutiao.R;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Lue on 2016/1/23.
 */
public class FavoriteBoardListAdapter extends BaseAdapter
{
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;

    public final class ListItemView
    {
        public TextView board_first_char;
        public TextView board_description;
        public TextView board_replynum;
    }

    public FavoriteBoardListAdapter(Context context, List<Map<String, Object>> listItems)
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
        final int selectID = position;

        //自定义视图
        ListItemView listItemView = null;
        if(convertView == null)
        {
            listItemView = new ListItemView();

            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.list_item_favorite_board, null);

            //获取控件对象
            listItemView.board_first_char = (TextView)convertView.findViewById(R.id.textview_board_first_char);
            listItemView.board_description = (TextView)convertView.findViewById(R.id.textview_borad_description);
            listItemView.board_replynum = (TextView)convertView.findViewById(R.id.textview_board_replynum);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        }
        else
        {
            listItemView = (ListItemView)convertView.getTag();
        }

        String description = (String)listItems.get(position).get("description");

        listItemView.board_description.setText(description);

        description = description.substring(0, 1);

        if(position > 0)
        {

            listItemView.board_first_char.setText(description);

            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            int mColor = Color.rgb(r, g, b);                    // 随机生成颜色

            GradientDrawable drawable = (GradientDrawable) listItemView.board_first_char.getBackground();
            drawable.setColor(mColor);


            int threads_today_count = (int) listItems.get(position).get("threads_today_count");
            String string_threads_today_count = "今日有" + threads_today_count + "个新帖";

            listItemView.board_replynum.setText(string_threads_today_count);
            listItemView.board_replynum.setVisibility(View.VISIBLE);
        }
        else
        {
            int mColor = Color.rgb(169, 169, 169);
            GradientDrawable drawable = (GradientDrawable) listItemView.board_first_char.getBackground();
            drawable.setColor(mColor);

            listItemView.board_replynum.setVisibility(View.GONE);
            listItemView.board_description.setText("添加收藏版面");
            listItemView.board_first_char.setText("＋");
        }

        return convertView;
    }
}
