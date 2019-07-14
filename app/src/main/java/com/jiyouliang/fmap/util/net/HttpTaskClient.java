package com.jiyouliang.fmap.util.net;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 网络请求相关
 */
public class HttpTaskClient {
    private static final String TAG = "HttpTaskClient";
    //连接超时时间
    private static final long TIMEOUT_CONNECT = 10;
    private static final long TIMEOUT_WRITE = 10;
    private static final long TIMEOUT_READ = 20;
    //核心线程数
    private static final int CORE_POOL_SIZE = 3;
    //最大线程数
    private static final int MAXIMUM_POOL_SIZE = 30;
    private static final long KEEP_ALIVE_TIME = 30;//秒钟，参考自AsyncTask,是非核心线程空闲时要等待下一个任务到来的时间;
    private static HttpTaskClient mClient = null;

    private static OkHttpClient mOkHttpClient;
    private static ThreadPoolExecutor mThreadPoolExecutor;
    private static InternalHandler sHandler;
    private Handler mHandler;
    private static final int SUCCESS = 0;
    private static final int FAILED = -1;

    private HttpTaskClient() {
    }

    public static HttpTaskClient getInstance() {
        if (null == mClient) {
            synchronized (HttpTaskClient.class) {
                if (null == mClient) {
                    mClient = new HttpTaskClient();
                    initOkHttp();
                    initThreadPool();
                    getMainHandler();
                }
            }
        }
        return mClient;
    }

    /**
     * get请求
     *
     * @param url
     * @param listener
     */
    public void get(String url, OnHttpResponseListener listener) {
        sHandler.setListener(listener);
        if (null == sHandler) {
            throw new IllegalArgumentException("Create Handler fail, hHandler can not be null.");
        }
        mHandler = sHandler;
        //构造请求对象
        Request request = new Request.Builder()
                .url(url)//请求的url,10.0.3.2为genymotion访问本地服务器ip
                .get()
                .build();
        Call call = mOkHttpClient.newCall(request);
        callOnThreadPool(call);
    }

    /**
     * post请求
     *
     * @param url
     * @param json 请求参数,json格式
     * @param listener
     */
    public void post(String url, String json, OnHttpResponseListener listener) {
        sHandler.setListener(listener);
        if (null == sHandler) {
            throw new IllegalArgumentException("Create Handler fail, hHandler can not be null.");
        }
        mHandler = sHandler;
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        //构造请求对象
        Request request = new Request.Builder()
                .url(url)//请求的url,10.0.3.2为genymotion访问本地服务器ip
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        callOnThreadPool(call);
    }

    private static void getMainHandler() {
        synchronized (AsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler(Looper.getMainLooper());
            }
        }
    }

    /**
     * 初始化线程池
     */
    private static void initThreadPool() {
        //阻塞队列
        LinkedBlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<Runnable>(30);
        mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);

    }

    private static void initOkHttp() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS).build();
    }


    /**
     * 线程池请求网络
     *
     * @param call
     */
    private void callOnThreadPool(final Call call) {
        // okhttp已经处理异步，并且在线程池中
       /* mThreadPoolExecutor.execute(sThreadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        mHandler.obtainMessage(FAILED, e).sendToTarget();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.d(TAG, "Okhttp onResponse: thread name = " + Thread.currentThread().getName());
                        ResponseBody body = response.body();
                        if (body != null) {
                            mHandler.obtainMessage(SUCCESS, body.string()).sendToTarget();
                            return;
                        }
                        mHandler.obtainMessage(SUCCESS, null).sendToTarget();
                    }
                });

            }
        }));*/

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.obtainMessage(FAILED, e).sendToTarget();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "Okhttp onResponse: thread name = " + Thread.currentThread().getName());
                ResponseBody body = response.body();
                if (body != null) {
                    mHandler.obtainMessage(SUCCESS, body.string()).sendToTarget();
                    return;
                }
                mHandler.obtainMessage(SUCCESS, null).sendToTarget();
            }
        });

    }

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread #" + mCount.getAndIncrement());
        }
    };


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
}
