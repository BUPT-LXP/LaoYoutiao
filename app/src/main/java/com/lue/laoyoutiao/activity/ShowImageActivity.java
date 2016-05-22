package com.lue.laoyoutiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.cache.ACache;
import com.lue.laoyoutiao.eventtype.Event;
import com.lue.laoyoutiao.helper.AttachmentHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Lue on 2016/5/20.
 * 参考 ： http://blog.csdn.net/t12x3456/article/details/8160128
 */
public class ShowImageActivity extends AppCompatActivity
{
    private ViewPager viewpager;
    private ImageView[] pageview;
    private int currentitem = 0;
    private Bitmap[] images ;
    private String[] urls;
    private MyPagerAdapter adapter;
    private ACache cache;

    //处理事件
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message message)
        {
            if(pageview[currentitem].getDrawable() == null)
            {
                pageview[currentitem].setImageBitmap(images[currentitem]);
                adapter.notifyDataSetChanged();
            }
            viewpager.setCurrentItem(currentitem);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_showimage);

        viewpager = (ViewPager) findViewById(R.id.viewpager);

        Intent intent = getIntent();
        urls = intent.getStringArrayExtra("urls");
        final int index = intent.getIntExtra("index", 0);

        if (urls.length > 0)
        {
            //缓存
            cache = ACache.get(this);

            pageview = new ImageView[urls.length];
            images = new Bitmap[urls.length];

//            images = (Bitmap[]) intent.getParcelableArrayExtra("images");

            List<Bitmap> image_list;
            image_list = intent.getParcelableArrayListExtra("images");

            for (int i = 0; i < urls.length; i++)
            {
                ImageView imageView = new ImageView(this);
                pageview[i] = imageView;

                images[i] = image_list.get(i);
            }

            //设置适配器
            adapter = new MyPagerAdapter();
            viewpager.setAdapter(adapter);
            MyPageChangeListener listener = new MyPageChangeListener();
            viewpager.setOnPageChangeListener(listener);

            show(index);
        }
    }

    private void show(final int index)
    {
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
                        currentitem = index;

                        //缓存图片两分钟，若在时间内再次点击，则不用再次加载
                        cache.put(urls[index], bitmap_remote, 120);
                        handler.obtainMessage().sendToTarget();

                        boolean flag = BYR_BBS_API.saveImage(urls[index], bitmap_remote);
                        Log.d("ShowImageActivity" , flag+"");

                        EventBus.getDefault().post(new Event.Bitmap_HD(urls[index], bitmap_remote));

                    } else
                    {
                        images[index] = bitmap_local;
                        currentitem = index;
                        //缓存图片两分钟，若在时间内再次点击，则不用再次加载
                        cache.put(urls[index], bitmap_local, 120);

                        handler.obtainMessage().sendToTarget();

                        boolean flag = BYR_BBS_API.saveImage(urls[index], bitmap_local);
                        Log.d("ShowImageActivity" , flag+"");
                        EventBus.getDefault().post(new Event.Bitmap_HD(urls[index], bitmap_local));
                    }
                }
            }.start();
        }
        else
        {
            viewpager.setCurrentItem(index);
        }
    }

    //当ViewPager中页面的状态发生改变时调用
    private class MyPageChangeListener implements OnPageChangeListener
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
            return position;
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == pageview[Integer.parseInt(object.toString())];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(pageview[position]);
        }
    }


    /**
     * 重写按下返回键的响应
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
//        for(ImageView imageView : pageview)
//        {
//            imageView.setImageBitmap(null);
//        }
//
//        for(Bitmap image : images)
//        {
//            if(image != null)
//                image.recycle();
//        }
//        System.gc();
//        finish();
    }
}
