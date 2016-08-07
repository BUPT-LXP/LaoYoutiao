package com.lue.laoyoutiao.eventtype;

import android.graphics.Bitmap;

import com.lue.laoyoutiao.metadata.Article;
import com.lue.laoyoutiao.metadata.Board;
import com.lue.laoyoutiao.metadata.Section;
import com.lue.laoyoutiao.metadata.User;

import java.util.ArrayList;
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
        private boolean isLocal = false;

        public Topten_ArticleList(List<Article> topten_list, boolean isLocal)
        {
            this.topten_list = topten_list;
            this.isLocal = isLocal;
        }

        public List<Article> getTopten_list()
        {
            return topten_list;
        }

        public boolean isLocal()
        {
            return isLocal;
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

    /**
     * BYR_BBS_API 在获取所有分区信息之后，向BoardFragment发送消息，告诉它可以取消加载页面的显示
     */
    public static class Get_Sections_Finished
    {
        private boolean is_finished = false;

        public Get_Sections_Finished(boolean is_finished)
        {
            this.is_finished = is_finished;
        }
    }

    /**
     * 提交收藏版面成功
     */
    public static class Post_Favorite_Finished
    {
        private String description = null;
        private boolean is_favorite = false;

        public Post_Favorite_Finished(String name, boolean is_favorite)
        {
            this.description = name;
            this.is_favorite = is_favorite;
        }

        public String getDescription()
        {
            return description;
        }
        public boolean getIs_favorite()
        {
            return is_favorite;
        }
    }

    /**
     * 指定版面的文章列表
     */
    public static class Specified_Board_Articles
    {
        private List<Article> articles;

        public Specified_Board_Articles(List<Article> articles)
        {
            this.articles = articles;
        }

        public List<Article> getArticles()
        {
            return articles;
        }
    }

    /**
     * 指定标题的文章及其回复
     */
    public static class Read_Articles_Info
    {
        private List<Article> articles;
        private List<String> user_faces = new ArrayList<>();
        private int reply_count = -1;

        public Read_Articles_Info(List<Article> articles, List<String> faces, int reply_count)
        {
            this.articles = articles;
            this.user_faces = faces;
            this.reply_count = reply_count;
        }

        public List<Article> getArticles()
        {
            return articles;
        }

        public List<String> getUser_faces()
        {
            return user_faces;
        }

        public int getReply_count()
        {
            return reply_count;
        }
    }

    /**
     * 附件中的图片信息，图片大小：中等
     */
    public static class Attachment_Images
    {
        private int article_index;
        private List<Bitmap> images = new ArrayList<>();
        private List<String> urls = new ArrayList<>();
        private List<Float> sizes = new ArrayList<>();

        public Attachment_Images(int article_index, List<Bitmap> images, List<String> urls, List<Float> sizes)
        {
            this.article_index = article_index;
            this.images = images;
            this.urls = urls;
            this.sizes = sizes;
        }

        public List<Bitmap> getImages()
        {
            return images;
        }

        public int getArticle_index()
        {
            return article_index;
        }

        public List<String> getUrls()
        {
            return urls;
        }

        public List<Float> getSizes()
        {
            return sizes;
        }
    }

    /**
     * 当使用本app打开新的链接时，通知其他的activity停止订阅EventBus
     */
    public static class Start_New
    {
        public Start_New() {}
    }

    /**
     * 附件及站外大图
     */
    public static class Bitmap_HD
    {
        private String url;
        private Bitmap image_hd;

        public Bitmap_HD(String url, Bitmap image_hd)
        {
            this.url = url;
            this.image_hd = image_hd;
        }


        public String getUrl()
        {
            return url;
        }

        public Bitmap getImage_hd()
        {
            return image_hd;
        }

    }

    /**
     * 站外图片
     */
    public static class Bitmap_Outside
    {
        private int article_index;
        private String url;
        private Bitmap image_hd;

        public Bitmap_Outside(int article_index, String url, Bitmap image_hd)
        {
            this.article_index = article_index;
            this.url = url;
            this.image_hd = image_hd;
        }

        public String getUrl()
        {
            return url;
        }

        public Bitmap getImage_hd()
        {
            return image_hd;
        }

        public int getArticle_index()
        {
            return article_index;
        }

    }

    /**
     * 站内链接错误，指定的文章不存在或链接错误
     */
    public static class Article_Not_Exist
    {
        private String error_info;
        public Article_Not_Exist(String error_info)
        {
            this.error_info = error_info;
        }

        public String getError_info()
        {
            return error_info;
        }
    }


    /**
     * 回复或者发表新的帖子后服务器返回的数据
     */
    public static class Send_Article
    {
        private Article article;
        private boolean failed;
        private String failed_info;

        public Send_Article(Article article, boolean failed, String failed_info)
        {
            this.article = article;
            this.failed = failed;
            this.failed_info = failed_info;
        }

        public Article getArticle()
        {
            return article;
        }

        public boolean isFailed()
        {
            return failed;
        }

        public String getFailed_info()
        {
            return failed_info;
        }
    }
}
