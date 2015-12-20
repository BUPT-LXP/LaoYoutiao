package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Section 分区元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Section
 * Created by Lue on 2015/12/20.
 */
public class Section implements Parcelable
{
    //分区名称
    private String name = null;
    //分区表述
    private String description = null;
    //是否是根分区
    private boolean is_root = false;
    //该分区所属根分区名称
    private String parent = null;


    protected Section(Parcel in)
    {
        name = in.readString();
        description = in.readString();
        is_root = in.readByte() != 0;
        parent = in.readString();
    }

    public static final Creator<Section> CREATOR = new Creator<Section>()
    {
        @Override
        public Section createFromParcel(Parcel in)
        {
            return new Section(in);
        }

        @Override
        public Section[] newArray(int size)
        {
            return new Section[size];
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
        dest.writeString(description);
        dest.writeInt(is_root? 1:0);
        dest.writeString(parent);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean is_root()
    {
        return is_root;
    }

    public void setIs_root(boolean is_root)
    {
        this.is_root = is_root;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getParent()
    {
        return parent;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }
}
