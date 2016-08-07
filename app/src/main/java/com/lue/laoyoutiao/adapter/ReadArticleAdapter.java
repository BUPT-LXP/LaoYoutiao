package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.helper.ArticleHelper;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.network.PicassoHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.view.ArticleView;
import com.lue.laoyoutiao.view.span.ClickableMovementMethod;

import java.util.List;

import de.greenrobot.event.EventBus;



/**
 * Created by Lue on 2016/4/26.
 */
public class ReadArticleAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater listContainer;
    private List<Article> reply_articles;
    private List<String> user_faces;
    private List<String> floors;
    private ListView listview;
    private int textview_width;


    public ReadArticleAdapter(Context context, List<Article> articles,
                              List<String> faces, List<String> floors, ListView listview)
    {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.reply_articles = articles;
        this.user_faces = faces;
        this.floors = floors;
        this.listview = listview;

        //注册EventBus
        EventBus.getDefault().register(this);
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

            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.list_item_article_content, null);
            viewHolder = new ArticleView(context, convertView);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ArticleView) convertView.getTag();
        }

        Article article = reply_articles.get(position);
        PicassoHelper.getPicassoHelper().loadImage(user_faces.get(position), 1).into(viewHolder.imageview_face);


        viewHolder.textview_username.setText(article.getUser().getId());
        viewHolder.textview_posttime.setText(BYR_BBS_API.timeStamptoDate(article.getPost_time(), true));
        viewHolder.textview_floor.setText(floors.get(position));



        if(!article.is_content_separated())
        {
            SpannableString content[] = ArticleHelper.SeparateContent(article.getContent());
            article.setIs_content_separated(true);
            article.setSs_reference(content[1]);
            article.setSs_app(content[2]);

            //若包含表情，则将String 转化成 SpannableString，使之显示动态表情
            if(content[0] != null)
            {
                SpannableStringBuilder ssb = ArticleHelper.ParseContent(position, content[0].toString(),
                        viewHolder.textview_content, article.getAttachment());
                article.setSsb_content(ssb);
                viewHolder.textview_content.setText(ssb);
                viewHolder.textview_content.setMovementMethod(ClickableMovementMethod.getInstance());
            }
        }
        else
        {
            viewHolder.textview_content.setText(article.getSsb_content());
            viewHolder.textview_content.setMovementMethod(ClickableMovementMethod.getInstance());
        }

        if (article.getSs_reference() != null)
        {
            viewHolder.textview_content_reply.setText(article.getSs_reference());
            viewHolder.textview_content_reply.setVisibility(View.VISIBLE);
            int padding = (int) context.getResources().getDimension(R.dimen.article_content_textpadding_top);
            viewHolder.textview_content_reply.setPadding(0, 0, 0, padding);
        }
        else
        {
            viewHolder.textview_content_reply.setVisibility(View.GONE);
        }
        if (article.getSs_app() != null)
        {
            viewHolder.textview_post_app.setText(article.getSs_app());
            viewHolder.textview_post_app.setVisibility(View.VISIBLE);
            int padding = (int) context.getResources().getDimension(R.dimen.article_content_textpadding_top);
            viewHolder.textview_post_app.setPadding(0, 0, 0, padding);

            viewHolder.textview_post_app.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else
        {
            viewHolder.textview_post_app.setVisibility(View.GONE);
        }


        return convertView;
    }

    /**
     * 响应 发布的图片附件信息，并将其展现
     * @param attachment_images 图片附件
     */
    public void onEventMainThread(final Event.Attachment_Images attachment_images)
    {
        int article_index = attachment_images.getArticle_index();
        if(article_index >= 0)
        {
            int child_index = article_index - listview.getFirstVisiblePosition() + listview.getHeaderViewsCount();
            View view = listview.getChildAt(child_index);

            if(textview_width == 0)
                textview_width = listview.getWidth();

            SpannableStringBuilder ssb = ArticleHelper.Show_Attachments(reply_articles.get(article_index).getSsb_content(),
                    attachment_images.getImages(), textview_width, attachment_images.getUrls(), context, attachment_images.getSizes());
            reply_articles.get(article_index).setSsb_content(ssb);
            try
            {
                TextView textview_content = (TextView) view.findViewById(R.id.textview_article_content);

                textview_content.setText(ssb);
            }
            catch (NullPointerException | IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收站外高清大图
     * @param bitmap_outside 图片信息
     */
    public void onEventMainThread(final Event.Bitmap_Outside bitmap_outside)
    {
        int article_index = bitmap_outside.getArticle_index();
        if(article_index >= 0)
        {
            int child_index = article_index - listview.getFirstVisiblePosition() + listview.getHeaderViewsCount();
            View view = listview.getChildAt(child_index);

            if(textview_width == 0)
                textview_width = listview.getWidth();

            String url = bitmap_outside.getUrl();
            Bitmap image = bitmap_outside.getImage_hd();

            SpannableStringBuilder ssb  = ArticleHelper.Show_Outside_Images(reply_articles.get(article_index).getSsb_content()
                    , image, textview_width, url, context);
            reply_articles.get(article_index).setSsb_content(ssb);

            try
            {
                TextView textview_content = (TextView) view.findViewById(R.id.textview_article_content);

                textview_content.setText(ssb);
            }
            catch (NullPointerException | IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
    }
}
