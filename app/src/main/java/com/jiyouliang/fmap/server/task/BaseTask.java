package com.jiyouliang.fmap.server.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 异步任务基类,继承该基类重写{@link #getRunnable()}方法返回Runnable对象,
 * 根据个人需求在run方法中执行耗时操作,通过{@link #callOnThreadPool()}方法触发异步任务
 */
public abstract class BaseTask {

    //连接超时时间
    protected static final long TIMEOUT_CONNECT = 10;
    protected static final long TIMEOUT_WRITE = 10;
    protected static final long TIMEOUT_READ = 20;
    //核心线程数
    protected static final int CORE_POOL_SIZE = 3;
    //最大线程数
    protected static final int MAXIMUM_POOL_SIZE = 30;
    protected static final long KEEP_ALIVE_TIME = 30;//秒钟，参考自AsyncTask,是非核心线程空闲时要等待下一个任务到来的时间;

    private static ThreadPoolExecutor mThreadPoolExecutor;

    private static InternalHandler sHandler;
    private Handler mHandler;
    private static final int SUCCESS = 0;
    private static final int FAILED = -1;

    protected BaseTask() {
        getMainHandler();
        initThreadPool();
    }


    protected void setListener(TaskResponseListener listener) {
        sHandler.setListener(listener);
        if (null == sHandler) {
            throw new IllegalArgumentException("Create Handler fail, hHandler can not be null.");
        }
        mHandler = sHandler;
    }

    private static class InternalHandler extends Handler {
        private TaskResponseListener listener;

        public void setListener(TaskResponseListener listener) {
            this.listener = listener;
        }

        public InternalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String response = (String) msg.obj;
                    listener.onSuccess(response);
                    break;
                case FAILED:
                    Exception e = (Exception) msg.obj;
                    listener.onFailed(e);
                    break;
            }
        }
    }

    private static void getMainHandler() {
        synchronized (AsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler(Looper.getMainLooper());
            }
        }
    }

    /**
     * 通过handler发送success回调
     *
     * @param response
     */
    protected void sendSuccessToTarger(String response) {
        mHandler.obtainMessage(SUCCESS, response).sendToTarget();
    }

    /**
     * 通过handler发送failed回调
     *
     * @param e
     */
    protected void sendFailedToTarger(Exception e) {
        mHandler.obtainMessage(FAILED, e).sendToTarget();
    }


    /**
     * 初始化线程池
     */
    private static void initThreadPool() {
        //阻塞队列
        LinkedBlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<Runnable>(30);
        mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);

    }

    /**
     * 在线程池中调用
     */
    protected void callOnThreadPool() {
        Runnable r = getRunnable();
        if(r == null){
            throw new NullPointerException("Runnable对象r不能为空,请重写getRunnable方法并返回非空getRunnable");
        }
        mThreadPoolExecutor.execute(sThreadFactory.newThread(r));
    }


    /**
     * 返回Runnable对象,线程池通过该对象构造Thread并添加到线程池,在线程池中执行耗时操作
     * @return Runnable
     */
    abstract Runnable getRunnable();

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread #" + mCount.getAndIncrement());
        }
    };

    /**
     * 任务回调接口
     */
    public interface TaskResponseListener<T> {
        /**
         * 失败
         *
         * @param e
         */
        void onFailed(Exception e);

        /**
         * 成功
         *
         * @param t
         */
        void onSuccess(T t);
    }

}
