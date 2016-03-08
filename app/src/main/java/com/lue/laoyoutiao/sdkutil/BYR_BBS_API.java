package com.lue.laoyoutiao.sdkutil;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.metadata.Section;
import com.lue.laoyoutiao.network.OkHttpHelper;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2015/12/23.
 */
public class BYR_BBS_API
{
    private static final String TAG = "BYR_BBS_API";

    //北邮人论坛网址
    private static final String host = "http://api.byr.cn";

    //返回格式以及appkey
    private static final String returnFormat = ".json?";
    private static final String appkey = "&appkey=" + "7a282a1a9de5b450";

    //由用户名及密码组成的认证信息
    private static String auth;

    /**
     * 主要用到的API
     **/

    //用户接口
    public static final String STRING_USER = "user";
    public static final String STRING_LOGIN = "login";
    public static final String STRING_LOGOUT = "logout";

    //widget接口
    public static final String STRING_WIDGET = "widget";
    public static final String STRING_TOPTEN = "topten";

    //分区接口
    public static final String STRING_SECTION = "section";

    //收藏夹接口
    public static final String STRING_FAVORITE = "favorite";

    //老邮条在本地的储存目录
    public static final String ROOT_FOLDER = "/LaoYouTiao";
    public final static String LOCAL_FILEPATH = Environment.getExternalStorageDirectory().getPath() + ROOT_FOLDER;
    public static final String MY_INFO_FOLDER = "/my_user_info";
    public static final String MY_FACE_NAME = "/my_face.png";

    //本地 SharedPreferences
    private SharedPreferences My_SharedPreferences;
    private SharedPreferences.Editor editor;

    public static final Map<String, Section> All_Sections = new HashMap<>();

    public static final Map<String, Board> All_Boards = new HashMap<>();

    public static final List<String> Root_Sections_Name = new ArrayList<>();


    public BYR_BBS_API()
    {
        My_SharedPreferences = ContextApplication.getAppContext().getSharedPreferences("My_SharePreference", Context.MODE_PRIVATE);
        editor = My_SharedPreferences.edit();

        String username = My_SharedPreferences.getString("username", "guset");
        String password = My_SharedPreferences.getString("password", "");
        setAuth(username, password);

        //注册EventBus
        EventBus.getDefault().register(this);
    }



    public static Section getSectionfromRoot(int root_section_name_position)
    {
        return All_Sections.get(Root_Sections_Name.get(root_section_name_position));
    }



    /**
     * 根据用户名和密码设置认证信息，使用Base64进行编码
     *
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
     *
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
     * 响应 SectionHelper 发布的所有根分区信息
     *
     * @param Root_Sections
     */
    public void onEventMainThread(final Event.All_Root_Sections Root_Sections)
    {
        new Thread()
        {
            public void run()
            {
                for (Section section : Root_Sections.getSections())
                {
                    Root_Sections_Name.add(section.getName());

                    if (section.getIs_root())
                    {
                        try
                        {
                            getSectionsAndBoards(section.getName());
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();


//        for(Section section : Root_Sections.getSections())
//        {
//            if(section.getIs_root())
//            {
//                getSectionsAndBoards(section.getName());
//            }
//        }
    }


    public void getSectionsAndBoards(final String section_name) throws IOException
    {
        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_SECTION, section_name);

        Response response = new OkHttpHelper().getExecute(url);
        String response_result = response.body().string();

        //版面所属类别, 原接口中为class，但class为保留字，因此使用boardclass
        response_result = response_result.replace("\"class\"", "\"boardclass\"");

        JSONObject jsonObject = JSON.parseObject(response_result);

        //储存分区信息
        Section section = new Gson().fromJson(response_result, new TypeToken<Section>() {}.getType());

        if (section.getParent() != null)
        {
            Section parent_section = BYR_BBS_API.All_Sections.get(section.getParent());
            parent_section.setSub_section_names(section.getName());
            BYR_BBS_API.All_Sections.put(parent_section.getName(), parent_section);
        }
        BYR_BBS_API.All_Sections.put(section.getName(), section);
        editor.putString(section.getName(), section.getDescription());

        //储存版面信息
        String Boards_String = jsonObject.getString("board");
        List<Board> boards = new Gson().fromJson(Boards_String, new TypeToken<List<Board>>() {}.getType());
        for (Board board : boards)
        {
            BYR_BBS_API.All_Boards.put(board.getName(), board);
            editor.putString(board.getName(), board.getDescription());


            Section parent_section = BYR_BBS_API.All_Sections.get(section.getName());
            parent_section.setBoards_names(board.getName());
            BYR_BBS_API.All_Sections.put(parent_section.getName(), parent_section);
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
            }
        }

        editor.commit();
    }
}

/**
 * 给定根分区名称，获取该根分区目录下所有的子分区以及所有版面，并将分区“名称-描述”和版面“名称-描述”键值对保存到sharedprefrence中
 *
 * @param section_name
 */
//    public void getSectionsAndBoards(final String section_name)
//    {
//        final String url = BYR_BBS_API.buildUrl(BYR_BBS_API.STRING_SECTION, section_name);
//
//        new Thread()
//        {
//            public void run()
//            {
//                try
//                {
//                    Response response = new OkHttpHelper().getExecute(url);
//                    String response_result = response.body().string();
//
//                    //版面所属类别, 原接口中为class，但class为保留字，因此使用boardclass
//                    response_result = response_result.replace("\"class\"","\"boardclass\"");
//
//                    JSONObject jsonObject = JSON.parseObject(response_result);
//
//                    //储存分区信息
//                    Section section = new Gson().fromJson(response_result, new TypeToken<Section>(){}.getType());
//
//                    if(section.getParent() != null)
//                    {
//                        Section parent_section = BYR_BBS_API.All_Sections.get(section.getParent());
//                        parent_section.setSub_section_names(section.getName());
//                        BYR_BBS_API.All_Sections.put(parent_section.getName(), parent_section);
////                        BYR_BBS_API.All_Sections.put(section.getParent(), parent_section);
//                    }
//                    BYR_BBS_API.All_Sections.put(section.getName(), section);
//                    editor.putString(section.getName(), section.getDescription());
//
//                    //储存版面信息
//                    String Boards_String = jsonObject.getString("board");
//                    List<Board> boards = new Gson().fromJson(Boards_String, new TypeToken<List<Board>>(){}.getType());
//                    for(Board board : boards)
//                    {
//                        BYR_BBS_API.All_Boards.put(board.getName(), board);
//                        editor.putString(board.getName(), board.getDescription());
//
//
//
//                        Section parent_section = BYR_BBS_API.All_Sections.get(section.getName());
//                        parent_section.setBoards_names(board.getName());
//                        BYR_BBS_API.All_Sections.put(parent_section.getName(), parent_section);
//                    }
//
//
//                    //判断是否有子分区，若有，则查找子分区信息
//                    String Sub_Section_String = jsonObject.getString("sub_section");
//                    if(Sub_Section_String.length() > 2)
//                    {
//                        Sub_Section_String = Sub_Section_String.substring(Sub_Section_String.indexOf("[")+1, Sub_Section_String.lastIndexOf("]"));
//                        String Sub_Section_Name[] = Sub_Section_String.split(",");
//                        for(int i=0; i<Sub_Section_Name.length; i++)
//                        {
//                            Sub_Section_Name[i] = Sub_Section_Name[i].substring(Sub_Section_Name[i].indexOf("\"")+1, Sub_Section_Name[i].lastIndexOf("\""));
//                            getSectionsAndBoards(Sub_Section_Name[i]);
//                        }
//                    }
//
//                    editor.commit();
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
