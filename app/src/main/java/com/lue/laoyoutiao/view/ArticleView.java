package com.lue.laoyoutiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lue.laoyoutiao.R;

/**
 * Created by Lue on 2016/4/26.
 */
public class ArticleView extends LinearLayout
{
    public ImageView imageview_face;
    public TextView textview_username;
    public TextView textview_posttime;
    public TextView textview_floor;
    public TextView textview_title;
//    public LinearLayout linearlayout_content;
    public TextView textview_content;
    public TextView textview_content_reply;
    public TextView textview_post_app;


    public ArticleView(Context context)
    {
        super(context);
    }

    public ArticleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        View view = View.inflate(context, R.layout.list_item_article_content, this);
        imageview_face = (ImageView)view.findViewById(R.id.imageview_user_face);
        textview_username = (TextView)view.findViewById(R.id.textview_user_name);
        textview_posttime = (TextView)view.findViewById(R.id.textview_post_time);
        textview_floor = (TextView)view.findViewById(R.id.textview_user_floor);
        textview_title = (TextView)view.findViewById(R.id.textview_article_title);
        textview_content = (TextView)view.findViewById(R.id.textview_article_content);
        textview_content_reply = (TextView)view.findViewById(R.id.textview_article_content_reference);
        textview_post_app = (TextView)view.findViewById(R.id.textview_article_post_app);
//        linearlayout_content = (LinearLayout)view.findViewById(R.id.linearlayout_article_content);
    }

    public ArticleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


}
