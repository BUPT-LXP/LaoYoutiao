package com.lue.laoyoutiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lue.laoyoutiao.R;

/**
 * Created by Lue on 2016/4/26.
 */
public class ArticleView extends LinearLayout implements View.OnClickListener
{
    private Context context;
    public ImageView imageview_face;
    public TextView textview_username;
    public TextView textview_posttime;
    public TextView textview_floor;
    public TextView textview_title;
    public TextView textview_content;
    public TextView textview_content_reply;
    public TextView textview_post_app;


    public ArticleView(Context context, View view)
    {
        super(context);

        this.context = context;
        imageview_face = (ImageView)view.findViewById(R.id.imageview_user_face);
        textview_username = (TextView)view.findViewById(R.id.textview_user_name);
        textview_posttime = (TextView)view.findViewById(R.id.textview_post_time);
        textview_floor = (TextView)view.findViewById(R.id.textview_user_floor);
        textview_title = (TextView)view.findViewById(R.id.textview_article_title);
        textview_content = (TextView)view.findViewById(R.id.textview_article_content);
        textview_content_reply = (TextView)view.findViewById(R.id.textview_article_content_reference);
        textview_post_app = (TextView)view.findViewById(R.id.textview_article_post_app);

        imageview_face.setOnClickListener(this);
        textview_content.setOnClickListener(this);
//        textview_post_app.setOnClickListener(this);
    }

    public ArticleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.context = context;
        View view = View.inflate(context, R.layout.list_item_article_content, this);
        imageview_face = (ImageView)view.findViewById(R.id.imageview_user_face);
        textview_username = (TextView)view.findViewById(R.id.textview_user_name);
        textview_posttime = (TextView)view.findViewById(R.id.textview_post_time);
        textview_floor = (TextView)view.findViewById(R.id.textview_user_floor);
        textview_title = (TextView)view.findViewById(R.id.textview_article_title);
        textview_content = (TextView)view.findViewById(R.id.textview_article_content);
        textview_content_reply = (TextView)view.findViewById(R.id.textview_article_content_reference);
        textview_post_app = (TextView)view.findViewById(R.id.textview_article_post_app);

        imageview_face.setOnClickListener(this);
        textview_content.setOnClickListener(this);
//        textview_post_app.setOnClickListener(this);
    }

    public ArticleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View v)
    {

        int id = v.getId();
        switch (id)
        {
            case R.id.imageview_user_face:
                Toast.makeText(context, "点击头像", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textview_article_content:
                Toast.makeText(context, "点击内容", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textview_article_post_app:
                Toast.makeText(context, "点击APP", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
