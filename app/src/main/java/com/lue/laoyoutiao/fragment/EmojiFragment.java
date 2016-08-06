package com.lue.laoyoutiao.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.adapter.EmojiGridviewAdapter;
import com.lue.laoyoutiao.view.emoji.EmojiClickManager;

/**
 * Created by Lue on 2016/5/17.
 */
public class EmojiFragment extends Fragment
{
    private Context context;
    private int emoji_num;
    //表情类别，0代表普通，1代表悠嘻猴，2代表兔斯基，3代表洋葱头
    private int emoji_category;

    public static EmojiFragment getInstance(int emoji_num, int emoji_category)
    {
        EmojiFragment fragment = new EmojiFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("emoji_num", emoji_num);
        bundle.putInt("emoji_category", emoji_category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            emoji_num = bundle.getInt("emoji_num");
            emoji_category = bundle.getInt("emoji_category");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = null;
        if(emoji_category == 0)
        {
            view = inflater.inflate(R.layout.emotion_gird_classic, container, false);
        }
        else
        {
            view = inflater.inflate(R.layout.emotion_gird, container, false);
        }

        EmojiGridviewAdapter adapter = new EmojiGridviewAdapter(context, emoji_num, emoji_category);

        GridView grid = (GridView) view.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(EmojiClickManager.getInstance().getOnItemClickListener(emoji_category));
        return view;
    }
}
