package com.lue.laoyoutiao.eventtype;

import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.metadata.User;

import java.util.List;

/**
 * Created by Lue on 2016/1/16.
 */
public class Event
{
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
}
