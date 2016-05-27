package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

/** Article元数据
 * //https://github.com/xw2423/nForum/wiki/nForum-API-Meta-Article
 * Created by Lue on 2015/12/20.
 */
public class Article implements Parcelable
{
    //文章id
    private int id = -1;
    // 该文章所属主题的id
    private int group_id = -1;
    // 该文章回复文章的id
    private int reply_id = -1;
    // 文章标记 分别是m g ; b u o 8
    private String flag = null;
    //文章所在主题的位置或文章在默写浏览模式下的位置,
    // /board/:name的非主题模式下为访问此文章的id，在/threads/:board/:id中为所在主题中的位置，其余为空
    private int position = -1;
    // 文章是否置顶
    private boolean is_top = false;
    // 该文章是否是主题帖
    private boolean is_subject = false;
    // 文章是否有附件
    private boolean has_attachment = false;
    // 当前登陆用户是否对文章有管理权限 包括编辑，删除，修改附件
    private boolean is_admin = false;
    // 文章标题
    private String title = null;
    //文章发表用户，这是一个用户元数据
    private User user= null;
    // 文章发表时间，unixtimestamp
    private int post_time = -1;
    // 所属版面名称
    private String board_name = null;
    // 文章内容， 在/board/:name的文章列表和/search/(article|threads)中不存在此属性
    private String content = null;
    //文章附件列表，这是一个附件元数据
    private Attachment attachment = null;
    // 该文章的前一篇文章id,只存在于/article/:board/:id中
    private int previous_id = -1;
    // 该文章的后一篇文章id,只存在于/article/:board/:id中
    private int next_id = -1;
    // 该文章同主题前一篇文章id,只存在于/article/:board/:id中
    private int threads_previous_id = -1;
    // 该文章同主题后一篇文章id,只存在于/article/:board/:id中
    private int threads_next_id = -1;
    // 该主题回复文章数,只存在于/board/:name，/threads/:board/:id和/search/threads中
    private int reply_count = -1;
    private String last_reply_user_id = null;
    //该文章最后回复的时间 unxitmestamp
    private int last_reply_time = -1;

    //该文章回复内容的SpannableString，仅限回复内容，不包括引用别人的内容
    private SpannableStringBuilder ssb_content;
    private String str_reference;
    private SpannableString ss_reference;
    private String str_app;
    private SpannableString ss_app;

    private boolean is_content_separated = false;

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeInt(group_id);
        dest.writeInt(reply_id);
        dest.writeString(flag);
        dest.writeInt(position);
        dest.writeInt(is_top? 1:0);
        dest.writeInt(is_subject? 1:0);
        dest.writeInt(has_attachment? 1:0);
        dest.writeInt(is_admin? 1:0);
        dest.writeString(title);
        dest.writeValue(user);
        dest.writeInt(post_time);
        dest.writeString(board_name);
        dest.writeString(content);
        dest.writeValue(attachment);
        dest.writeInt(previous_id);
        dest.writeInt(next_id);
        dest.writeInt(threads_previous_id);
        dest.writeInt(threads_next_id);
        dest.writeInt(reply_count);
        dest.writeString(last_reply_user_id);
        dest.writeInt(last_reply_time);
    }

    protected Article(Parcel in)
    {
        id = in.readInt();
        group_id = in.readInt();
        reply_id = in.readInt();
        flag = in.readString();
        position = in.readInt();
        is_top = in.readByte() != 0;
        is_subject = in.readByte() != 0;
        has_attachment = in.readByte() != 0;
        is_admin = in.readByte() != 0;
        title = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        post_time = in.readInt();
        board_name = in.readString();
        content = in.readString();
        attachment = in.readParcelable(Attachment.class.getClassLoader());
        previous_id = in.readInt();
        next_id = in.readInt();
        threads_previous_id = in.readInt();
        threads_next_id = in.readInt();
        reply_count = in.readInt();
        last_reply_user_id = in.readString();
        last_reply_time = in.readInt();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>()
    {
        @Override
        public Article createFromParcel(Parcel in)
        {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size)
        {
            return new Article[size];
        }
    };

    public Attachment getAttachment()
    {
        return attachment;
    }

    public void setAttachment(Attachment attachment)
    {
        this.attachment = attachment;
    }

    public String getBoard_name()
    {
        return board_name;
    }

    public void setBoard_name(String board_name)
    {
        this.board_name = board_name;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getFlag()
    {
        return flag;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }

    public int getGroup_id()
    {
        return group_id;
    }

    public void setGroup_id(int group_id)
    {
        this.group_id = group_id;
    }

    public boolean isHas_attachment()
    {
        return has_attachment;
    }

    public void setHas_attachment(boolean has_attachment)
    {
        this.has_attachment = has_attachment;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public boolean is_admin()
    {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin)
    {
        this.is_admin = is_admin;
    }

    public boolean is_subject()
    {
        return is_subject;
    }

    public void setIs_subject(boolean is_subject)
    {
        this.is_subject = is_subject;
    }

    public boolean is_top()
    {
        return is_top;
    }

    public void setIs_top(boolean is_top)
    {
        this.is_top = is_top;
    }

    public int getLast_reply_time()
    {
        return last_reply_time;
    }

    public void setLast_reply_time(int last_reply_time)
    {
        this.last_reply_time = last_reply_time;
    }

    public String getLast_reply_user_id()
    {
        return last_reply_user_id;
    }

    public void setLast_reply_user_id(String last_reply_user_id)
    {
        this.last_reply_user_id = last_reply_user_id;
    }

    public int getNext_id()
    {
        return next_id;
    }

    public void setNext_id(int next_id)
    {
        this.next_id = next_id;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getPost_time()
    {
        return post_time;
    }

    public void setPost_time(int post_time)
    {
        this.post_time = post_time;
    }

    public int getPrevious_id()
    {
        return previous_id;
    }

    public void setPrevious_id(int previous_id)
    {
        this.previous_id = previous_id;
    }

    public int getReply_count()
    {
        return reply_count;
    }

    public void setReply_count(int reply_count)
    {
        this.reply_count = reply_count;
    }

    public int getReply_id()
    {
        return reply_id;
    }

    public void setReply_id(int reply_id)
    {
        this.reply_id = reply_id;
    }

    public int getThreads_next_id()
    {
        return threads_next_id;
    }

    public void setThreads_next_id(int threads_next_id)
    {
        this.threads_next_id = threads_next_id;
    }

    public int getThreads_previous_id()
    {
        return threads_previous_id;
    }

    public void setThreads_previous_id(int threads_previous_id)
    {
        this.threads_previous_id = threads_previous_id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public SpannableStringBuilder getSsb_content()
    {
        return ssb_content;
    }

    public void setSsb_content(SpannableStringBuilder ssb_content)
    {
        this.ssb_content = ssb_content;
    }

    public boolean is_content_separated()
    {
        return is_content_separated;
    }

    public void setIs_content_separated(boolean is_content_separated)
    {
        this.is_content_separated = is_content_separated;
    }

    public String getStr_reference()
    {
        return str_reference;
    }

    public void setStr_reference(String str_reference)
    {
        this.str_reference = str_reference;
    }

    public String getStr_app()
    {
        return str_app;
    }

    public void setStr_app(String str_app)
    {
        this.str_app = str_app;
    }

    public SpannableString getSs_reference()
    {
        return ss_reference;
    }

    public SpannableString getSs_app()
    {
        return ss_app;
    }

    public void setSs_reference(SpannableString ss_reference)
    {
        this.ss_reference = ss_reference;
    }

    public void setSs_app(SpannableString ss_app)
    {
        this.ss_app = ss_app;
    }
}
