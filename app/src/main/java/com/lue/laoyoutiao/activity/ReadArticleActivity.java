package com.lue.laoyoutiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

public class ReadArticleActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate
{
    private BGARefreshLayout mBGARefreshLayout;
    private ListView lv_Reply_List;
    private View view_mainpost;
    private View post_devider;
    private ArticleView main_post;
    private LinearLayout main_devider;
    private LayoutInflater inflater;
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

        init_view();

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    private void init_view()
    {
        actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mBGARefreshLayout = (BGARefreshLayout)findViewById(R.id.layout_read_article);
        lv_Reply_List = (ListView)findViewById(R.id.listview_read_article);


        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        view_mainpost = inflater.inflate(R.layout.layout_article_mainpost, lv_Reply_List, false);
        post_devider = inflater.inflate(R.layout.layout_reply_devider, lv_Reply_List, false);

        main_post = (ArticleView)view_mainpost.findViewById(R.id.articleview_read_article) ;
        main_devider = (LinearLayout)post_devider.findViewById(R.id.linearlayout);

        loading_dialog = new LoadingDialog(this);
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

            main_post.imageview_face.setImageBitmap(user_faces.get(0));
            main_post.textview_username.setText(articleList.get(0).getUser().getId());
            main_post.textview_posttime.setText(BYR_BBS_API.timeStamptoDate(articleList.get(0).getPost_time(), true));
            main_post.textview_floor.setText(R.string.first_floor);
            main_post.textview_title.setText(articleList.get(0).getTitle());
            main_post.textview_title.setVisibility(View.VISIBLE);


            String content[] = BYR_BBS_API.ParseContent(articleList.get(0).getContent());
            main_post.textview_content.setText(content[0]);
            if(content[2] != null)
            {
                main_post.textview_post_app.setText(Html.fromHtml(content[2]));
                main_post.textview_post_app.setVisibility(View.VISIBLE);
                int padding = (int)getResources().getDimension(R.dimen.article_content_textpadding_top);
                main_post.textview_post_app.setPadding(0, 0, 0, padding);
            }

            //主贴内容已经在main_post中显示过了，因此将其移除，剩下的数据传给Adapter
            articleList.remove(0);
            user_faces.remove(0);

            if(adapter == null)
            {
                //将主贴和分割线添加为HeaderView
                lv_Reply_List.addHeaderView(view_mainpost);
                lv_Reply_List.addHeaderView(post_devider);

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
