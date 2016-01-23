package com.lue.laoyoutiao.eventtype;

import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.metadata.Section;
import com.lue.laoyoutiao.metadata.User;

import java.util.List;

/**
 * Created by Lue on 2016/1/16.
 */
public class Event
{
    /**
     * 封装好的十大热门话题事件
     */
    public static class Topten_ArticleList
    {
        private List<Article> topten_list;

        public Topten_ArticleList(List<Article> topten_list)
        {
            this.topten_list = topten_list;
        }

        public List<Article> getTopten_list()
        {
            return topten_list;
        }
    }

    /**
     * 封装好的登录用户详细信息事件
     */
    public static class My_User_Info
    {
        private User me;

        public My_User_Info(User me)
        {
            this.me = me;
        }

        public User getMe()
        {
            return me;
        }
    }

    /**
     * 封装好的所有根分区事件
     */
    public static class All_Root_Sections
    {
        private List<Section> sections;

        public All_Root_Sections(List<Section> sections)
        {
            this.sections = sections;
        }

        public List<Section> getSections()
        {
            return sections;
        }
    }

    /**
     * 封装好的所有根分区事件
     */
    public static class My_Favorite_Boards
    {
        private List<Board> boards;

        public My_Favorite_Boards(List<Board> boards)
        {
            this.boards = boards;
        }

        public List<Board> getBoards()
        {
            return boards;
        }
    }
}
