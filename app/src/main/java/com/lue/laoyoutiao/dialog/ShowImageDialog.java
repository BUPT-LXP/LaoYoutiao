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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lue.laoyoutiao.R;
import com.lue.laoyoutiao.cache.ACache;
import com.lue.laoyoutiao.global.ContextApplication;
import com.lue.laoyoutiao.network.PicassoHelper;
import com.lue.laoyoutiao.sdkutil.BYR_BBS_API;
import com.lue.laoyoutiao.threadpool.ThreadPool;
import com.lue.laoyoutiao.view.ZoomImageView;

import java.util.concurrent.ExecutorService;

/**
 * Created by Lue on 2016/5/20.
 */
public class ShowImageDialog extends DialogFragment implements ZoomImageView.OnImageClickListner
{
    private Context context;
    private ViewPager viewpager;
    private TextView textview;
    private ZoomImageView[] pageview;
    private int currentitem = 0;
    private int totalitem = 0;
    private Bitmap[] images;
    private String[] urls;
    private float[] sizes;
    private MyPagerAdapter adapter;
    private static ExecutorService singleTaskExecutor;
    private ACache cache;

    private FrameLayout framecontainer;
    private ProgressBar progressBar;


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
            progressBar.setVisibility(View.GONE);
            viewpager.setCurrentItem(currentitem);

            AlphaAnimation aAnima = new AlphaAnimation(1.0f, 0.0f);//从全不透明变为全透明
            aAnima.setDuration(2000);
            aAnima.setFillAfter(true);
            textview.startAnimation(aAnima);
        }
    };

    public static ShowImageDialog getInstance(String[] urls, int currentitem, float[] sizes)
    {
        singleTaskExecutor = ThreadPool.getSingleTaskExecutor();
        ShowImageDialog dialog = new ShowImageDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArray("urls", urls);
        bundle.putInt("currentitem", currentitem);
        bundle.putFloatArray("sizes", sizes);
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

        framecontainer = (FrameLayout)view.findViewById(R.id.container);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        textview = (TextView) view.findViewById(R.id.textview);

        // 给progressbar准备一个FrameLayout的LayoutParams
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置对其方式为：屏幕居中对其
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        progressBar = new ProgressBar(this.getActivity());
        progressBar.setVisibility(View.GONE);
        progressBar.setLayoutParams(lp);
        framecontainer.addView(progressBar);

        cache = ACache.get(context);

        init();

        return view;
    }


    private void init()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            urls = bundle.getStringArray("urls");
            currentitem = bundle.getInt("currentitem");
            sizes = bundle.getFloatArray("sizes");
        }

        if (urls.length > 0)
        {
            totalitem = urls.length;

            pageview = new ZoomImageView[totalitem];
            images = new Bitmap[totalitem];

            for (int i = 0; i < totalitem; i++)
            {
                ZoomImageView imageView = new ZoomImageView(context);
                imageView.setOnImageClickListner(this);
                pageview[i] = imageView;
            }

            //设置适配器
            adapter = new MyPagerAdapter();
            viewpager.setAdapter(adapter);
            MyPageChangeListener listener = new MyPageChangeListener();
            viewpager.setOnPageChangeListener(listener);

            showImage(currentitem);
        }
    }


    private void showImage(final int index)
    {
        currentitem = index;

        textview.setText((currentitem+1) + "/" + totalitem);

        if(images[index] == null)
        {
            progressBar.setVisibility(View.VISIBLE);
            singleTaskExecutor.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    int zoom;
                    float f = sizes[index] / 200.0f;
                    zoom = (int)f + 1;

//                    if(sizes[index] > 200.0f)
//                    {
//                        //图片较大
//                        zoom = 3;
//                    }
//                    else
//                    {
//                        zoom = 1;
//                    }


                    Bitmap bitmap = cache.getAsBitmap(urls[index]);
                    if(bitmap == null)
                    {
                        bitmap = PicassoHelper.getPicassoHelper().getBitmap
                                (urls[index] + BYR_BBS_API.returnFormat + BYR_BBS_API.appkey, zoom);

                        cache.put(urls[index], bitmap, 2*60);
                    }

                    images[index] = bitmap;

                    handler.obtainMessage().sendToTarget();

                }
            });
        }
        else
        {
            if (pageview[currentitem].getDrawable() == null)
            {
                pageview[currentitem].setImageBitmap(images[currentitem]);
                adapter.notifyDataSetChanged();
            }
            viewpager.setCurrentItem(currentitem);

            AlphaAnimation aAnima = new AlphaAnimation(1.0f, 0.0f);//从全不透明变为全透明
            aAnima.setDuration(2000);
            aAnima.setFillAfter(true);
            textview.startAnimation(aAnima);
        }
    }

    @Override
    public void onClick()
    {
        dismiss();
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
            showImage(position);
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
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(pageview[position]);
        }
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        for(int i=0; i<totalitem; i++)
        {
            pageview[i].setImageDrawable(null);
            if(images[i] != null)
                images[i].recycle();
        }
    }
}
