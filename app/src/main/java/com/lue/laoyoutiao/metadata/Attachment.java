package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Attachment 附件元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Attachment
 * Created by Lue on 2015/12/20.
 */
public class Attachment implements Parcelable
{
    // 文件列表
    private file[] files = null;
    // 剩余空间大小
    private String remain_space = null;
    // 剩余附件个数
    private int remain_count = -1;




    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeArray(files);
        dest.writeString(remain_space);
        dest.writeInt(remain_count);
    }

    protected Attachment(Parcel in)
    {
        files = in.createTypedArray(file.CREATOR);
        remain_space = in.readString();
        remain_count = in.readInt();
    }

    public static final Creator<Attachment> CREATOR = new Creator<Attachment>()
    {
        @Override
        public Attachment createFromParcel(Parcel in)
        {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size)
        {
            return new Attachment[size];
        }
    };

    public file[] getFiles()
    {
        return files;
    }
    public void setFiles(file[] files)
    {
        this.files = files;
    }

    public String getRemain_space()
    {
        return remain_space;
    }
    public void setRemain_space(String remain_space)
    {
        this.remain_space = remain_space;
    }

    public int getRemain_count()
    {
        return remain_count;
    }
    public void setRemain_count(int remain_count)
    {
        this.remain_count = remain_count;
    }


    public static class file implements Parcelable
    {
        // 文件名
        String name = null;
        // 文件链接，在用户空间的文件，该值为空
        String url = null;
        // 文件大小
        String size = null;
        // 宽度为120px的缩略图，用户空间的文件，该值为空,附件为图片格式(jpg,png,gif)存在
        String thumbnail_small = null;
        // 宽度为240px的缩略图，用户空间的文件，该值为空,附件为图片格式(jpg,png,gif)存在
        String thumbnail_middle = null;

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

        protected file(Parcel in)
        {
            name = in.readString();
            url = in.readString();
            size = in.readString();
            thumbnail_small = in.readString();
            thumbnail_middle = in.readString();
        }

        public static final Creator<file> CREATOR = new Creator<file>()
        {
            @Override
            public file createFromParcel(Parcel in)
            {
                return new file(in);
            }

            @Override
            public file[] newArray(int size)
            {
                return new file[size];
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
}

