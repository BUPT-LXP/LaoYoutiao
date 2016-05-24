package com.lue.laoyoutiao.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.activity.ReadArticleActivity;
import com.lue.laoyoutiao.cache.ACache;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.helper.AttachmentHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.view.ZoomImageView;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/5/20.
 */
public class ShowImageDialog extends DialogFragment
{
    private Context context;
    private ViewPager viewpager;
    private TextView textview;
    private ZoomImageView[] pageview;
    private int currentitem = 0;
    private int totalitem = 0;
    private Bitmap[] images;
    private String[] urls;
    private MyPagerAdapter adapter;
    private ACache cache;


    //处理事件
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message message)
        {
            if (pageview[currentitem].getDrawable() == null)
            {
                pageview[currentitem].setImageBitmap(images[currentitem]);
                adapter.notifyDataSetChanged();
            }
//            pageview[currentitem].setImage(images[currentitem]);
            viewpager.setCurrentItem(currentitem);

            AlphaAnimation aAnima = new AlphaAnimation(1.0f, 0.0f);//从全不透明变为全透明
            aAnima.setDuration(2000);
            aAnima.setFillAfter(true);
            textview.startAnimation(aAnima);
        }
    };

    public static ShowImageDialog getInstance(String[] urls, int currentitem)
    {
        ShowImageDialog dialog = new ShowImageDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArray("urls", urls);
        bundle.putInt("currentitem", currentitem);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置全屏，在onCreateDialog() 或 onCreateView()里设置无效
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        context = ContextApplication.getAppContext();

        View view = inflater.inflate(R.layout.dialog_showimage, container);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        textview = (TextView)view.findViewById(R.id.textview);

        init();

        return view;
    }


    private void init()
    {
        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            urls = bundle.getStringArray("urls");
            currentitem = bundle.getInt("currentitem");
        }

        if (urls.length > 0)
        {
            totalitem = urls.length;
            //缓存
            cache = ACache.get(context);

            pageview = new ZoomImageView[totalitem];
            images = new Bitmap[totalitem];

            ReadArticleActivity activity = (ReadArticleActivity) this.getActivity();

            for (int i = 0; i < totalitem; i++)
            {
                ZoomImageView imageView = new ZoomImageView(context);
                pageview[i] = imageView;
                images[i] = activity.images_hd.get(urls[i]);
            }

            //设置适配器
            adapter = new MyPagerAdapter();
            viewpager.setAdapter(adapter);
            MyPageChangeListener listener = new MyPageChangeListener();
            viewpager.setOnPageChangeListener(listener);

            show(currentitem);
        }
    }


    private void show(final int index)
    {
        currentitem = index;

        textview.setText((currentitem+1) + "/" + totalitem);

        if(images[index] == null)
        {
            //发现如果在UI线程访问图片缓存的话会存在一定卡顿的现象，那就和访问网络数据一样开一个线程吧
            new Thread()
            {
                public void run()
                {
                    Bitmap bitmap_local = cache.getAsBitmap(urls[index]);
                    if (bitmap_local == null)
                    {

                        AttachmentHelper helper = new AttachmentHelper();
                        Bitmap bitmap_remote = helper.get_Attachment_Image(urls[index] + BYR_BBS_API.returnFormat + BYR_BBS_API.appkey);
                        images[index] = bitmap_remote;

                        //缓存图片两分钟，若在时间内再次点击，则不用再次加载
                        cache.put(urls[index], bitmap_remote, 120);
                        handler.obtainMessage().sendToTarget();
                        EventBus.getDefault().post(new Event.Bitmap_HD(urls[index], bitmap_remote));

                    } else
                    {
                        images[index] = bitmap_local;
                        //缓存图片两分钟，若在时间内再次点击，则不用再次加载
                        cache.put(urls[index], bitmap_local, 120);

                        handler.obtainMessage().sendToTarget();
                        EventBus.getDefault().post(new Event.Bitmap_HD(urls[index], bitmap_local));
                    }
                }
            }.start();
        }
        else
        {
            if (pageview[currentitem].getDrawable() == null)
            {
                pageview[currentitem].setImageBitmap(images[currentitem]);
                adapter.notifyDataSetChanged();
            }
//            pageview[currentitem].setImage(images[currentitem]);
            viewpager.setCurrentItem(currentitem);

            AlphaAnimation aAnima = new AlphaAnimation(1.0f, 0.0f);//从全不透明变为全透明
            aAnima.setDuration(2000);
            aAnima.setFillAfter(true);
            textview.startAnimation(aAnima);
        }
    }

    //当ViewPager中页面的状态发生改变时调用
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageSelected(final int position)
        {
            show(position);
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    }

    //填充ViewPager页面的适配器
    private class MyPagerAdapter extends PagerAdapter
    {

        @Override
        public int getCount()
        {
            return urls.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(pageview[position]);
            return pageview[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
//            return view == pageview[Integer.parseInt(object.toString())];
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(pageview[position]);
        }
    }
}
