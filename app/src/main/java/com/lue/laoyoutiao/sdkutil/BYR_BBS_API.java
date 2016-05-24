package com.lue.laoyoutiao.sdkutil;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.AttachmentHelper;
import com.lue.laoyoutiao.metadata.Attachment;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.metadata.Section;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.lue.laoyoutiao.view.span.CenteredImageSpan;
import com.lue.laoyoutiao.view.span.ClickableTextSpan;
import com.lue.laoyoutiao.view.span.GifCallback;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Lue on 2015/12/23.
 */
public class BYR_BBS_API
{
    private Context context;
    private static final String TAG = "BYR_BBS_API";

    //北邮人论坛网址
    private static final String host = "http://api.byr.cn";

    //返回格式以及appkey
    public static final String returnFormat = ".json?";
    public static final String appkey = "&appkey=" + "7a282a1a9de5b450";

    //由用户名及密码组成的认证信息
    private static String auth;


    /************************************* 主要用到的API***********************************/

    //用户接口
    public static final String STRING_USER = "user";
    public static final String STRING_LOGIN = "login";
    public static final String STRING_LOGOUT = "logout";

    //widget接口
    public static final String STRING_WIDGET = "widget";
    public static final String STRING_TOPTEN = "topten";

    //分区接口
    public static final String STRING_SECTION = "section";

    //版面接口
    public static final String STRING_BOARD = "board";

    //收藏夹接口
    public static final String STRING_FAVORITE = "favorite";
    public static final String STRING_FAVORITE_ADD = "add";
    public static final String STRING_FAVORITE_DELETE = "delete";
    public static final String STRING_FAVORITE_TOP_LEVEL = "0";

    //文章接口
    public static final String STRING_ARTICLE = "article";
    public static final String STRING_THREADS = "threads";

    //老邮条在本地的储存目录
    public static final String ROOT_FOLDER = "/LaoYouTiao";
    public final static String LOCAL_FILEPATH = Environment.getExternalStorageDirectory().getPath() + ROOT_FOLDER;
    public static final String MY_INFO_FOLDER = "/my_user_info";
    public static final String MY_FACE_NAME = "/my_face.png";
    public static final String IMAGES_SAVED = "/images_saved";

    /***********************************************************************************************/

    private static final String MINUTES_AGO = "分钟前";
    private static final String HOUR = "小时前";
    private static final String DATE = "日";
    private static final String JUST_NOW = "刚刚";
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //本地 SharedPreferences
    private SharedPreferences My_SharedPreferences;
    private SharedPreferences.Editor editor;

    //保存所有分区信息， key 为 Section.getName
    public static final Map<String, Section> All_Sections = new Hashtable<>();

    //保存所有的版面信息, key 为 Board.getDescription
    public static final Map<String, Board> All_Boards = new Hashtable<>();

    //保存所有根分区信息， key 为 Section.getName
    public static final List<Section> ROOT_SECTIONS = new ArrayList<>();

    public static final Hashtable<String, Board> Favorite_Boards = new Hashtable<>();


    //是否所有分区及版面信息均以获取完毕，用以提示显示分区目录
    public static boolean Is_GetSections_Finished = false;


    //BYR_BBS_API使用单例模式，该类在JVM中仅允许创建一个实例
    //在没有使用单例模式时，发现因为存在很对对象，在EventBus仅发布一次事件时，会导致订阅事件函数被多次触发，但实际上只需要触发一次即可
    private volatile static BYR_BBS_API m_byr_bbs_api;

    //构造函数为private
    private BYR_BBS_API()
    {
        My_SharedPreferences = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
        editor = My_SharedPreferences.edit();

        String username = My_SharedPreferences.getString("username", "guset");
        String password = My_SharedPreferences.getString("password", "");
        setAuth(username, password);

        editor.apply();

        context = ContextApplication.getAppContext();

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    //获取该类的唯一实例，注意需要使用双重校验锁检测，这样可以线程同步
    public static BYR_BBS_API getM_byr_bbs_api()
    {
        if (m_byr_bbs_api == null)
        {
            synchronized (BYR_BBS_API.class)
            {
                if (m_byr_bbs_api == null)
                {
                    m_byr_bbs_api = new BYR_BBS_API();
                }
            }
        }
        return m_byr_bbs_api;
    }


    /**
     * 根据用户名和密码设置认证信息，使用Base64进行编码
     * @param username
     * @param password
     */
    public void setAuth(String username, String password)
    {
        byte[] encodeauth = (username + ":" + password).getBytes();
        auth = Base64.encodeToString(encodeauth, Base64.NO_WRAP);
    }

    public String getAuth()
    {
        return auth;
    }


    /**
     * 构建URL
     * @param strings
     * @return 构建好的URL
     * Example: param   : widget topten
     * result  : "/widget/topten"
     * return  : "http://api.byr.cn/widget/topten.json?&appkey=7a282a1a9de5b450"
     */
    public static String buildUrl(String... strings)
    {
        String result = "";
        for (String s : strings)
        {
            result = result + "/" + s;
        }
        return host + result + returnFormat + appkey;
    }

    /**
     *
     * @param params HTTP GET 参数
     * @param strings 构建url时必要的参数
     * @return 构建好的URL
     * Example: Map: key:page, value:2 ; strings: widget topten
     * return  : "http://api.byr.cn/widget/topten.json?&page=2&appkey=7a282a1a9de5b450"
     */
    public static String buildGETUrl(HashMap<String, String> params, String... strings)
    {
        String result = "";
        for (String s : strings)
        {
            result = result + "/" + s;
        }
        String string_params = "";
        for(String key : params.keySet())
        {
            string_params = string_params + "&" + key + "=" + params.get(key);
        }
        result = host + result + returnFormat + string_params + appkey;

        return result;
    }


    /**
     * 保存图片到本地
     * @param image_name 图片名称，一般以图片的url代替
     * @param bitmap 图片
     * @return 成功则返回true，否则返回false
     */
    public static boolean saveImage(String image_name, Bitmap bitmap)
    {
        //创建本地储存文件夹及对应文件
        String root_dic = LOCAL_FILEPATH;
        File dirFile = new File(root_dic + IMAGES_SAVED);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File file = new File(dirFile + image_name);

        try
        {
            //将bitmap保存到本地文件中
            FileOutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();
            fout.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 响应 SectionHelper 发布的所有根分区信息
     *
     * @param Root_Sections
     */

    public void onEventBackgroundThread(final Event.All_Root_Sections Root_Sections)
    {

        try
        {
            for (Section section : Root_Sections.getSections())
            {
                getSectionsAndBoards(section.getName());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 获取所有分区和版面的信息，并保存
     * @param section_name 分区名称
     * @throws IOException
     */
    public void getSectionsAndBoards(final String section_name) throws IOException
    {
        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_SECTION, section_name);

        String last_section_name = ROOT_SECTIONS.get(ROOT_SECTIONS.size() -1).getName();

        if( section_name.equals(last_section_name))
        {
            Is_GetSections_Finished = true;

            //向BoardFragment发送消息，告诉它可以取消加载页面的显示
            EventBus.getDefault().post(new Event.Get_Sections_Finished(true));
        }

        Response response = OkHttpHelper.getM_OkHttpHelper().getExecute(url);
        String response_result = response.body().string();

        //版面所属类别, 原接口中为class，但class为保留字，因此使用boardclass
        response_result = response_result.replace("\"class\"", "\"boardclass\"");

        JSONObject jsonObject = JSON.parseObject(response_result);

        //储存分区信息
        Section section = new Gson().fromJson(response_result, new TypeToken<Section>()
        {
        }.getType());

        if (section.getParent() != null)
        {
            Section parent_section = BYR_BBS_API.All_Sections.get(section.getParent());
            parent_section.setSub_section_names(section.getName());
            parent_section.setSub_section_descriptions(section.getDescription());
            BYR_BBS_API.All_Sections.put(parent_section.getName(), parent_section);

        }
        BYR_BBS_API.All_Sections.put(section.getName(), section);

        editor.putString(section.getName(), section.getDescription());

        //储存版面信息
        String Boards_String = jsonObject.getString("board");
        List<Board> boards = new Gson().fromJson(Boards_String, new TypeToken<List<Board>>()
        {
        }.getType());
        for (Board board : boards)
        {
            BYR_BBS_API.All_Boards.put(board.getDescription(), board);

            editor.putString(board.getName(), board.getDescription());

            Section parent_section = BYR_BBS_API.All_Sections.get(section.getName());
            parent_section.setBoard_names(board.getName());
            parent_section.setBoard_descriptions(board.getDescription());
            BYR_BBS_API.All_Sections.put(parent_section.getName(), parent_section);

            if (section.getParent() == null)
            {
                ROOT_SECTIONS.get(Integer.parseInt(section.getName())).setBoard_names(board.getName());
                ROOT_SECTIONS.get(Integer.parseInt(section.getName())).setBoard_descriptions(board.getDescription());
            }
        }

        //判断是否有子分区，若有，则查找子分区信息
        String Sub_Section_String = jsonObject.getString("sub_section");
        if (Sub_Section_String.length() > 2)
        {
            Sub_Section_String = Sub_Section_String.substring(Sub_Section_String.indexOf("[") + 1, Sub_Section_String.lastIndexOf("]"));
            String Sub_Section_Name[] = Sub_Section_String.split(",");
            for (int i = 0; i < Sub_Section_Name.length; i++)
            {
                Sub_Section_Name[i] = Sub_Section_Name[i].substring(Sub_Section_Name[i].indexOf("\"") + 1, Sub_Section_Name[i].lastIndexOf("\""));
                getSectionsAndBoards(Sub_Section_Name[i]);

                ROOT_SECTIONS.get(Integer.parseInt(section.getName())).setSub_section_names(Sub_Section_Name[i]);
                ROOT_SECTIONS.get(Integer.parseInt(section.getName())).setSub_section_descriptions(All_Sections.get(Sub_Section_Name[i]).getDescription());
            }
        }
        editor.commit();
    }
    /**
     * 判断当前网络是否可用
     * @return 是否可用
     */
    public static boolean isNetWorkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) ContextApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if( info != null && info.isConnected() )
            {
                //当前网络是连接的
                if(info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 将服务器返回的unixtimestamp时间戳转化成可读的日期标识
     * @param timestamp_int  unixtimestamp时间戳
     * @param if_whore_time  是否返回完整时间, false代表返回的是相对当前时刻的时间, true代表返回的是绝对时间
     * @return 可读的日期标识
     */
    public static String timeStamptoDate(int timestamp_int, boolean if_whore_time)
    {
        String result;

        if(!if_whore_time)
        {
            long current_time = new Date().getTime();
            current_time = current_time / 1000;
            long time_defference = current_time - timestamp_int; //时间差
            if (time_defference <= 60)
            {
                return JUST_NOW;
            }
            else if (time_defference > 60 && time_defference <= 3600)
            {
                //时间小于一小时
                int minutes = (int) time_defference / 60;
                result = minutes + MINUTES_AGO;
                return result;
            }
            else if (time_defference > 3600 && time_defference < 24 * 3600)
            {
                //时间小于一天
                int total_minutes = (int) time_defference / 60; //总分钟数
                int hours = total_minutes / 60;                 //小时数
                result = hours + HOUR;
                return result;
            }
            else
            {
                long timestamp_long = (long) timestamp_int * 1000;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
                String date = simpleDateFormat.format(new Date(timestamp_long));
                date = date.substring(5, 10) + DATE;
                return date;
            }
        }
        else
        {
            long timestamp_long = (long) timestamp_int * 1000;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
            String date = simpleDateFormat.format(new Date(timestamp_long));
            return date;
        }
    }


    /**
     * 将文章内容通过正则表达式等解析成相应的类型
     * @param article_content 内容
     * @return 内容、引用内容、使用的APP
     */
    public static String[] SeparateContent(String article_content)
    {
        Pattern pattern;
        Matcher matcher;

        while (article_content.endsWith("-") || article_content.endsWith("\n"))
        {
            article_content = article_content.substring(0, article_content.length() -1);
        }

        //分离出用户使用的客户端程序
        String my_app = null;
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

            //"<a href=\"http://guiyou.wangx.in\"><u>发自「贵邮」</u></a>"
            my_app = "<a href=\"" + url + "\"><u>" + my_app  + "</u></a>";
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
        String array[] = new String[3];
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

//        //取消字号
//        my_content = my_content.replaceAll("\\[size=[^\\]]*?\\][\\s\\S]*?", "");
//        my_content = my_content.replaceAll("\\[/size\\]", "");

        array[0] = my_content;
        array[1] = my_reference;
        array[2] = my_app;
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
        SpannableStringBuilder spannableString = new SpannableStringBuilder(content);

        //文字加粗
        if(content.contains("[b]") && content.contains("[/b]"))
        {
            pattern = Pattern.compile("\\[b\\]([\\s\\S]*?)\\[/b\\]");
            matcher = pattern.matcher(spannableString);

            //3表示的是[b] 的长度
            //4表示的是[/b]的长度
            //7是相加的长度
            int bold_num = 0;
            while (matcher.find())
            {
                spannableString.delete(matcher.start()- bold_num*7, matcher.start()+3 - bold_num*7);
                spannableString.delete(matcher.end()-7 - bold_num*7, matcher.end()-3 - bold_num*7);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), matcher.start() - bold_num*7, matcher.end()-7 - bold_num*7,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bold_num ++ ;
            }
        }

        //文字颜色
        if(content.contains("[color=") && content.contains("[/color]"))
        {
            pattern = Pattern.compile("\\[color=([^\\]]*?)\\]([\\s\\S]*?)\\[/color\\]");
            matcher = pattern.matcher(spannableString);

            //15表示的是[color=#XXXXXX] 的长度
            //8表示的是[/color]的长度
            //23是相加的长度
            int color_num = 0;
            while (matcher.find())
            {
                String color = matcher.group(1);
                spannableString.delete(matcher.start()- color_num*23, matcher.start()+15 - color_num*23);
                spannableString.delete(matcher.end()-23 - color_num*23, matcher.end()-15 - color_num*23);

                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
                spannableString.setSpan(span, matcher.start() - color_num*23, matcher.end()-23 - color_num*23,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                color_num ++ ;
            }
        }

        //文字大小
        if(content.contains("[size=") && content.contains("[/size]"))
        {
            pattern = Pattern.compile("\\[size=(\\d)\\]([\\s\\S]*?)\\[/size\\]");
            matcher = pattern.matcher(spannableString);

            //8表示的是[size=5] 的长度
            //7表示的是[/size]的长度
            //15是相加的长度
            int size_num = 0;
            while (matcher.find())
            {
                String size = matcher.group(1);
                Float size_f = Float.parseFloat(size) / 2;

                spannableString.delete(matcher.start()- size_num*15, matcher.start()+8 - size_num*15);
                spannableString.delete(matcher.end()-15 - size_num*15, matcher.end()-8 - size_num*15);

                RelativeSizeSpan span = new RelativeSizeSpan(size_f);
                spannableString.setSpan(span, matcher.start() - size_num*15, matcher.end()-15 - size_num*15,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                size_num ++ ;
            }
        }

        //匹配论坛超链接
        if(content.contains("http://bbs.byr.cn"))
        {
            pattern = Pattern.compile("http://bbs.byr.cn[^\\s]+");
            matcher = pattern.matcher(spannableString);

            while(matcher.find())
            {
                String url = matcher.group();
                ClickableTextSpan span = new ClickableTextSpan(context, url);
                spannableString.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        //网络图片
        if(content.contains("[img=") && content.contains("[/img]"))
        {
            pattern = Pattern.compile("\\[img=([^\\]]*?)\\]\\[/img\\]");
            final Matcher m = pattern.matcher(content);

            new Thread()
            {
                public void run()
                {
                    while (m.find())
                    {
                        final String url = m.group(1);
                        Bitmap bitmap = null;
                        try
                        {
                            bitmap = Picasso.with(context).load(url).get();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new Event.Bitmap_Outside(article_index, url, bitmap));
                    }
                }
            }.start();
        }

        //表情
        if (content.contains("[em"))
        {
            pattern = Pattern.compile("\\[(em[abc]?\\d+)\\]");
            matcher = pattern.matcher(spannableString);
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

                spannableString.setSpan(new ImageSpan(gif, ImageSpan.ALIGN_BASELINE), matcher.start(),
                        matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        //包含附件
        if(attachment.getRemain_count() < 20 )
        {
            final AttachmentHelper attachmentHelper = new AttachmentHelper();
            new Thread()
            {
                public void run()
                {
                    Attachment._file[] attachmentFiles = attachment.getFile();
                    List<Bitmap> attachment_images = new ArrayList<>();
                    List<String> urls = new ArrayList<>();
                    for (Attachment._file attachmentFile : attachmentFiles)
                    {
                        if(attachmentFile.getName().endsWith(".png") || attachmentFile.getName().endsWith(".jpg")
                                || attachmentFile.getName().endsWith(".gif") || attachmentFile.getName().endsWith(".jpeg"))
                        {
                            final String img_url = attachmentFile.getThumbnail_middle() + returnFormat + appkey;
                            Bitmap bitmap = attachmentHelper.get_Attachment_Image(img_url);
                            attachment_images.add(bitmap);
                            urls.add(attachmentFile.getUrl());
                        }
                    }
                    if(attachment_images.size() > 0)
                    {
                        EventBus.getDefault().post(new Event.Attachment_Images(article_index, attachment_images, urls));
                    }
                }
            }.start();
        }

        return spannableString;
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
                ImageSpan.ALIGN_BOTTOM, tv_width, url);

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
                                                          List<String> urls, Context context)
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
                        ImageSpan.ALIGN_BOTTOM, tv_width, urls.get(upload_index_int-1));

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
                        ImageSpan.ALIGN_BASELINE, tv_width, urls.get(index));
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

