package com.lue.laoyoutiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.adapter.ReadArticleAdapter;
import com.lue.laoyoutiao.dialog.LoadingDialog;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.ArticleHelper;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.view.ArticleView;
import com.lue.laoyoutiao.view.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

public class ReadArticleActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate
{
    private BGARefreshLayout mBGARefreshLayout;
    private ScrollView mScrollView;
    private ArticleView v_Main_Post;
    private NoScrollListView lv_Reply_List;
    private TextView tv_all_reply;
    private View v_all_reply_devider;
    //显示正在加载的对话框
    private LoadingDialog loading_dialog;
    private ActionBar actionBar;

    private String board_name;
    private int article_id;
    private List<Article> articleList = new ArrayList<>();
    private int page_number = 1;
    private int reply_count = -1;
    private static int count_per_page = 10;
    private List<Bitmap> user_faces = new ArrayList<>();
    ArticleHelper articleHelperhelper = null;
    private ReadArticleAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        Intent intent = getIntent();
        board_name = intent.getStringExtra("board_name");
        article_id = intent.getIntExtra("article_id", 0);
        articleHelperhelper = new ArticleHelper();

        actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init_view();

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    private void init_view()
    {
        mBGARefreshLayout = (BGARefreshLayout)findViewById(R.id.layout_read_article);
        mScrollView = (ScrollView)findViewById(R.id.scrollview_read_article);
        v_Main_Post = (ArticleView)findViewById(R.id.articleview_read_article);
        lv_Reply_List = (NoScrollListView)findViewById(R.id.listview_read_article);
        tv_all_reply = (TextView)findViewById(R.id.textview_all_reply);
        v_all_reply_devider = findViewById(R.id.view_all_reply_devider);

        lv_Reply_List.setFocusable(false);
        tv_all_reply.setVisibility(View.INVISIBLE);
        v_all_reply_devider.setVisibility(View.INVISIBLE);

        loading_dialog = new LoadingDialog(this);
        //试图让dialog显示在actionbar的下面
//        if(actionBar != null)
//        {
//            Window window = loading_dialog.getWindow();
//            WindowManager.LayoutParams lp = window.getAttributes();
//            TypedValue tv = new TypedValue();
//            int actionbar_height = 0;
//            if(getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
//            {
//                actionbar_height= TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
//            }
//            window.setAttributes(lp);
//        }
        loading_dialog.show();

        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder holder = new BGANormalRefreshViewHolder(ContextApplication.getAppContext(), true);
        mBGARefreshLayout.setRefreshViewHolder(holder);
    }

    public void onEventMainThread(final Event.Read_Articles_Info articles_info)
    {
        this.reply_count = articles_info.getReply_count();
        if(page_number == 1)
        {
            articleList.clear();
            user_faces.clear();

            for(int i=0; i<articles_info.getArticles().size(); i++)
            {
                articleList.add(articles_info.getArticles().get(i));
                user_faces.add(articles_info.getUser_faces().get(i));
            }

            //如果按照下面这种方式的话，会造成 notifyDataSetChanged 不刷新
            // 因为这样使 articleList指向了另外一个对象，原来的对象并没有改变。
//            articleList = articles_info.getArticles();
//            user_faces = articles_info.getUser_faces();

            v_Main_Post.imageview_face.setImageBitmap(user_faces.get(0));
            v_Main_Post.textview_username.setText(articleList.get(0).getUser().getId());
            v_Main_Post.textview_posttime.setText(BYR_BBS_API.timeStamptoDate(articleList.get(0).getPost_time(), true));
            v_Main_Post.textview_floor.setText(R.string.first_floor);
            v_Main_Post.textview_title.setText(articleList.get(0).getTitle());
            v_Main_Post.textview_title.setVisibility(View.VISIBLE);


            String content[] = BYR_BBS_API.ParseContent(articleList.get(0).getContent());
            v_Main_Post.textview_content.setText(content[0]);
            if(content[2] != null)
            {
                v_Main_Post.textview_post_app.setText(Html.fromHtml(content[2]));
                v_Main_Post.textview_post_app.setVisibility(View.VISIBLE);
                int padding = (int)getResources().getDimension(R.dimen.article_content_textpadding_top);
                v_Main_Post.textview_post_app.setPadding(0, 0, 0, padding);
            }

//            TextView textView_content = new TextView(this);
//            textView_content.setText(articleList.get(0).getContent().trim());
//            textView_content.setTextColor(getResources().getColor(R.color.black));
//            textView_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.article_content_textsize));
//            textView_content.setPadding(0, (int)getResources().getDimension(R.dimen.article_content_textpadding_top), 0, 0);
//            v_Main_Post.linearlayout_content.addView(textView_content);
//            v_Main_Post.linearlayout_content.invalidate();

            articleList.remove(0);
            user_faces.remove(0);
            if(adapter == null)
            {
                adapter = new ReadArticleAdapter(ContextApplication.getAppContext(), articleList, user_faces);
                lv_Reply_List.setAdapter(adapter);
            }
            else
            {
                adapter.notifyDataSetChanged();
            }
        }
        else
        {
            for(int i=0; i<articles_info.getArticles().size(); i++)
            {
                articleList.add(articles_info.getArticles().get(i));
                user_faces.add(articles_info.getUser_faces().get(i));
            }

            adapter.notifyDataSetChanged();
        }

        if(mBGARefreshLayout.getCurrentRefreshStatus() == BGARefreshLayout.RefreshStatus.REFRESHING)
            mBGARefreshLayout.endRefreshing();
        if(mBGARefreshLayout.isLoadingMore())
            mBGARefreshLayout.endLoadingMore();

        loading_dialog.dismiss();
        tv_all_reply.setVisibility(View.VISIBLE);
        v_all_reply_devider.setVisibility(View.VISIBLE);

    }



    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        if(BYR_BBS_API.isNetWorkAvailable())
        {
            // 如果网络可用，则加载网络数据
            page_number = 1 ;
            articleHelperhelper.getThreadsInfo(board_name, article_id, page_number);
        }
        else
        {
            // 网络不可用，结束下拉刷新
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            mBGARefreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        if(BYR_BBS_API.isNetWorkAvailable())
        {
            // 如果网络可用，判断是否已经加载到最后一页
            if( (reply_count / count_per_page) >= page_number)
            {
                //加载网络数据
                page_number++;
                articleHelperhelper.getThreadsInfo(board_name, article_id, page_number);
                return true;
            }
            else
            {
                Toast.makeText(this, "已经到最后一页啦", Toast.LENGTH_SHORT).show();
                mBGARefreshLayout.endRefreshing();
                return false;
            }
        }
        else
        {
            // 网络不可用，结束下拉刷新
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            mBGARefreshLayout.endRefreshing();
            return false;
        }

    }

    /**
     * 重写按下返回键的响应
     */
    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
