package com.lue.laoyoutiao.metadata;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> sub_section_names = new ArrayList<>();

    private List<String> boards_names = new ArrayList<>();


    public Section(String name)
    {
        this.name = name;
    }


    protected Section(Parcel in)
    {
        name = in.readString();
        description = in.readString();
        is_root = in.readByte() != 0;
        parent = in.readString();
        in.readStringList(sub_section_names);
        in.readStringList(boards_names);
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
        dest.writeStringList(sub_section_names);
        dest.writeStringList(boards_names);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean getIs_root()
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

    public List<String> getBoards_names()
    {
        return boards_names;
    }



    public List<String> getSub_section_names()
    {
        return sub_section_names;
    }

    public String getSub_section_name(int position)
    {
        return sub_section_names != null? sub_section_names.get(position) : null;
    }

    public int getSub_section_size()
    {
        return sub_section_names != null? sub_section_names.size() : 0;
    }

    public void setBoards_names(String board_name)
    {
        if(this.boards_names == null)
            this.boards_names = new ArrayList<>();
        this.boards_names.add(board_name);
    }

    public String getBoard_name(int position)
    {
        return boards_names != null? boards_names.get(position) : null;
    }


    public int getBoards_size()
    {
        return boards_names != null? boards_names.size() : 0;
    }


    public void setSub_section_names(String sub_section_names)
    {
        if(this.sub_section_names == null)
            this.sub_section_names = new ArrayList<>();
        this.sub_section_names.add(sub_section_names);
    }
}
