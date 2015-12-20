package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Widget  widget元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Widget
 * Created by Lue on 2015/12/20.
 */
public class Widget implements Parcelable
{
    //widget标识
    private String name = null;
    //widget标题
    private String title = null;
    //上次修改时间
    private int time = -1;


    protected Widget(Parcel in)
    {
        name = in.readString();
        title = in.readString();
        time = in.readInt();
    }

    public static final Creator<Widget> CREATOR = new Creator<Widget>()
    {
        @Override
        public Widget createFromParcel(Parcel in)
        {
            return new Widget(in);
        }

        @Override
        public Widget[] newArray(int size)
        {
            return new Widget[size];
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
        dest.writeString(name);
        dest.writeString(title);
        dest.writeInt(time);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
}
