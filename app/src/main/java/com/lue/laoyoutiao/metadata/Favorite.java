package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Favorite 收藏夹元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Favorite
 * Created by Lue on 2015/12/20.
 */
public class Favorite implements Parcelable
{
    //收藏夹级数，顶层收藏夹level为0
    private int level = -1;
    //收藏夹目录
    private String description = null;
    //收藏夹目录位置，该值用于删除收藏夹目录
    private int position = -1;


    protected Favorite(Parcel in)
    {
        level = in.readInt();
        description = in.readString();
        position = in.readInt();
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>()
    {
        @Override
        public Favorite createFromParcel(Parcel in)
        {
            return new Favorite(in);
        }

        @Override
        public Favorite[] newArray(int size)
        {
            return new Favorite[size];
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
        dest.writeInt(level);
        dest.writeString(description);
        dest.writeInt(position);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }
}
