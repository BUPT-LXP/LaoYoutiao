package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Vote_Option 投票选项元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Vote
 * Created by Lue on 2015/12/20.
 */
public class Vote_Option implements Parcelable
{
    //投票选项标识id
    private int viid = -1;
    //选项内容
    private String label = null;
    //改选项已投票数，如果设置投票后可见且还没投票这个值为-1
    private int num = -1;


    protected Vote_Option(Parcel in)
    {
        viid = in.readInt();
        label = in.readString();
        num = in.readInt();
    }

    public static final Creator<Vote_Option> CREATOR = new Creator<Vote_Option>()
    {
        @Override
        public Vote_Option createFromParcel(Parcel in)
        {
            return new Vote_Option(in);
        }

        @Override
        public Vote_Option[] newArray(int size)
        {
            return new Vote_Option[size];
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
        dest.writeInt(viid);
        dest.writeString(label);
        dest.writeInt(num);
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getNum()
    {
        return num;
    }

    public void setNum(int num)
    {
        this.num = num;
    }

    public int getViid()
    {
        return viid;
    }

    public void setViid(int viid)
    {
        this.viid = viid;
    }
}
