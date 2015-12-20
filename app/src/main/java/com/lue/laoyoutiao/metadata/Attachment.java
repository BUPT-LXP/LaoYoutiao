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
    private AttachmentFile[] attachmentFiles = null;
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
        dest.writeArray(attachmentFiles);
        dest.writeString(remain_space);
        dest.writeInt(remain_count);
    }

    protected Attachment(Parcel in)
    {
        attachmentFiles = in.createTypedArray(AttachmentFile.CREATOR);
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

    public AttachmentFile[] getAttachmentFiles()
    {
        return attachmentFiles;
    }
    public void setAttachmentFiles(AttachmentFile[] attachmentFiles)
    {
        this.attachmentFiles = attachmentFiles;
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

}

