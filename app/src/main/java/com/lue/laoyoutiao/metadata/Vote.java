package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** Vote 投票元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Vote
 * Created by Lue on 2015/12/20.
 */
public class Vote implements Parcelable
{
    //投票标识id
    private int vid = -1;
    //投票标题
    private String title = null;
    //投票发起时间戳
    private int start = -1;
    //投票截止时间戳
    private int end = -1;
    //投票参与的人数
    private int user_count = -1;
    //投票总票数(投票类型为单选时与user_count相等)，如果设置投票后可见且还没投票这个值为-1, 只存在于/vote/:id中
    private int vote_count = -1;
    //投票类型，0为单选，1为多选
    private int type = -1;
    //每个用户能投票数的最大值，只有当type为1时，此属性有效
    private int limit = -1;
    //投票所关联的投票版面的文章id
    private int aid = -1;
    //投票是否截止
    private boolean is_end = false;
    //投票是否被删除
    private boolean is_deleted = false;
    //投票结果是否投票后可见
    private boolean is_result_voted = false;
    //投票发起人的用户元数据，如果该用户不存在则为字符串
    private User user = null;
    //当前用户的投票结果，如果用户已投票，则含有两个属性time(int)和viid(array)，
    // 分别表示投票的时间和所投选项的viid数组；如果用户没投票则为false
    private Object voted = null;
    //投票选项，由投票选项元数据组成的数组
    private Vote_Option[] options = null;


    protected Vote(Parcel in)
    {
        vid = in.readInt();
        title = in.readString();
        start = in.readInt();
        end = in.readInt();
        user_count = in.readInt();
        vote_count = in.readInt();
        type = in.readInt();
        limit = in.readInt();
        aid = in.readInt();
        is_end = in.readByte() != 0;
        is_deleted = in.readByte() != 0;
        is_result_voted = in.readByte() != 0;
        user = in.readParcelable(User.class.getClassLoader());
        options = in.createTypedArray(Vote_Option.CREATOR);
    }

    public static final Creator<Vote> CREATOR = new Creator<Vote>()
    {
        @Override
        public Vote createFromParcel(Parcel in)
        {
            return new Vote(in);
        }

        @Override
        public Vote[] newArray(int size)
        {
            return new Vote[size];
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
        dest.writeInt(vid);
        dest.writeString(title);
        dest.writeInt(start);
        dest.writeInt(end);
        dest.writeInt(user_count);
        dest.writeInt(vote_count);
        dest.writeInt(type);
        dest.writeInt(limit);
        dest.writeInt(aid);
        dest.writeInt(is_end? 1:0);
        dest.writeInt(is_deleted? 1:0);
        dest.writeInt(is_result_voted? 1:0);
        dest.writeValue(user);
        dest.writeValue(voted);
        dest.writeArray(options);
    }


    public int getAid()
    {
        return aid;
    }

    public void setAid(int aid)
    {
        this.aid = aid;
    }

    public int getEnd()
    {
        return end;
    }

    public void setEnd(int end)
    {
        this.end = end;
    }

    public boolean is_deleted()
    {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted)
    {
        this.is_deleted = is_deleted;
    }

    public boolean is_end()
    {
        return is_end;
    }

    public void setIs_end(boolean is_end)
    {
        this.is_end = is_end;
    }

    public boolean is_result_voted()
    {
        return is_result_voted;
    }

    public void setIs_result_voted(boolean is_result_voted)
    {
        this.is_result_voted = is_result_voted;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    public Vote_Option[] getOptions()
    {
        return options;
    }

    public void setOptions(Vote_Option[] options)
    {
        this.options = options;
    }

    public int getStart()
    {
        return start;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public int getUser_count()
    {
        return user_count;
    }

    public void setUser_count(int user_count)
    {
        this.user_count = user_count;
    }

    public int getVid()
    {
        return vid;
    }

    public void setVid(int vid)
    {
        this.vid = vid;
    }

    public int getVote_count()
    {
        return vote_count;
    }

    public void setVote_count(int vote_count)
    {
        this.vote_count = vote_count;
    }

    public Object getVoted()
    {
        return voted;
    }

    public void setVoted(Object voted)
    {
        this.voted = voted;
    }
}
