package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.view.ArticleView;

import java.util.List;

/**
 * Created by Lue on 2016/4/26.
 */
public class ReadArticleAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater listContainer;
    private List<Article> reply_articles;
    private List<Bitmap> user_faces;

    public ReadArticleAdapter(Context context, List<Article> articles, List<Bitmap> faces)
    {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.reply_articles = articles;
        this.user_faces = faces;
    }

    @Override
    public int getCount()
    {
        return reply_articles.size();
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
        ArticleView viewHolder = null;
        if(convertView == null)
        {
            viewHolder = new ArticleView(context);
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.list_item_article_content, null);

            viewHolder.imageview_face = (ImageView)convertView.findViewById(R.id.imageview_user_face);
            viewHolder.textview_username = (TextView)convertView.findViewById(R.id.textview_user_name);
            viewHolder.textview_posttime = (TextView)convertView.findViewById(R.id.textview_post_time);
            viewHolder.textview_floor = (TextView)convertView.findViewById(R.id.textview_user_floor);
            viewHolder.textview_title = (TextView)convertView.findViewById(R.id.textview_article_title);
            viewHolder.textview_content = (TextView)convertView.findViewById(R.id.textview_article_content);

            viewHolder.textview_title.setVisibility(View.GONE);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ArticleView)convertView.getTag();
        }

        viewHolder.imageview_face.setImageBitmap(user_faces.get(position));
        viewHolder.textview_username.setText(reply_articles.get(position).getUser().getId());
        viewHolder.textview_posttime.setText(BYR_BBS_API.timeStamptoDate(reply_articles.get(position).getPost_time(), true));
        viewHolder.textview_floor.setText(position + 1 + "楼");
        viewHolder.textview_content.setText(reply_articles.get(position).getContent().trim());

        return convertView;
    }
}
