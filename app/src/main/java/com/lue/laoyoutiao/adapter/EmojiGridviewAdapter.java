package com.lue.laoyoutiao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lue.laoyoutiao.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Lue on 2016/5/17.
 */
public class EmojiGridviewAdapter extends BaseAdapter
{

    private Context context;
    private int emoji_num;
    //表情类别，0代表普通，1代表悠嘻猴，2代表兔斯基，3代表洋葱头
    private int emoji_category;

    private class ViewHolder
    {
        public ImageView image;
    }

    public EmojiGridviewAdapter(Context context, int emoji_num, int emoji_category)
    {
        this.context = context;
        this.emoji_num = emoji_num;
        this.emoji_category = emoji_category;
    }

    @Override
    public int getCount()
    {
        return emoji_num;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.emotion_gird_item, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String path = "";
        switch (emoji_category)
        {
            case 0:
                //经典表情编号从em1开始的
                path = "file:///android_asset/emoji/em" + (position+1) + ".gif";
                break;
            case 1:
                path = "file:///android_asset/emoji/ema" + position + ".gif";
                break;
            case 2:
                path = "file:///android_asset/emoji/emb" + position + ".gif";
                break;
            case 3:
                path = "file:///android_asset/emoji/emc" + position + ".gif";
                break;
            default:
                break;
        }
        if(holder.image != null)
            Picasso.with(context).load(path).into(holder.image);

        return convertView;
    }
}
