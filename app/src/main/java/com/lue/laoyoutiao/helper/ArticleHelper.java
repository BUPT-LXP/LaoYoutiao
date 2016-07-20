package com.lue.laoyoutiao.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.cache.ACache;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.metadata.Attachment;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.network.PicassoHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.threadpool.ThreadPool;
import com.lue.laoyoutiao.view.span.CenteredImageSpan;
import com.lue.laoyoutiao.view.span.ClickableTextSpan;
import com.lue.laoyoutiao.view.span.GifCallback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Lue on 2016/4/26.
 */
public class ArticleHelper
{
    private static final String TAG = "ArticleHelper";
    private OkHttpHelper okHttpHelper;
    private static ExecutorService singleTaskExecutor;
    private static ExecutorService fixedTaskExecutor;

    public ArticleHelper()
    {
        okHttpHelper = OkHttpHelper.getM_OkHttpHelper();
        singleTaskExecutor = ThreadPool.getSingleTaskExecutor();
        fixedTaskExecutor = ThreadPool.getFixedTaskExecutor();
    }

    /**
     * 获取当前帖子下的回复信息
     * @param board_name 版面名称
     * @param article_id 帖子ID
     * @param page 页数
     */
    public void getThreadsInfo(String board_name, int article_id, final int page)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        final String url = BYR_BBS_API.buildGETUrl(params, BYR_BBS_API.STRING_THREADS, board_name, article_id+"");

        fixedTaskExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Response response = okHttpHelper.getExecute(url);
                    String response_result = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(response_result);
                    response_result = jsonObject.getString("article");
                    List<Article> articles = new Gson().fromJson(response_result, new TypeToken<List<Article>>() {}.getType());

                    List<Bitmap> faces = new ArrayList<Bitmap>();
                    for(Article article : articles)
                    {
                        Bitmap face = PicassoHelper.getPicassoHelper().getBitmap(article.getUser().getFace_url(), 2);
                        faces.add(face);
                    }
                    int reply_count = jsonObject.getInteger("reply_count");
                    EventBus.getDefault().post(new Event.Read_Articles_Info(articles, faces, reply_count));

                }
                catch (NullPointerException e)
                {
                    EventBus.getDefault().post(new Event.Article_Not_Exist("指定的文章不存在或链接错误"));
                }
                catch (IOException e)
                {
                    EventBus.getDefault().post(new Event.Article_Not_Exist("网络错误"));
                }
            }
        });

    }


    /**
     * 将文章内容通过正则表达式等解析成相应的类型
     * @param article_content 内容
     * @return 内容、引用内容、使用的APP
     */
    public static SpannableString[] SeparateContent(String article_content)
    {
        Pattern pattern;
        Matcher matcher;

        while (article_content.endsWith("-") || article_content.endsWith("\n"))
        {
            article_content = article_content.substring(0, article_content.length() -1);
        }

        //分离出用户使用的客户端程序
        String my_app = null;
        SpannableString ss_my_app = null;
        //实例 ：****[url=http://guiyou.wangx.in]发自「贵邮」[/url]
        if(article_content.endsWith("[/url]"))
        {
            //去掉后面的 [/url]：  ****[url=http://guiyou.wangx.in]发自「贵邮」
            article_content = article_content.substring(0, article_content.length() -6);
            int index = article_content.lastIndexOf("[url=");
            //[url=http://guiyou.wangx.in]发自「贵邮」
            my_app = article_content.substring(index);
            //去掉整个app_string的内容, 此时article_content中只剩下****了
            article_content = article_content.substring(0, index);

            index = my_app.lastIndexOf("]");
            // url = http://guiyou.wangx.in
            String url = my_app.substring(5, index);
            //发自「贵邮」
            my_app = my_app.substring(index + 1);
            ss_my_app = new SpannableString(my_app);

            ClickableTextSpan span = new ClickableTextSpan(ContextApplication.getAppContext(), my_app, url);
            ss_my_app.setSpan(span, 0, my_app.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//            //"<a href=\"http://guiyou.wangx.in\"><u>发自「贵邮」</u></a>"
//            my_app = "<a href=\"" + url + "\"><u>" + my_app  + "</u></a>";
        }

        //分离出用户的评论以及引用内容
        String my_content = article_content;
        String my_reference = null;



        pattern = Pattern.compile("【[^】]*?在[\\s\\S]*?的大作中提到:[^】]*?】");
        matcher = pattern.matcher(article_content);

        if(matcher.find())
        {
            my_reference = matcher.group();
        }
        if(my_reference != null)
        {
            int index = article_content.indexOf(my_reference);
            my_reference = article_content.substring(index);

            my_content = article_content.substring(0, index);

            //此处是为了辨别引用处于回复上面的一些情况
            //例如测试区的 "\n: 嘿嘿嘿\n: 哈哈哈\n: 哼哼哼\n下面下面下面"
            // 其实"下面下面下面"是回复内容, "\n: 嘿嘿嘿\n: 哈哈哈\n: 哼哼哼"是引用内容
            int index1 = my_reference.lastIndexOf("\n:");
            String s1 = my_reference.substring(index1 + 2);
            // s1:哼哼哼\n下面下面下面
            int index2 = s1.indexOf("\n");
            if(index2 != -1)
            {
                //在后面找到了换行符，表明此时回复内容在引用下面，是不正常的，需要调整
                if(my_content.equals("\n"))
                    my_content = s1.substring(index2+1);
                else
                    my_content = my_content + s1.substring(index2+1);
                my_reference = my_reference.replace(my_content, "");
            }

        }

        //返回结果
        SpannableString array[] = new SpannableString[3];
        while (my_content.endsWith("-") || my_content.endsWith("\n"))
        {
            my_content = my_content.substring(0, my_content.length() -1);
        }
        if(my_reference != null)
        {
            while (my_reference.endsWith("-") || my_reference.endsWith("\n") || my_reference.endsWith(":"))
            {
                my_reference = my_reference.substring(0, my_reference.length() - 1);
            }
        }


        //取消斜体
        my_content = my_content.replaceAll("\\[[iI]\\]", "");
        my_content = my_content.replaceAll("\\[/[iI]\\]", "");

        //取消下划线
        my_content = my_content.replaceAll("\\[[uU]\\]", "");
        my_content = my_content.replaceAll("\\[/[uU]\\]", "");

        //取消字体
        my_content = my_content.replaceAll("\\[face=[^\\]]*?\\][\\s\\S]*?", "");
        my_content = my_content.replaceAll("\\[/face\\]", "");

        SpannableString ss_my_content = null;
        if(my_content != null)
            ss_my_content= new SpannableString(my_content);
        SpannableString ss_my_reference = null;
        if(my_reference != null)
            ss_my_reference = new SpannableString(my_reference);

        array[0] = ss_my_content;
        array[1] = ss_my_reference;
        array[2] = ss_my_app;
        return array;
    }


    /**
     * SpannableStringBuilder
     * @param article_index 文章序号，主贴序号为-1，回复序号既是Adapter的position
     * @param content 内容
     * @param textView 显示的textview
     * @param attachment 文章附件
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder ParseContent(final int article_index, String content,
                                                      TextView textView, final Attachment attachment)
    {
        final Context context = ContextApplication.getAppContext();
        Pattern pattern;
        Matcher matcher;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);

        //文字加粗
        if(content.contains("[b]") && content.contains("[/b]"))
        {
            pattern = Pattern.compile("\\[b\\]([\\s\\S]*?)\\[/b\\]");
            matcher = pattern.matcher(spannableStringBuilder);

            //3表示的是[b] 的长度
            //4表示的是[/b]的长度
            //7是相加的长度
            int bold_num = 0;
            while (matcher.find())
            {
                spannableStringBuilder.delete(matcher.start()- bold_num*7, matcher.start()+3 - bold_num*7);
                spannableStringBuilder.delete(matcher.end()-7 - bold_num*7, matcher.end()-3 - bold_num*7);
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), matcher.start() - bold_num*7, matcher.end()-7 - bold_num*7,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bold_num ++ ;
            }
        }

        //文字颜色
        if(content.contains("[color=") && content.contains("[/color]"))
        {
            pattern = Pattern.compile("\\[color=([^\\]]*?)\\]([\\s\\S]*?)\\[/color\\]");
            matcher = pattern.matcher(spannableStringBuilder);

            //15表示的是[color=#XXXXXX] 的长度
            //8表示的是[/color]的长度
            //23是相加的长度
            int color_num = 0;
            while (matcher.find())
            {
                String color = matcher.group(1);
                spannableStringBuilder.delete(matcher.start()- color_num*23, matcher.start()+15 - color_num*23);
                spannableStringBuilder.delete(matcher.end()-23 - color_num*23, matcher.end()-15 - color_num*23);

                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
                spannableStringBuilder.setSpan(span, matcher.start() - color_num*23, matcher.end()-23 - color_num*23,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                color_num ++ ;
            }
        }

        //文字大小
        if(content.contains("[size=") && content.contains("[/size]"))
        {
            pattern = Pattern.compile("\\[size=(\\d)\\]([\\s\\S]*?)\\[/size\\]");
            matcher = pattern.matcher(spannableStringBuilder);

            //8表示的是[size=5] 的长度
            //7表示的是[/size]的长度
            //15是相加的长度
            int size_num = 0;
            while (matcher.find())
            {
                String size = matcher.group(1);
                Float size_f = Float.parseFloat(size) / 2;

                spannableStringBuilder.delete(matcher.start()- size_num*15, matcher.start()+8 - size_num*15);
                spannableStringBuilder.delete(matcher.end()-15 - size_num*15, matcher.end()-8 - size_num*15);

                RelativeSizeSpan span = new RelativeSizeSpan(size_f);
                spannableStringBuilder.setSpan(span, matcher.start() - size_num*15, matcher.end()-15 - size_num*15,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                size_num ++ ;
            }
        }

        //主动匹配超链接，即原味中加入了[url]标签
        if(content.contains("[url=") && content.contains("[/url]"))
        {
            pattern = Pattern.compile("\\[url=([^\\]]*?)\\]([\\s\\S]*?)\\[/url\\]");
            matcher = pattern.matcher(spannableStringBuilder);

            while (matcher.find())
            {
                String url = matcher.group(1);
                String text = matcher.group(2);
                ClickableTextSpan span = new ClickableTextSpan(context, text, url);
                spannableStringBuilder.setSpan(span, matcher.start(2), matcher.end(2), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.delete(matcher.start(1)-5, matcher.end(1)+1);
                spannableStringBuilder.delete(matcher.end(2) - url.length() - 6, matcher.end(2) - url.length());
            }
        }


        //被动匹配超链接，即原文中是纯txt格式
//        if(spannableStringBuilder.toString().contains("bbs.byr.cn"))
//        {
            pattern = Pattern.compile("((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+");
            matcher = pattern.matcher(spannableStringBuilder);

            while(matcher.find())
            {
                String url = matcher.group();
                ClickableTextSpan span = new ClickableTextSpan(context, url, url);
                spannableStringBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
//        }

        //匹配手机号码
        pattern = Pattern.compile("0?(13|14|15|18)[0-9]{9}");
        matcher = pattern.matcher(spannableStringBuilder);

        while(matcher.find())
        {
            String url = matcher.group();
            ClickableTextSpan span = new ClickableTextSpan(context, url, url);
            spannableStringBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //匹配电话号码
        pattern = Pattern.compile("[0-9-()（）]{7,18}");
        matcher = pattern.matcher(spannableStringBuilder);

        while(matcher.find())
        {
            String url = matcher.group();
            ClickableTextSpan span = new ClickableTextSpan(context, url, url);
            spannableStringBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //匹配邮件地址
        pattern = Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
        matcher = pattern.matcher(spannableStringBuilder);

        while(matcher.find())
        {
            String url = matcher.group();
            ClickableTextSpan span = new ClickableTextSpan(context, url, url);
            spannableStringBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }



        //网络图片
        if(content.contains("[img=") && content.contains("[/img]"))
        {
            pattern = Pattern.compile("\\[img=([^\\]]*?)\\]\\[/img\\]");
            final Matcher m = pattern.matcher(content);

            singleTaskExecutor.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    while (m.find())
                    {
                        final String url = m.group(1);
                        Bitmap bitmap = null;
                        try
                        {
                            bitmap = Picasso.with(context).load(url).get();

                            //对于站外图片，采用缓存的方式缓存一段时间，这样响应点击事件的时候如果缓存还在的话就不用
                            //消耗网络来获取图片了
                            ACache.get(context).put(url, bitmap, 2*60);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new Event.Bitmap_Outside(article_index, url, bitmap));
                    }
                }
            });
        }

        //表情
        if (content.contains("[em"))
        {
            pattern = Pattern.compile("\\[(em[abc]?\\d+)\\]");
            matcher = pattern.matcher(spannableStringBuilder);
            String emoji, emoji_filename;
            MatchResult match_result;
            GifDrawable gif = null;
            while (matcher.find())
            {
                match_result = matcher.toMatchResult();
                emoji = match_result.group(1);
                emoji_filename = "emoji/" + emoji + ".gif";

                try
                {
                    gif = new GifDrawable(context.getResources().getAssets(), emoji_filename);
                    gif.setBounds(0, 0, gif.getIntrinsicWidth() * 2, gif.getIntrinsicHeight() * 2);
                    gif.setCallback(new GifCallback(textView));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                spannableStringBuilder.setSpan(new ImageSpan(gif, ImageSpan.ALIGN_BASELINE), matcher.start(),
                        matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        //包含附件
        if(attachment.getRemain_count() < 20 )
        {
            singleTaskExecutor.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    Attachment._file[] attachmentFiles = attachment.getFile();
                    List<Bitmap> attachment_images = new ArrayList<>();
                    List<String> urls = new ArrayList<>();
                    List<Float> sizes = new ArrayList<>();
                    for (Attachment._file attachmentFile : attachmentFiles)
                    {
                        if(attachmentFile.getName().endsWith(".png") || attachmentFile.getName().endsWith(".PNG")
                                || attachmentFile.getName().endsWith(".jpg") || attachmentFile.getName().endsWith(".JPG")
                                || attachmentFile.getName().endsWith(".gif") || attachmentFile.getName().endsWith(".GIF")
                                || attachmentFile.getName().endsWith(".jpeg")|| attachmentFile.getName().endsWith(".JPEG"))
                        {
                            String size_str = attachmentFile.getSize();
                            size_str = size_str.replace("KB", "");
                            float size = -1.0f;
                            try
                            {
                                size = Float.parseFloat(size_str);
                            }
                            catch (NumberFormatException e)
                            {
                                e.printStackTrace();
                            }

                            String img_url;
                            Bitmap bitmap;

                            int zoom = (int)(size / 100.0f) + 1;

                            img_url = attachmentFile.getThumbnail_middle() + BYR_BBS_API.returnFormat + BYR_BBS_API.appkey;
                            bitmap = PicassoHelper.getPicassoHelper().getBitmap(img_url, zoom);


//                            if(size > 50.0f)
//                            {
//                                //图片大于50KB的时候使用小缩略图，防止发生OOM
//                                img_url = attachmentFile.getThumbnail_small() + BYR_BBS_API.returnFormat + BYR_BBS_API.appkey;
//                                bitmap = PicassoHelper.getPicassoHelper().getBitmap(img_url, 2);
//                            }
//                            else
//                            {
//                                img_url = attachmentFile.getThumbnail_middle() + BYR_BBS_API.returnFormat + BYR_BBS_API.appkey;
//                                bitmap = PicassoHelper.getPicassoHelper().getBitmap(img_url, 1);
//                            }


                            sizes.add(size);
                            attachment_images.add(bitmap);
                            urls.add(attachmentFile.getUrl());
                        }
                    }
                    if(attachment_images.size() > 0)
                    {
                        EventBus.getDefault().post(new Event.Attachment_Images(article_index, attachment_images, urls, sizes));
                    }
                }
            });
        }

        return spannableStringBuilder;
    }


    /**
     * 展示回复内容中的[img=************][/img]标签
     * @param content 内容
     * @param bitmap 图片
     * @param tv_width textview宽度
     * @param url 链接
     * @param context context
     * @return 设置了span的内容
     */
    public static SpannableStringBuilder Show_Outside_Images(SpannableStringBuilder content,
                                                             Bitmap bitmap, int tv_width,
                                                             String url, Context context)
    {
        String str = content.toString();
        int index = str.indexOf(url);
        int start = index - 5;
        int end = index + url.length() + 7;

        CenteredImageSpan imageSpan = new CenteredImageSpan(context, bitmap,
                ImageSpan.ALIGN_BOTTOM, tv_width, url, 0);

        content.setSpan(imageSpan, start, end
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return content;
    }

    /**
     * 将附件图片通过 SpannableStringBuilder 显示
     * @param content 回复内容
     * @param images 图片
     * @return 回复内容
     */
    public static SpannableStringBuilder Show_Attachments(SpannableStringBuilder content,
                                                          List<Bitmap> images, int tv_width,
                                                          List<String> urls, Context context,
                                                          List<Float> sizes)
    {
//        Context context = ContextApplication.getAppContext();
        Pattern pattern = Pattern.compile("(\\[upload=(\\d*)\\]\\[/upload\\])");
        Matcher matcher = pattern.matcher(content);

        int index = 0;
        int newline_num = 0; //插入的换行符的数量

        while (matcher.find())
        {
            try
            {
                String upload_index_str = matcher.group(2);
                int upload_index_int = Integer.parseInt(upload_index_str);
                CenteredImageSpan imageSpan = new CenteredImageSpan(context, images.get(upload_index_int-1),
                        ImageSpan.ALIGN_BOTTOM, tv_width, urls.get(upload_index_int-1), sizes.get(upload_index_int-1));

                index ++;

                //此处原本是准备在添加换行符之前判断是否已经有换行符的
                CharSequence charSequence;
                if(matcher.start() != 0)
                {
                    //是否一开头便是附件
                    charSequence = content.subSequence(matcher.start() + newline_num - 1, matcher.start() + newline_num);
                    if (!charSequence.toString().equals("\n"))
                    {
                        //原本没有换行，则添加两个换行符
                        content.insert(matcher.start() + newline_num, "\n\n");
                        newline_num += 2;
                    }
                    else
                    {
                        charSequence = content.subSequence(matcher.start() + newline_num - 2, matcher.start() + newline_num -1);
                        if (!charSequence.toString().equals("\n"))
                        {
                            //原本至有一个换行，则添加一个换行符；若原本有两个换行，则不动
                            content.insert(matcher.start() + newline_num, "\n");
                            newline_num += 1;
                        }
                    }
                }
                else
                {
                    //附件处于第一行，则添加一个换行符
                    content.insert(matcher.start() + newline_num, "\n");
                    newline_num += 1;
                }

                content.setSpan(imageSpan, matcher.start()+newline_num, matcher.end()+newline_num
                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if(matcher.end()+newline_num+1 < content.length())
                {
                    charSequence = content.subSequence(matcher.end() + newline_num, matcher.end() + newline_num + 1);
                    if (!charSequence.toString().equals("\n"))
                    {
                        //原本没有换行，则添加两个换行符
                        content.insert(matcher.end() + newline_num , "\n\n");
                        newline_num += 2;
                    }
                    else
                    {
                        charSequence = content.subSequence(matcher.end() + newline_num + 1, matcher.end() + newline_num + 2);
                        if (!charSequence.toString().equals("\n"))
                        {
                            //原本只有一个换行，则添加一个换行符；若有两个换行，则不动
                            content.insert(matcher.end() + newline_num, "\n");
                            newline_num += 1;
                        }
                    }
                }
                else
                {
                    //原本有换行，则添加一个换行符
                    content.insert(matcher.end() + newline_num , "\n");
                    newline_num += 1;
                }


            }
            catch (IndexOutOfBoundsException e)
            {
                //针对极少数content中包含的[upload]数量多于附件数量的情况下，该情况很少见且不正常，但是为了正常显示，需要规避这个Bug
                content.replace(matcher.start()+newline_num, matcher.end()+newline_num, "");
                while(matcher.find())
                {
                    content.replace(matcher.start()+newline_num, matcher.end()+newline_num, "");
                }
                return content;
            }
        }

        if(index < images.size())
        {
            //针对极少数content中包含的[upload]数量少于附件数量的情况下，该情况很少见且不正常，将附件中的内容展现在末尾
            while(images.size() > index)
            {
                CenteredImageSpan imageSpan = new CenteredImageSpan(context, images.get(index),
                        ImageSpan.ALIGN_BASELINE, tv_width, urls.get(index), sizes.get(index));
                //先换行，然后使用一个空格占位，接着使用图片代替刚才占位的空格，然后再换行
                content.insert(content.length(), "\n ");
                content.setSpan(imageSpan, content.length()-1, content.length()
                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.insert(content.length(), "\n");

                index++;
            }
        }

        return content;
    }

}
