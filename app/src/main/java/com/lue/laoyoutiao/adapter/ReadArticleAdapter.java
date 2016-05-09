package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //自定义视图
        ArticleView viewHolder;

        if (convertView == null)
        {
            viewHolder = new ArticleView(context);
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.list_item_article_content, null);

            viewHolder.imageview_face = (ImageView) convertView.findViewById(R.id.imageview_user_face);
            viewHolder.textview_username = (TextView) convertView.findViewById(R.id.textview_user_name);
            viewHolder.textview_posttime = (TextView) convertView.findViewById(R.id.textview_post_time);
            viewHolder.textview_floor = (TextView) convertView.findViewById(R.id.textview_user_floor);
            viewHolder.textview_title = (TextView) convertView.findViewById(R.id.textview_article_title);
            viewHolder.textview_content = (TextView) convertView.findViewById(R.id.textview_article_content);
            viewHolder.textview_content_reply = (TextView) convertView.findViewById(R.id.textview_article_content_reference);
            viewHolder.textview_post_app = (TextView) convertView.findViewById(R.id.textview_article_post_app);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ArticleView) convertView.getTag();
        }

        viewHolder.imageview_face.setImageBitmap(user_faces.get(position));
        viewHolder.textview_username.setText(reply_articles.get(position).getUser().getId());
        viewHolder.textview_posttime.setText(BYR_BBS_API.timeStamptoDate(reply_articles.get(position).getPost_time(), true));
        if(position == 0)
            viewHolder.textview_floor.setText("沙发");
        else if(position == 1)
            viewHolder.textview_floor.setText("板凳");
        else
            viewHolder.textview_floor.setText(position + 1 + "楼");

        String content[] = BYR_BBS_API.SeparateContent(reply_articles.get(position).getContent());

        //若包含表情，则将String 转化成 SpannableString，使之显示动态表情
        if (content[0].contains("[em"))
            viewHolder.textview_content.setText(BYR_BBS_API.ParseContent(content[0], viewHolder.textview_content));
        else
            viewHolder.textview_content.setText(content[0]);

        if (content[2] != null)
        {
            viewHolder.textview_post_app.setText(Html.fromHtml(content[2]));
            viewHolder.textview_post_app.setVisibility(View.VISIBLE);
            int padding = (int) context.getResources().getDimension(R.dimen.article_content_textpadding_top);
            viewHolder.textview_post_app.setPadding(0, 0, 0, padding);
        }
        if (content[1] != null)
        {
            viewHolder.textview_content_reply.setText(content[1]);
            viewHolder.textview_content_reply.setVisibility(View.VISIBLE);
            int padding = (int) context.getResources().getDimension(R.dimen.article_content_textpadding_top);
            viewHolder.textview_content_reply.setPadding(0, 0, 0, padding);
        }

        return convertView;
    }

}
