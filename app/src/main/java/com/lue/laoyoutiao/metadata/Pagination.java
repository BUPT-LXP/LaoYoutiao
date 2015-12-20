package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Pagination 分页元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Pagination
 * Created by Lue on 2015/12/20.
 */
public class Pagination implements Parcelable
{
    //总页数
    private int page_all_count = -1;
    //当前页数
    private int page_current_count = -1;
    //每页元素个数
    private int item_page_count = -1;
    //所有元素个数
    private int item_all_count = -1;


    protected Pagination(Parcel in)
    {
        page_all_count = in.readInt();
        page_current_count = in.readInt();
        item_page_count = in.readInt();
        item_all_count = in.readInt();
    }

    public static final Creator<Pagination> CREATOR = new Creator<Pagination>()
    {
        @Override
        public Pagination createFromParcel(Parcel in)
        {
            return new Pagination(in);
        }

        @Override
        public Pagination[] newArray(int size)
        {
            return new Pagination[size];
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
        dest.writeInt(page_all_count);
        dest.writeInt(page_current_count);
        dest.writeInt(item_page_count);
        dest.writeInt(item_all_count);
    }


    public int getItem_all_count()
    {
        return item_all_count;
    }

    public void setItem_all_count(int item_all_count)
    {
        this.item_all_count = item_all_count;
    }

    public int getItem_page_count()
    {
        return item_page_count;
    }

    public void setItem_page_count(int item_page_count)
    {
        this.item_page_count = item_page_count;
    }

    public int getPage_all_count()
    {
        return page_all_count;
    }

    public void setPage_all_count(int page_all_count)
    {
        this.page_all_count = page_all_count;
    }

    public int getPage_current_count()
    {
        return page_current_count;
    }

    public void setPage_current_count(int page_current_count)
    {
        this.page_current_count = page_current_count;
    }
}
