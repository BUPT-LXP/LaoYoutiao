package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

/** User 用户元数据
 * https://github.com/xw2423/nForum/wiki/nForum-API-Meta-User
 * Created by Lue on 2015/12/20.
 */
public class User implements Parcelable
{
    //用户id
    private String id = "";
    //用户昵称
    private String user_name = "";
    //用户头像地址
    private String face_url = "";
    //用户头像宽度
    private String face_width = "";
    //用户头像高度
    private String face_height = "";
    //用户性别；m表示男性，f表示女性，n表示隐藏性别
    private String gender = "";
    //用户星座，若隐藏星座则为空
    private String astro = "";
    //用户生命值
    private int life = -1;
    //用户QQ
    private String qq = "";
    //用户msn
    private String msn = "";
    //用户个人主页
    private String home_page = "";
    //用户身份
    private String level = "";
    //用户是否在线
    private boolean if_online = false;
    //用户发文数量
    private int post_count = -1;
    //用户上次登录时间,unixtimestamp
    private int last_login_time = -1;
    //用户上次登录IP
    private String last_login_ip = "";
    //用户是否隐藏性别和星座
    private boolean is_hide = false;
    //用户是否通过注册审批
    private boolean is_register = false;
    //用户注册时间，unixtimestamp,当前登陆用户为 自己或是当前用户具有管理权限
    private int first_login_time = -1;
    // 登录次数,当前登陆用户为 自己或是当前用户具有管理权限
    private int login_count = -1;
    //用户是否为管理员
    private boolean is_admin= false;
    //用户挂站时间，以秒为单位
    private int stay_count = -1;



    public int describeContents()
    {
        return 0;
    }


    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(id);
        dest.writeString(user_name);
        dest.writeString(face_url);
        dest.writeString(face_width);
        dest.writeString(face_height);
        dest.writeString(gender);
        dest.writeString(astro);
        dest.writeInt(life);
        dest.writeString(qq);
        dest.writeString(msn);
        dest.writeString(home_page);
        dest.writeString(level);
        dest.writeInt(if_online? 1:0);
        dest.writeInt(post_count);
        dest.writeInt(last_login_time);
        dest.writeString(last_login_ip);
        dest.writeInt(is_hide? 1:0);
        dest.writeInt(is_register? 1:0);
        dest.writeInt(first_login_time);
        dest.writeInt(login_count);
        dest.writeInt(is_admin? 1:0);
        dest.writeInt(stay_count);
    }

    protected User(Parcel in)
    {
        id = in.readString();
        user_name = in.readString();
        face_url = in.readString();
        face_width = in.readString();
        face_height = in.readString();
        gender = in.readString();
        astro = in.readString();
        life = in.readInt();
        qq = in.readString();
        msn = in.readString();
        home_page = in.readString();
        level = in.readString();
        if_online = in.readByte() != 0;
        post_count = in.readInt();
        last_login_time = in.readInt();
        last_login_ip = in.readString();
        is_hide = in.readByte() != 0;
        is_register = in.readByte() != 0;
        first_login_time = in.readInt();
        login_count = in.readInt();
        is_admin = in.readByte() != 0;
        stay_count = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };



    public String getId()
    {
        return this.id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getUser_name()
    {
        return this.user_name;
    }
    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    public String getFace_url()
    {
        return this.face_url;
    }
    public void setFace_url(String face_url)
    {
        this.face_url = face_url;
    }

    public String getFace_width()
    {
        return this.face_width;
    }
    public void setFace_width(String face_width)
    {
        this.face_width = face_width;
    }

    public String getFace_height()
    {
        return this.face_height;
    }
    public void setFace_height(String face_height)
    {
        this.face_height = face_height;
    }

    public String getGender()
    {
        return this.gender;
    }
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getAstro()
    {
        return this.astro;
    }
    public void setAstro(String astro)
    {
        this.astro = astro;
    }

    public int getLife()
    {
        return this.life;
    }
    public void setLife(int life)
    {
        this.life = life;
    }

    public String getQQ()
    {
        return this.qq;
    }
    public void setQQ(String QQ)
    {
        this.qq = QQ;
    }

    public String getMsn()
    {
        return this.msn;
    }
    public void setMsn(String msn)
    {
        this.msn = msn;
    }

    public String getHome_page()
    {
        return this.home_page;
    }
    public void setHome_page(String home_page)
    {
        this.home_page = home_page;
    }

    public String getLevel()
    {
        return this.level;
    }
    public void setLevel(String level)
    {
        this.level = level;
    }

    public boolean getIf_online()
    {
        return this.if_online;
    }
    public void setIf_online(boolean if_online)
    {
        this.if_online = if_online;
    }

    public int getPost_count()
    {
        return this.post_count;
    }
    public void setPost_count(int post_count)
    {
        this.post_count = post_count;
    }

    public int getLast_login_time()
    {
        return this.last_login_time;
    }
    public void setLast_login_time(int last_login_time)
    {
        this.last_login_time = last_login_time;
    }

    public String getLat_login_ip()
    {
        return this.last_login_ip;
    }
    public void setLat_login_ip(String last_login_ip)
    {
        this.last_login_ip = last_login_ip;
    }

    public boolean getIs_hide()
    {
       return this.is_hide;
    }
    public void setIs_hide(boolean is_hide)
    {
        this.is_hide = is_hide;
    }

    public boolean getIs_register()
    {
        return this.is_register;
    }
    public void setIs_register(boolean is_register)
    {
        this.is_register = is_register;
    }

    public int getFirst_login_time()
    {
        return this.first_login_time;
    }
    public void setFirst_login_time(int first_login_time)
    {
        this.first_login_time = first_login_time;
    }

    public int getLogin_count()
    {
        return this.login_count;
    }
    public void setLogin_count(int login_count)
    {
        this.login_count = login_count;
    }

    public boolean getIs_admin()
    {
        return this.is_admin;
    }
    public void setIs_admin(boolean is_admin)
    {
        this.is_admin = is_admin;
    }

    public int getStay_count()
    {
        return this.stay_count;
    }
    public void setStay_count(int stay_count)
    {
        this.stay_count = stay_count;
    }

}
