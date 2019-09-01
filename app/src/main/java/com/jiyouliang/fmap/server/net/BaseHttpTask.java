package com.jiyouliang.fmap.server.net;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.jiyouliang.fmap.util.Constants;
import com.jiyouliang.fmap.util.DeviceUtils;
import com.jiyouliang.fmap.util.security.RSACrypt;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;


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
     * @deprecated 该方法已过期, 请使用
     * {@link #get(String, Map, BaseHttpResponse)}替代当前方法
     */
    @Deprecated
    abstract void get(String url, String json, BaseHttpResponse listener);

    /**
     * get请求,该方法用于替换就版本{@link #get(String, String, BaseHttpResponse)},就版本get方法每次都需要
     * 手动生成timestamp和RSA sign.新版本get方法已将生成sign统一封装,避免每次都需要生成.
     *
     * @param url
     * @param params   Map格式参数,
     * @param listener
     */
    abstract void get(String url, Map<String, String> params, BaseHttpResponse listener);


    /**
     * post请求
     *
     * @param url
     * @param json     json格式参数
     * @param listener
     * @deprecated 该方法已过期, 请使用{@link #post(String, Map, Context, BaseHttpResponse)}替代当前方法
     */
    @Deprecated
    abstract void post(String url, String json, BaseHttpResponse listener);


    /**
     * post请求,该方法用于替换就版本{@link #post(String, String, BaseHttpResponse)} )},就版本post方法每次都需要
     * 手动生成timestamp和RSA sign.新版本get方法已将生成sign统一封装,避免每次都需要生成.
     *
     * @param url
     * @param params   Map格式参数,
     * @param listener
     */
    abstract void post(String url, Map<String, String> params, Context context, BaseHttpResponse listener) throws Exception;

    protected void setListener(BaseHttpResponse listener) {
        sHandler.setListener(listener);
        if (null == sHandler) {
            throw new IllegalArgumentException("Create Handler fail, hHandler can not be null.");
        }
        mHandler = sHandler;
    }

    private static class InternalHandler extends Handler {
        private BaseHttpResponse listener;

        public void setListener(BaseHttpResponse listener) {
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
     * 网络请求回调
     *
     * @see BaseHttpResponse
     * @deprecated 该回调接口已过期, 请使用下面Listener替换
     */
    @Deprecated
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
     * 回调接口：该回调仅用于BaseHttpTask第一层子类,其他类请使用{@link HttpTaskClient.OnHttpResponseListener}替代
     * 这是用于服务器返回数据需要通过Class<T>泛型生成对象对象Bean,而BaseTask中的内部类InternalHandler为static类型,
     * 无法使用泛型(static class不支持泛型,会擦除泛型)
     */
    public interface BaseHttpResponse {
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
     *
     * @param url
     * @return
     */
    public String getFullUrl(String url) {
        return getUrlPrefix() + url;
    }


    /**
     * 组装网络请求参数,通过参数timestamp和sign请勿传递进来
     *
     * @return
     */
    protected String assembleReqParams(Context context, Map<String, String> map) throws Exception {
        //遍历key和value,存储到json作为请求参数
        JSONObject json = getCommonJsonObject(context);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry == null) {
                continue;
            }
            String key = entry.getKey();
            String value = entry.getValue();
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                continue;
            }

            json.put(key, value);
        }
        return json.toString();
    }

    /**
     * 组装网络请求通用json对象,该对象存储sign和timestame两个字段,避免每次发起网络请求都需要重复添加
     *
     * @return
     */
    private JSONObject getCommonJsonObject(Context context) throws Exception {
        JSONObject json = new JSONObject();
        String timestamp = DeviceUtils.getTimestampStr();
        String sign = RSACrypt.genSign(context, timestamp);
        json.put("timestamp", timestamp);
        json.put("sign", sign);
        return json;
    }

}
