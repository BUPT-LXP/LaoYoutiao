package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Refer 提醒元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Refer
 * Created by Lue on 2015/12/20.
 */
public class Refer implements Parcelable
{
    //提醒编号，此编号用于提醒的相关操作
    private int index = -1;
    //提醒文章的id
    private int id = -1;
    //提醒文章的group id
    private int group_id = -1;
    //提醒文章的reply id
    private int reply_id = -1;
    //提醒文章所在版面
    private String board_name = null;
    //提醒文章的标题
    private String title = null;
    //提醒文章的发信人，此为user元数据，如果user不存在则为用户id
    private User user = null;
    //发出提醒的时间
    private int time = -1;
    //提醒是否已读
    private boolean is_read = false;


    protected Refer(Parcel in)
    {
        index = in.readInt();
        id = in.readInt();
        group_id = in.readInt();
        reply_id = in.readInt();
        board_name = in.readString();
        title = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        time = in.readInt();
        is_read = in.readByte() != 0;
    }

    public static final Creator<Refer> CREATOR = new Creator<Refer>()
    {
        @Override
        public Refer createFromParcel(Parcel in)
        {
            return new Refer(in);
        }

        @Override
        public Refer[] newArray(int size)
        {
            return new Refer[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeInt(id);
        dest.writeInt(group_id);
        dest.writeInt(reply_id);
        dest.writeString(board_name);
        dest.writeString(title);
        dest.writeValue(user);
        dest.writeInt(time);
        dest.writeInt(is_read? 1:0);
    }

    public String getBoard_name()
    {
        return board_name;
    }

    public void setBoard_name(String board_name)
    {
        this.board_name = board_name;
    }

    public int getGroup_id()
    {
        return group_id;
    }

    public void setGroup_id(int group_id)
    {
        this.group_id = group_id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public boolean is_read()
    {
        return is_read;
    }

    public void setIs_read(boolean is_read)
    {
        this.is_read = is_read;
    }

    public int getReply_id()
    {
        return reply_id;
    }

    public void setReply_id(int reply_id)
    {
        this.reply_id = reply_id;
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
    {
        this.time = time;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
