package com.lue.laoyoutiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.HashMap;
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
    private EditText editText;
    private Button sendButton;

    private String board_name;
    private String title = "";
    private int article_id;
    private SpannableStringBuilder ssb_content;

    private List<Article> articleList = new ArrayList<>();
    private List<Bitmap> user_faces = new ArrayList<>();
    private List<String> floors = new ArrayList<>();
    private Article main_post_article;
    private Bitmap main_post_face;

    private int page_number = 1;
    private int reply_count = -1;
    private int effetive_floor_size = 0;
    private int reply_article_id;
    private String reply_reference = "";
    private static int count_per_page = 10;

    private ArticleHelper articleHelper = null;
    private ReadArticleAdapter adapter = null;


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
        reply_article_id = article_id;
        articleHelper = new ArticleHelper();

        init_view();

        //刚点进帖子的时候显示正在加载的动画
        mBGARefreshLayout.beginRefreshing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            Recycle();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 初始化视图，添加点击监视等
     */
    private void init_view()
    {
        //显示左上角的返回按钮
        actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //通过xml文件映射
        mBGARefreshLayout = (BGARefreshLayout)findViewById(R.id.layout_read_article);
        lv_Reply_List = (ListView)findViewById(R.id.listview_read_article);
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        view_mainpost = inflater.inflate(R.layout.layout_article_mainpost, lv_Reply_List, false);
        post_devider = inflater.inflate(R.layout.layout_article_mainpost_devider, lv_Reply_List, false);
        main_post = (ArticleView)view_mainpost.findViewById(R.id.articleview_read_article) ;
        lv_Reply_List.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position >= 2)
                {
                    //减2是因为有两个HeaderView
                    position = position - 2;
                    reply_article_id = articleList.get(position).getId();
                    String reply_user_id = articleList.get(position).getUser().getId();
                    editText.setHint("回复" + floors.get(position) + reply_user_id);
                    String reference = articleList.get(position).getSsb_content().toString();
                    reference = reference.replaceAll("\n", "\n: ");
                    reply_reference = "【 在 " + reply_user_id + " 的大作中提到: 】" + "\n: "
                            + reference + "\n\n";
                }
                else if(position == 0)
                {
                    reply_article_id = article_id;
                    String reply_user_id = main_post_article.getUser().getId();
                    editText.setHint("回复" + "楼主" + reply_user_id);
                    String reference = main_post_article.getContent();
                    reference = reference.replaceAll("\n", "\n: ");
                    reply_reference = "【 在 " + reply_user_id + " 的大作中提到: 】" + "\n: "
                            + reference + "\n\n";
                }
            }
        });


        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder holder = new BGANormalRefreshViewHolder(ContextApplication.getAppContext(), true);
        mBGARefreshLayout.setRefreshViewHolder(holder);


        //回复框
        editText = (EditText)findViewById(R.id.edit_text);
        editText.setHint(getResources().getString(R.string.reply_main_article));
        sendButton = (Button) findViewById(R.id.btn_send);

        emotionInputDetector = EmotionInputDetector.with(this)
                .setEmotionView(findViewById(R.id.relativelayout_emoji))
                .bindToContent(findViewById(R.id.linearlayout_articles))
                .bindToEditText((EditText) findViewById(R.id.edit_text))
                .bindToEmotionButton(findViewById(R.id.imageview_emoji))
                .setmPlusLayout(findViewById(R.id.linearlayout_plus))
                .bindToPlusButton(findViewById(R.id.imageview_plus))
                .bindSendButton(sendButton)
                .build();

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String reply_content = editText.getText().toString();
                if(reply_content.isEmpty())
                    Toast.makeText(ReadArticleActivity.this, "请输入内容后再回复哦", Toast.LENGTH_SHORT).show();
                else
                {
                    String reply_title;
                    if(title.startsWith("Re:"))
                        reply_title = title;
                    else
                        reply_title = "Re: " + title;

                    String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_ARTICLE, board_name, BYR_BBS_API.STRING_POST);
                    HashMap<String, String> params_map = new HashMap<>();
                    params_map.put("title", reply_title);
                    params_map.put("content", reply_content + "\n" + reply_reference);
                    params_map.put("reid", reply_article_id+"");
                    new ArticleHelper().postArticle(url, params_map);

                    loading_dialog = new LoadingDialog(ReadArticleActivity.this, "正在回复，请稍侯...");
                    Window dialogWindow = loading_dialog.getWindow();
                    dialogWindow.setGravity(Gravity.BOTTOM);
                    loading_dialog.show();
                }
            }
        });


        //表情相关
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
            //刷新过后将一系列数据重置
            articleList.clear();
            user_faces.clear();
            floors.clear();
            effetive_floor_size = 0;
            reply_article_id = article_id;
            reply_reference = "";

            for(int i=0; i<articles_info.getArticles().size(); i++)
            {
                if(i == 0)
                {
                    main_post_article = articles_info.getArticles().get(i);
                    main_post_face = articles_info.getUser_faces().get(i);
                }
                else
                {
                    articleList.add(articles_info.getArticles().get(i));
                    user_faces.add(articles_info.getUser_faces().get(i));

                    if(i == 1)
                        floors.add("沙发");
                    else if(i == 2)
                        floors.add("板凳");
                    else
                        floors.add(effetive_floor_size + "楼");
                }
                effetive_floor_size ++ ;
            }

            //展示主贴
            main_post.imageview_face.setImageBitmap(main_post_face);
            main_post.textview_username.setText(main_post_article.getUser().getId());
            main_post.textview_posttime.setText(BYR_BBS_API.timeStamptoDate(main_post_article.getPost_time(), true));
            main_post.textview_floor.setText(R.string.main_floor);
            main_post.textview_title.setText(main_post_article.getTitle());
            main_post.textview_title.setVisibility(View.VISIBLE);
            this.title = main_post_article.getTitle();

            SpannableString content[] = ArticleHelper.SeparateContent(main_post_article.getContent());

            //若包含表情，则将String 转化成 SpannableString，使之显示动态表情
            ssb_content = ArticleHelper.ParseContent(-1, content[0].toString(),
                    main_post.textview_content, main_post_article.getAttachment());
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

            //设置Adapter,展示跟帖部分
            if(adapter == null)
            {
                //将主贴和分割线添加为HeaderView
                lv_Reply_List.addHeaderView(view_mainpost);
                lv_Reply_List.addHeaderView(post_devider);

                adapter = new ReadArticleAdapter(this, articleList, user_faces, floors, lv_Reply_List);
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
                floors.add(effetive_floor_size + "楼");
                effetive_floor_size ++ ;
            }
            adapter.notifyDataSetChanged();
        }

        if(mBGARefreshLayout.getCurrentRefreshStatus() == BGARefreshLayout.RefreshStatus.REFRESHING)
            mBGARefreshLayout.endRefreshing();
        if(mBGARefreshLayout.isLoadingMore())
            mBGARefreshLayout.endLoadingMore();
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
     * 发表回复成功
     * @param article 发表回复成功之后返回的回复文章元数据
     */
    public void onEventMainThread(final Event.Send_Article article)
    {
        loading_dialog.dismiss();
        reply_article_id = article_id;
        reply_reference = "";

        if(article.isFailed())
        {
            String failed_info = article.getFailed_info();
            Snackbar.make(lv_Reply_List, failed_info, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        Snackbar.make(lv_Reply_List, "回复成功", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        editText.setText("");
        editText.setHint(getResources().getString(R.string.reply_main_article));
        emotionInputDetector.interceptBackPress();
        emotionInputDetector.hideSoftInput();

        articleList.add(article.getArticle());
        user_faces.add(BYR_BBS_API.My_Face.copy(Bitmap.Config.RGB_565, true));
        floors.add(reply_count + "楼");
        adapter.notifyDataSetChanged();
        //将焦点移到最后回复的地方
        lv_Reply_List.setSelection(articleList.size());
    }


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
            articleHelper.getThreadsInfo(board_name, article_id, page_number);
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
                articleHelper.getThreadsInfo(board_name, article_id, page_number);
                return true;
            }
            else
            {
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
            Recycle();
            finish();
        }
    }

    /**
     * 在关闭本Activity之前先进行内存回收
     */
    private void Recycle()
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

        ssb_content = null;
        emotionInputDetector = null;


        articleList.clear();
        System.gc();
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
