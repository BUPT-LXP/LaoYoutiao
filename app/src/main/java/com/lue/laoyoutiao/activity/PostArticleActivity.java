package com.lue.laoyoutiao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.dialog.LoadingDialog;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.helper.ArticleHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class PostArticleActivity extends AppCompatActivity
{
    private Menu menu;

    private EditText editText_Title;
    private EditText editText_Content;
    //显示正在加载的对话框
    private LoadingDialog loading_dialog;

    private String board_name;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_article);

        editText_Title = (EditText)findViewById(R.id.title);
        editText_Content = (EditText)findViewById(R.id.content);

        intent = getIntent();
        board_name = intent.getStringExtra("board_name");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        EventBus.getDefault().register(this);
    }


    /**
     * 添加ActionBar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post_article, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                Confirm();
                break;
            case R.id.action_send:
                PostArticle();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 返回之前先检测是否已经有输入，若没有输入，则直接返回；若已经有输入，则弹出提示框。
     */
    public void Confirm()
    {
        if(editText_Title.getText().toString().isEmpty() && editText_Content.getText().toString().isEmpty())
        {
            setResult(BoardArticleListActivity.RESULT_GIVEUP, intent);
            finish();
            return;
        }

        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(PostArticleActivity.this);
        dialog_builder.setIcon(R.mipmap.warning);
        dialog_builder.setTitle("提示");
        dialog_builder.setMessage("确认放弃已编辑内容吗");
        dialog_builder.setNegativeButton( "取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        dialog_builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                setResult(BoardArticleListActivity.RESULT_GIVEUP, intent);
                finish();
            }
        });
        dialog_builder.create().show();
    }


    @Override
    public void onBackPressed()
    {
        Confirm();
    }


    private void PostArticle()
    {
        if(editText_Title.getText().toString().isEmpty())
        {
            Toast.makeText(this, "请输入文章标题", Toast.LENGTH_LONG).show();
            return;
        }
        String title = editText_Title.getText().toString();
        String content = editText_Content.getText().toString();
        String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_ARTICLE, board_name, BYR_BBS_API.STRING_POST);
        HashMap<String, String> params_map = new HashMap<>();
        params_map.put("title", title);
        params_map.put("content", content);
        new ArticleHelper().postArticle(url, params_map);

        loading_dialog = new LoadingDialog(PostArticleActivity.this, "正在发布，请稍侯...");
        Window dialogWindow = loading_dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        loading_dialog.show();
    }


    /**
     * 发表回复成功
     * @param article 发表回复成功之后返回的回复文章元数据
     */
    public void onEventMainThread(final Event.Send_Article article)
    {
        loading_dialog.dismiss();

        if(article.isFailed())
        {
            String failed_info = article.getFailed_info();
            Snackbar.make(editText_Content, failed_info, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        Toast.makeText(this, "发布帖子成功", Toast.LENGTH_LONG).show();

        setResult(BoardArticleListActivity.RESULT_GIVEUP, intent);
        finish();
    }

}
