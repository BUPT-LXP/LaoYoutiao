package com.lue.laoyoutiao.view;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

/**
 * Created by Lue on 2016/5/9.
 */
public class GifCallback implements Drawable.Callback
{
    private final TextView textView;

    public GifCallback(TextView textView)
    {
        this.textView = textView;
    }
    @Override
    public void invalidateDrawable(Drawable who)
    {
        textView.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when)
    {
        textView.postDelayed(what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what)
    {
        textView.removeCallbacks(what);
    }
}
