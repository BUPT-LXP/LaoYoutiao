package com.lue.laoyoutiao.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

/**
 * Created by Lue on 2016/5/16.
 */
public class ReplyviewOnItemClickManager
{

    private static ReplyviewOnItemClickManager instance;
    private EditText mEditText;

    public static ReplyviewOnItemClickManager getInstance()
    {
        if (instance == null)
        {
            instance = new ReplyviewOnItemClickManager();
        }
        return instance;
    }

    public void attachToEditText(EditText editText)
    {
        mEditText = editText;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener(final int emojiType)
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                StringBuilder sb = new StringBuilder();
                switch (emojiType)
                {
                    case 0:
                        sb.append("[em").append(String.valueOf(position + 1)).append("]");
                        break;
                    case 1:
                        sb.append("[ema").append(String.valueOf(position)).append("]");
                        break;
                    case 2:
                        sb.append("[emb").append(String.valueOf(position)).append("]");
                        break;
                    case 3:
                        sb.append("[emc").append(String.valueOf(position)).append("]");
                        break;
                }
                mEditText.append(sb.toString());
            }
        };
    }
}
