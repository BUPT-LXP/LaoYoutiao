package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.global.ContextApplication;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Lue on 2016/1/9.
 */
public class ToptenArticleListAdapter extends BaseAdapter
{
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;
    private boolean if_change_color[] = new boolean[10];

    //本地 SharedPreferences
    private SharedPreferences My_SharedPreferences;

    public final class ListItemViewHolder
    {
        public TextView article_board;
        public TextView article_title;
    }

    public ToptenArticleListAdapter(Context context, List<Map<String, Object>> listItems)
    {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.listItems = listItems;

        for (int i = 0; i < 10; i++)
        {
            if_change_color[i] = false;
        }

        My_SharedPreferences = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
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
//        final int selectID = position;

        //自定义视图
        ListItemViewHolder listItemViewHolder = null;
        if (convertView == null)
        {
            listItemViewHolder = new ListItemViewHolder();

            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.list_item_topten_article, null);

            //获取控件对象
            listItemViewHolder.article_board = (TextView) convertView.findViewById(R.id.textview_article_board);
            listItemViewHolder.article_title = (TextView) convertView.findViewById(R.id.textview_article_title);

            //设置控件集到convertView
            convertView.setTag(listItemViewHolder);
        } else
        {
            listItemViewHolder = (ListItemViewHolder) convertView.getTag();
        }

        String board = (String) listItems.get(position).get("board");
        board = My_SharedPreferences.getString(board, board);

        board = board.substring(0, 1);
        listItemViewHolder.article_board.setText(board);

        if(!if_change_color[position])
        {
            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            int mColor = Color.rgb(r, g, b);                    // 随机生成颜色

            GradientDrawable drawable = (GradientDrawable) listItemViewHolder.article_board.getBackground();
            drawable.setColor(mColor);

            if_change_color[position] = true;
        }

        listItemViewHolder.article_title.setText((String) listItems.get(position).get("title"));

        return convertView;
    }
}
