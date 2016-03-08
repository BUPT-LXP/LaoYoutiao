package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Board 版面元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Board
 * Created by Lue on 2015/12/20.
 */
public class Board implements Parcelable
{
    //版面名称
    private String name = null;
    //版主列表，以空格分隔各个id
    private String manager = null;
    //版面描述
    private String description = null;
    //版面所属类别, 原接口中为class，但class为保留字，因此使用boardclass
    private String boardclass = null;
    //版面所属分区号
    private String section = null;
    //今日发帖总数
    private int threads_today_count = -1;
    //今日发文总数
    private int post_today_count = -1;
    //版面主题总数
    private int post_threads_count = -1;
    //版面文章总数
    private int post_all_count = -1;
    //版面是否只读
    private boolean is_read_only = false;
    //版面是否不可回复
    private boolean is_no_reply = false;
    //版面书否允许附件
    private boolean allow_attachment = false;
    //版面是否允许匿名发文
    private boolean allow_anonymous = false;
    //版面是否允许转信
    private boolean allow_outgo = false;
    //当前登陆用户是否有发文/回复权限
    private boolean allow_post = false;
    //版面当前在线用户数
    private int user_online_count = -1;
    //版面历史最大在线用户数
    private int user_online_max_count = -1;
    //版面历史最大在线用户数发生时间
    private int user_online_max_time = -1;




    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(manager);
        dest.writeString(description);
        dest.writeString(boardclass);
        dest.writeString(section);
        dest.writeInt(threads_today_count);
        dest.writeInt(post_today_count);
        dest.writeInt(post_threads_count);
        dest.writeInt(post_all_count);
        dest.writeInt(is_read_only? 1:0);
        dest.writeInt(is_no_reply? 1:0);
        dest.writeInt(allow_attachment? 1:0);
        dest.writeInt(allow_anonymous? 1:0);
        dest.writeInt(allow_outgo? 1:0);
        dest.writeInt(allow_post? 1:0);
        dest.writeInt(user_online_count);
        dest.writeInt(user_online_max_count);
        dest.writeInt(user_online_max_time);
    }

    protected Board(Parcel in)
    {
        name = in.readString();
        manager = in.readString();
        description = in.readString();
        boardclass = in.readString();
        section = in.readString();
        threads_today_count = in.readInt();
        post_today_count = in.readInt();
        post_threads_count = in.readInt();
        post_all_count = in.readInt();
        is_read_only = in.readByte() != 0;
        is_no_reply = in.readByte() != 0;
        allow_attachment = in.readByte() != 0;
        allow_anonymous = in.readByte() != 0;
        allow_outgo = in.readByte() != 0;
        allow_post = in.readByte() != 0;
        user_online_count = in.readInt();
        user_online_max_count = in.readInt();
        user_online_max_time = in.readInt();
    }

    public static final Creator<Board> CREATOR = new Creator<Board>()
    {
        @Override
        public Board createFromParcel(Parcel in)
        {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size)
        {
            return new Board[size];
        }
    };

    public boolean isAllow_anonymous()
    {
        return allow_anonymous;
    }

    public void setAllow_anonymous(boolean allow_anonymous)
    {
        this.allow_anonymous = allow_anonymous;
    }

    public boolean isAllow_attachment()
    {
        return allow_attachment;
    }

    public void setAllow_attachment(boolean allow_attachment)
    {
        this.allow_attachment = allow_attachment;
    }

    public boolean isAllow_outgo()
    {
        return allow_outgo;
    }

    public void setAllow_outgo(boolean allow_outgo)
    {
        this.allow_outgo = allow_outgo;
    }

    public boolean isAllow_post()
    {
        return allow_post;
    }

    public void setAllow_post(boolean allow_post)
    {
        this.allow_post = allow_post;
    }

    public String getBoardclass()
    {
        return boardclass;
    }

    public void setBoardclass(String boardclass)
    {
        this.boardclass = boardclass;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean is_no_reply()
    {
        return is_no_reply;
    }

    public void setIs_no_reply(boolean is_no_reply)
    {
        this.is_no_reply = is_no_reply;
    }

    public boolean is_read_only()
    {
        return is_read_only;
    }

    public void setIs_read_only(boolean is_read_only)
    {
        this.is_read_only = is_read_only;
    }

    public String getManager()
    {
        return manager;
    }

    public void setManager(String manager)
    {
        this.manager = manager;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPost_all_count()
    {
        return post_all_count;
    }

    public void setPost_all_count(int post_all_count)
    {
        this.post_all_count = post_all_count;
    }

    public int getPost_threads_count()
    {
        return post_threads_count;
    }

    public void setPost_threads_count(int post_threads_count)
    {
        this.post_threads_count = post_threads_count;
    }

    public int getPost_today_count()
    {
        return post_today_count;
    }

    public void setPost_today_count(int post_today_count)
    {
        this.post_today_count = post_today_count;
    }

    public String getSection()
    {
        return section;
    }

    public void setSection(String section)
    {
        this.section = section;
    }

    public int getUser_online_count()
    {
        return user_online_count;
    }

    public void setUser_online_count(int user_online_count)
    {
        this.user_online_count = user_online_count;
    }

    public int getUser_online_max_count()
    {
        return user_online_max_count;
    }

    public void setUser_online_max_count(int user_online_max_count)
    {
        this.user_online_max_count = user_online_max_count;
    }

    public int getUser_online_max_time()
    {
        return user_online_max_time;
    }

    public void setUser_online_max_time(int user_online_max_time)
    {
        this.user_online_max_time = user_online_max_time;
    }

    public int getThreads_today_count()
    {
        return threads_today_count;
    }

    public void setThreads_today_count(int threads_today_count)
    {
        this.threads_today_count = threads_today_count;
    }
}
