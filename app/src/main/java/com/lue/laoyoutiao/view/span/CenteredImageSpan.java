package com.lue.laoyoutiao.view.span;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.activity.ReadArticleActivity;
import com.lue.laoyoutiao.activity.ShowImageActivity;
import com.lue.laoyoutiao.dialog.ShowImageDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lue on 2016/5/13.
 */
public class CenteredImageSpan extends ImageSpan implements View.OnClickListener
{
    private ReadArticleActivity context;
    private int tv_width;
    private String url;
    //高清大图
    private Bitmap bitmap_hd;

    public CenteredImageSpan(Context context, Bitmap b, int verticalAlignment, int tv_width, String url)
    {
        super(context, b, verticalAlignment);
        this.tv_width = tv_width;
        this.url = url;
        this.context = (ReadArticleActivity) context;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm)
    {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null)
        {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint)
    {
        Drawable b = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;

        //使图片水平居中
        canvas.translate((tv_width - b.getBounds().width())/2, transY);
        b.draw(canvas);
        canvas.restore();
    }

    @Override
    public void onClick(View v)
    {
//        Context context = ContextApplication.getAppContext();

        int id = v.getId();
        if(id == R.id.textview_article_content)
        {
            TextView textView = (TextView)v;
            CharSequence charSequence = textView.getText();
            SpannableString spannableString = new SpannableString(charSequence);
            CenteredImageSpan[] spans = spannableString.getSpans(0, spannableString.length(), CenteredImageSpan.class);

            Intent intent = new Intent(context, ShowImageActivity.class);
            String[] urls = new String[spans.length];
//            Bitmap[] images = new Bitmap[spans.length];
            List<Bitmap> images = new ArrayList<>();

            int index = 0;
            for(int i=0; i<spans.length; i++)
            {
                urls[i] = spans[i].url;
                if(url.equals(spans[i].url))
                {
//                    intent.putExtra("index", i);
                    index = i;
                }

//                images.add(context.images_hd.get(urls[i]));
            }
//            intent.putExtra("urls", urls);
//            intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) images);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);


            ShowImageDialog dialog = ShowImageDialog.getInstance(context, urls, index);
            dialog.show(context.getFragmentManager(), "");
        }
    }

}
