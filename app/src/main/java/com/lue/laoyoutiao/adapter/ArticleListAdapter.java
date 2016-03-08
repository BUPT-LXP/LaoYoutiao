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
public class ArticleListAdapter extends BaseAdapter
{
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;

    //本地 SharedPreferences
    private SharedPreferences My_SharedPreferences;

    public final class ListItemView
    {
        public TextView article_board;
        public TextView article_title;
    }

    public ArticleListAdapter(Context context, List<Map<String, Object>> listItems)
    {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.listItems = listItems;

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
        final int selectID = position;

        //自定义视图
        ListItemView listItemView = null;
        if(convertView == null)
        {
            listItemView = new ListItemView();

            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.article_list_item, null);

            //获取控件对象
            listItemView.article_board = (TextView)convertView.findViewById(R.id.textview_article_board);
            listItemView.article_title = (TextView)convertView.findViewById(R.id.textview_article_title);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        }
        else
        {
            listItemView = (ListItemView)convertView.getTag();
        }

        String board = (String)listItems.get(position).get("board");
        board = My_SharedPreferences.getString(board, board);

        board = board.substring(0, 1);
        listItemView.article_board.setText(board);

        Random random = new Random();
        int r = random.nextInt(256);
        int g= random.nextInt(256);
        int b = random.nextInt(256);
        int mColor = Color.rgb(r, g, b);                    // 随机生成颜色

        GradientDrawable drawable = (GradientDrawable)listItemView.article_board.getBackground();
        drawable.setColor(mColor);


        listItemView.article_title.setText((String)listItems.get(position).get("title"));

        return convertView;
    }
}
