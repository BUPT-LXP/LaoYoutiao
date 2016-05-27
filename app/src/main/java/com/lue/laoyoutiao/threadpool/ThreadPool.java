package com.lue.laoyoutiao.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lue on 2016/5/26.
 */
public class ThreadPool
{
    //总共多少任务（根据CPU个数决定创建活动线程的个数,这样取的好处就是可以让手机承受得住）
    private static final int threadcount = Runtime.getRuntime().availableProcessors();
    //每次只执行一个任务的线程池
    private volatile static ExecutorService singleTaskExecutor = null;
    //每次执行限定个数个任务的线程池
    private volatile static ExecutorService fixedTaskExecutor = null;



    public static ExecutorService getSingleTaskExecutor()
    {
        if(singleTaskExecutor == null)
        {
            synchronized(ExecutorService.class)
            {
                if(singleTaskExecutor == null)
                {
                    singleTaskExecutor = Executors.newSingleThreadExecutor();
                }
            }
        }
        return singleTaskExecutor;
    }

    public static ExecutorService getFixedTaskExecutor()
    {
        if(fixedTaskExecutor == null)
        {
            synchronized (ExecutorService.class)
            {
                if(fixedTaskExecutor == null)
                {
                    fixedTaskExecutor = Executors.newFixedThreadPool(threadcount);
                }
            }
        }
        return fixedTaskExecutor;
    }
}
