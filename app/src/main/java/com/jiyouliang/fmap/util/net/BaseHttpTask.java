package com.jiyouliang.fmap.util.net;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jiyouliang.fmap.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class BaseHttpTask {

    //连接超时时间
    protected static final long TIMEOUT_CONNECT = 10;
    protected static final long TIMEOUT_WRITE = 10;
    protected static final long TIMEOUT_READ = 20;
    //核心线程数
    protected static final int CORE_POOL_SIZE = 3;
    //最大线程数
    protected static final int MAXIMUM_POOL_SIZE = 30;
    protected static final long KEEP_ALIVE_TIME = 30;//秒钟，参考自AsyncTask,是非核心线程空闲时要等待下一个任务到来的时间;

    private static OkHttpClient mOkHttpClient;
    private static ThreadPoolExecutor mThreadPoolExecutor;

    private static InternalHandler sHandler;
    private Handler mHandler;
    private static final int SUCCESS = 0;
    private static final int FAILED = -1;
    //服务器ip
    private static final String LOCAL_HOST = "http://10.0.3.2";
    private static final String HOST = "http://47.106.182.74";
    //服务器端口号
    private static final String PORT = "8000";
    //服务器名称
    private static final String SERVICE_NAME = "/fmap/";

    protected BaseHttpTask() {
        getMainHandler();
        initThreadPool();
    }

    /**
     * get请求
     *
     * @param url
     * @param json     json格式参数
     * @param listener
     */
    abstract void get(String url, String json, OnHttpResponseListener listener);

    /**
     * post请求
     *
     * @param url
     * @param json     json格式参数
     * @param listener
     */
    abstract void post(String url, String json, OnHttpResponseListener listener);

    protected void setListener(OnHttpResponseListener listener) {
        sHandler.setListener(listener);
        if (null == sHandler) {
            throw new IllegalArgumentException("Create Handler fail, hHandler can not be null.");
        }
        mHandler = sHandler;
    }

    private static class InternalHandler extends Handler {
        private OnHttpResponseListener listener;

        public void setListener(OnHttpResponseListener listener) {
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
     * 线程池请求网络
     */
    protected void callOnThreadPool() {
        // TODO 未来添加
    }

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread #" + mCount.getAndIncrement());
        }
    };

    /**
     * 回调
     */
    public interface OnHttpResponseListener {
        /**
         * 失败
         *
         * @param e
         */
        void onFailed(Exception e);

        /**
         * 成功
         *
         * @param response
         */
        void onSuccess(String response);
    }

    /**
     * 接口前缀
     *
     * @return
     */
    public String getUrlPrefix() {
        String host = Constants.RELEASE_MODE ? HOST : LOCAL_HOST;
        return host + ":" + PORT + SERVICE_NAME;
    }

    /**
     * 获取完整url请求
     * @param url
     * @return
     */
    public String getFullUrl(String url){
        return getUrlPrefix() + url;
    }
}
