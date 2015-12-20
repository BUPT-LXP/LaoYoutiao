package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** AttachmentFile 附件中文件元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Attachment
 * Created by Lue on 2015/12/20.
 */
public class AttachmentFile implements Parcelable
{
    // 文件名
    private String name = null;
    // 文件链接，在用户空间的文件，该值为空
    private String url = null;
    // 文件大小
    private String size = null;
    // 宽度为120px的缩略图，用户空间的文件，该值为空,附件为图片格式(jpg,png,gif)存在
    private String thumbnail_small = null;
    // 宽度为240px的缩略图，用户空间的文件，该值为空,附件为图片格式(jpg,png,gif)存在
    private String thumbnail_middle = null;



    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(size);
        dest.writeString(thumbnail_small);
        dest.writeString(thumbnail_middle);
    }

    protected AttachmentFile(Parcel in)
    {
        name = in.readString();
        url = in.readString();
        size = in.readString();
        thumbnail_small = in.readString();
        thumbnail_middle = in.readString();
    }

    public static final Creator<AttachmentFile> CREATOR = new Creator<AttachmentFile>()
    {
        @Override
        public AttachmentFile createFromParcel(Parcel in)
        {
            return new AttachmentFile(in);
        }

        @Override
        public AttachmentFile[] newArray(int size)
        {
            return new AttachmentFile[size];
        }
    };


    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getSize()
    {
        return size;
    }
    public void setSize(String size)
    {
        this.size = size;
    }

    public String getThumbnail_small()
    {
        return thumbnail_small;
    }
    public void setThumbnail_small(String thumbnail_small)
    {
        this.thumbnail_small = thumbnail_small;
    }

    public String getThumbnail_middle()
    {
        return thumbnail_middle;
    }
    public void setThumbnail_middle(String thumbnail_middle)
    {
        this.thumbnail_middle = thumbnail_middle;
    }
}
