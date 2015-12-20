package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Mail 信件元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Mail
 * Created by Lue on 2015/12/20.
 */
public class Mail implements Parcelable
{
    //信件编号，此编号为/mail/:box/:num中的num
    private int index = -1;
    //是否标记为m
    private boolean is_m = false;
    //是否已读
    private boolean is_read = false;
    //是否回复
    private boolean is_reply = false;
    //是否有附件
    private boolean has_attachment = false;
    //信件标题
    private String title = null;
    //发信人，此为user元数据，如果user不存在则为用户id
    private User user = null;
    //发信时间
    private int post_time = -1;
    //所属信箱名
    private String box_name = null;
    //信件内容, 只存在于/mail/:box/:num中
    private String content = null;
    //信件的附件列表
    private Attachment attachment = null;


    protected Mail(Parcel in)
    {
        index = in.readInt();
        is_m = in.readByte() != 0;
        is_read = in.readByte() != 0;
        is_reply = in.readByte() != 0;
        has_attachment = in.readByte() != 0;
        title = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        post_time = in.readInt();
        box_name = in.readString();
        content = in.readString();
        attachment = in.readParcelable(Attachment.class.getClassLoader());
    }

    public static final Creator<Mail> CREATOR = new Creator<Mail>()
    {
        @Override
        public Mail createFromParcel(Parcel in)
        {
            return new Mail(in);
        }

        @Override
        public Mail[] newArray(int size)
        {
            return new Mail[size];
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
        dest.writeInt(is_m? 1:0);
        dest.writeInt(is_read? 1:0);
        dest.writeInt(is_reply? 1:0);
        dest.writeInt(has_attachment? 1:0);
        dest.writeString(title);
        dest.writeValue(user);
        dest.writeInt(post_time);
        dest.writeString(box_name);
        dest.writeString(content);
        dest.writeValue(attachment);
    }


    public Attachment getAttachment()
    {
        return attachment;
    }

    public void setAttachment(Attachment attachment)
    {
        this.attachment = attachment;
    }

    public String getBox_name()
    {
        return box_name;
    }

    public void setBox_name(String box_name)
    {
        this.box_name = box_name;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public boolean isHas_attachment()
    {
        return has_attachment;
    }

    public void setHas_attachment(boolean has_attachment)
    {
        this.has_attachment = has_attachment;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public boolean is_m()
    {
        return is_m;
    }

    public void setIs_m(boolean is_m)
    {
        this.is_m = is_m;
    }

    public boolean is_read()
    {
        return is_read;
    }

    public void setIs_read(boolean is_read)
    {
        this.is_read = is_read;
    }

    public boolean is_reply()
    {
        return is_reply;
    }

    public void setIs_reply(boolean is_reply)
    {
        this.is_reply = is_reply;
    }

    public int getPost_time()
    {
        return post_time;
    }

    public void setPost_time(int post_time)
    {
        this.post_time = post_time;
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
