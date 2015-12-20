package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Mailbox 用户信箱元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Mailbox
 * Created by Lue on 2015/12/20.
 */
public class Mailbox implements Parcelable
{
    //是否有新邮件
    private boolean new_mail = false;
    //信箱是否已满
    private boolean full_mail = false;
    //信箱已用空间
    private String space_used = null;
    //当前用户是否能发信
    private boolean can_send = false;


    protected Mailbox(Parcel in)
    {
        new_mail = in.readByte() != 0;
        full_mail = in.readByte() != 0;
        space_used = in.readString();
        can_send = in.readByte() != 0;
    }

    public static final Creator<Mailbox> CREATOR = new Creator<Mailbox>()
    {
        @Override
        public Mailbox createFromParcel(Parcel in)
        {
            return new Mailbox(in);
        }

        @Override
        public Mailbox[] newArray(int size)
        {
            return new Mailbox[size];
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
        dest.writeInt(new_mail? 1:0);
        dest.writeInt(full_mail? 1:0);
        dest.writeString(space_used);
        dest.writeInt(can_send? 1:0);
    }

    public boolean isCan_send()
    {
        return can_send;
    }

    public void setCan_send(boolean can_send)
    {
        this.can_send = can_send;
    }

    public boolean isFull_mail()
    {
        return full_mail;
    }

    public void setFull_mail(boolean full_mail)
    {
        this.full_mail = full_mail;
    }

    public boolean isNew_mail()
    {
        return new_mail;
    }

    public void setNew_mail(boolean new_mail)
    {
        this.new_mail = new_mail;
    }

    public String getSpace_used()
    {
        return space_used;
    }

    public void setSpace_used(String space_used)
    {
        this.space_used = space_used;
    }
}
