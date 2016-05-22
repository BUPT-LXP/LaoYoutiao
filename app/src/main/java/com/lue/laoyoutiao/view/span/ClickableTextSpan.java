package com.lue.laoyoutiao.view.span;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import com.lue.laoyoutiao.activity.BoardArticleListActivity;
import com.lue.laoyoutiao.activity.ReadArticleActivity;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.helper.ArticleHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/5/17.
 */
public class ClickableTextSpan extends ClickableSpan
{
    private Context context;
    private String text;

    public ClickableTextSpan(Context context, String text)
    {
        super();
        this.context = context;
        this.text = text;
    }

    @Override
    public void updateDrawState(TextPaint ds)
    {
        ds.setColor(Color.BLUE); //设置链接的文本颜色
//        ds.setUnderlineText(false); //去掉下划线
    }

    @Override
    public void onClick(View widget)
    {
        if(text.contains("http://bbs.byr.cn/"))
        {
            text = text.replace("http://bbs.byr.cn/", "");
            text = text.replace("#!", "");

            if(text.contains("article"))
            {
                int index1 = text.lastIndexOf("/");
                String id = text.substring(index1+1);
                int article_id;
                try
                {
                    article_id = Integer.parseInt(id);
                }catch (NumberFormatException e)
                {
                    Toast.makeText(context, "Oops, 网址格式有错误！",Toast.LENGTH_SHORT).show();
                    return;
                }

                EventBus.getDefault().post(new Event.Start_New());

                text = text.substring(0, index1);
                int index2 = text.lastIndexOf("/");
                String board_name = text.substring(index2+1);

                ArticleHelper helper = new ArticleHelper();
                helper.getThreadsInfo(board_name, article_id, 1);

                Intent intent = new Intent(context, ReadArticleActivity.class);
                intent.putExtra("board_name", board_name);
                intent.putExtra("article_id", article_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            else if(text.contains("board"))
            {
                int index = text.lastIndexOf("/");
                String board_name = text.substring(index+1);

                //本地 SharedPreferences
                SharedPreferences My_SharedPreferences;

                My_SharedPreferences = context.getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);

                String board_description = My_SharedPreferences.getString(board_name, "null");

                if(!board_description.equals("null"))
                {
                    EventBus.getDefault().post(new Event.Start_New());

                    Intent intent = new Intent(context, BoardArticleListActivity.class);
                    intent.putExtra("Board_Description", board_description);
                    boolean is_favorite = !(BYR_BBS_API.Favorite_Boards.get(board_description) == null);
                    intent.putExtra("Is_Favorite", is_favorite);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else
                {
                    Toast.makeText(context, "Oops, 网址格式有错误！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
