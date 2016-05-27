package com.lue.laoyoutiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.adapter.EmojiFragmentStatePagerAdapter;
import com.lue.laoyoutiao.adapter.ReadArticleAdapter;
import com.lue.laoyoutiao.dialog.LoadingDialog;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.ArticleHelper;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.view.ArticleView;
import com.lue.laoyoutiao.view.emoji.EmojiClickManager;
import com.lue.laoyoutiao.view.emoji.EmotionInputDetector;
import com.lue.laoyoutiao.view.emoji.SlidingTabLayout;
import com.lue.laoyoutiao.view.span.ClickableMovementMethod;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

//import com.dss886.emotioninputdetector.library.EmotionInputDetector;


public class ReadArticleActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate
{
    private static final String TAG = "ReadArticleActivity";

    private BGARefreshLayout mBGARefreshLayout;
    private ListView lv_Reply_List;
    private View view_mainpost;
    private View post_devider;
    private ArticleView main_post;
    private LayoutInflater inflater;
    //显示正在加载的对话框
    private LoadingDialog loading_dialog;
    private ActionBar actionBar;

    private String board_name;
    private SpannableStringBuilder ssb_content;
    private int article_id;
    private List<Article> articleList = new ArrayList<>();
    private int page_number = 1;
    private int reply_count = -1;
    private static int count_per_page = 10;
    private List<Bitmap> user_faces = new ArrayList<>();
    ArticleHelper articleHelperhelper = null;
    private ReadArticleAdapter adapter = null;

//    public Hashtable<String, Bitmap> images_hd = new Hashtable<>();

    //表情界面
    private EmotionInputDetector emotionInputDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        //注册EventBus
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        board_name = intent.getStringExtra("board_name");
        article_id = intent.getIntExtra("article_id", 0);
        articleHelperhelper = new ArticleHelper();

        init_view();

        ArticleHelper helper = new ArticleHelper();
        helper.getThreadsInfo(board_name, article_id, 1);

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
        post_devider = inflater.inflate(R.layout.layout_article_mainpost_devider, lv_Reply_List, false);


        main_post = (ArticleView)view_mainpost.findViewById(R.id.articleview_read_article) ;

        loading_dialog = new LoadingDialog(this);
        loading_dialog.show();

        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder holder = new BGANormalRefreshViewHolder(ContextApplication.getAppContext(), true);
        mBGARefreshLayout.setRefreshViewHolder(holder);



        //回复表情框
        emotionInputDetector = EmotionInputDetector.with(this)
                .setEmotionView(findViewById(R.id.relativelayout_emoji))
                .bindToContent(findViewById(R.id.linearlayout_articles))
                .bindToEditText((EditText) findViewById(R.id.edit_text))
                .bindToEmotionButton(findViewById(R.id.imageview_emoji))
                .setmPlusLayout(findViewById(R.id.linearlayout_plus))
                .bindToPlusButton(findViewById(R.id.imageview_plus))
                .build();


        final String[] titles = new String[]{"经典", "悠嘻猴", "兔斯基", "洋葱头"};
        EmojiFragmentStatePagerAdapter mViewPagerAdapter = new EmojiFragmentStatePagerAdapter(getSupportFragmentManager(), titles);
        final ViewPager viewpager = (ViewPager)findViewById(R.id.viewpager);
        viewpager.setAdapter(mViewPagerAdapter);
        viewpager.setCurrentItem(1);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.layout_emoji_tab_indicator, R.id.text);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorPrimary));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewpager);

        EmojiClickManager globalOnItemClickListener = EmojiClickManager.getInstance();
        globalOnItemClickListener.attachToEditText((EditText)findViewById(R.id.edit_text));



    }

    /**
     * 响应 ArticleHelper 中发布的主贴及其回复信息，并将信息展现
     * @param articles_info 主贴及回复信息
     */
    public void onEventMainThread(final Event.Read_Articles_Info articles_info)
    {
        this.reply_count = articles_info.getReply_count();
        if(page_number == 1)
        {
//            lv_Reply_List.setSelection(0);

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
            main_post.textview_floor.setText(R.string.main_floor);
            main_post.textview_title.setText(articleList.get(0).getTitle());
            main_post.textview_title.setVisibility(View.VISIBLE);


            SpannableString content[] = ArticleHelper.SeparateContent(articleList.get(0).getContent());

            //若包含表情，则将String 转化成 SpannableString，使之显示动态表情
            ssb_content = ArticleHelper.ParseContent(-1, content[0].toString(),
                    main_post.textview_content, articleList.get(0).getAttachment());
            main_post.textview_content.setText(ssb_content);
            main_post.textview_content.setMovementMethod(ClickableMovementMethod.getInstance());

            if(content[2] != null)
            {
                main_post.textview_post_app.setText(content[2]);
                main_post.textview_post_app.setVisibility(View.VISIBLE);
                int padding = (int)getResources().getDimension(R.dimen.article_content_textpadding_top);
                main_post.textview_post_app.setPadding(0, 0, 0, padding);
                main_post.textview_post_app.setMovementMethod(LinkMovementMethod.getInstance());
            }


            //主贴内容已经在main_post中显示过了，因此将其移除，剩下的数据传给Adapter
            articleList.remove(0);
            user_faces.remove(0);

            if(adapter == null)
            {
                //将主贴和分割线添加为HeaderView
                lv_Reply_List.addHeaderView(view_mainpost);
                lv_Reply_List.addHeaderView(post_devider);

                adapter = new ReadArticleAdapter(this, articleList, user_faces, lv_Reply_List);
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

    /**
     * 响应 发布的图片附件信息，并将其展现
     * 需要注意的是，此处只需要响应一次（主贴），后续发布的在Adapter中响应(暂时无法实现)
     * @param attachment_images 图片附件
     */
    public void onEventMainThread(final Event.Attachment_Images attachment_images)
    {
        int article_index = attachment_images.getArticle_index();
        if(-1 == article_index)
        {
            ssb_content = ArticleHelper.Show_Attachments(ssb_content, attachment_images.getImages(),
                    main_post.textview_content.getWidth(), attachment_images.getUrls(), this, attachment_images.getSizes());
            main_post.textview_content.setText(ssb_content);
        }
    }

    /**
     *当点击了指向本站的链接时，开启一个新的Activity，此时需要注销当前Activity的EventBus
     * @param start_new 标志
     */
    public void onEventBackgroundThread(final Event.Start_New start_new)
    {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    /**
     * 接收附件高清大图
     * @param bitmap_hd 图片信息
     */
//    public void onEventBackgroundThread(final Event.Bitmap_HD bitmap_hd)
//    {
//        String url = bitmap_hd.getUrl();
//        Bitmap image = bitmap_hd.getImage_hd();
//
//        images_hd.put(url, image);
//    }

    /**
     * 接收站外高清大图
     * @param bitmap_outside 图片信息
     */
    public void onEventMainThread(final Event.Bitmap_Outside bitmap_outside)
    {
        String url = bitmap_outside.getUrl();
        Bitmap image = bitmap_outside.getImage_hd();

        if(bitmap_outside.getArticle_index() == -1)
        {
            ssb_content = ArticleHelper.Show_Outside_Images(ssb_content, image,
                    main_post.textview_content.getWidth(), url, this);
            main_post.textview_content.setText(ssb_content);
        }
    }

    /**
     * 站内链接错误，指定的文章不存在或链接错误
     * @param NotExist 错误信息
     */
    public void onEventMainThread(final Event.Article_Not_Exist NotExist)
    {
        Toast.makeText(this, NotExist.getError_info(), Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 重写下拉刷新
     * @param refreshLayout
     */
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

    /**
     * 重写上拉加载
     * @param refreshLayout
     * @return
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        if(BYR_BBS_API.isNetWorkAvailable())
        {
            // 如果网络可用，判断是否已经加载到最后一页
            if( reply_count > page_number * count_per_page )
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
        if (!emotionInputDetector.interceptBackPress())
        {
            //为adapter注销EventBus
            if (adapter != null)
            {
                EventBus.getDefault().unregister(adapter);
                adapter = null;
            }

            //释放图片内存
            for (Article article : articleList)
            {
                if (article.getSsb_content() != null)
                    article.getSsb_content().clear();
            }
            if(user_faces != null)
            {
                for (Bitmap bitmap : user_faces)
                {
                    if (bitmap != null)
                        bitmap.recycle();
                }
            }
            for (Article article : articleList)
            {
                article = null;
            }


            articleList.clear();
            System.gc();
            finish();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        //注册EventBus
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //注销EventBus
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

}
