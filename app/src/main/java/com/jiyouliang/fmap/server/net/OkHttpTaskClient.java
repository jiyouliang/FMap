package com.jiyouliang.fmap.server.net;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttp网络请求相关
 */
public class OkHttpTaskClient extends BaseHttpTask {
    private static final String TAG = "OkHttpTaskClient";
    private static OkHttpTaskClient instance;

    private static OkHttpClient mOkHttpClient;
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";;

    private OkHttpTaskClient() {
        super();
    }


    /**
     * 单例
     *
     * @return
     */
    public static synchronized OkHttpTaskClient getInstance() {
        if (null == instance) {
            instance = new OkHttpTaskClient();
            initOkHttp();
        }
        return instance;
    }

    /**
     * get请求，TODO 还么有将参数设置到请求中
     *
     * @param url
     * @param json     json格式参数
     * @param listener
     * @deprecated 该方法已过期, 请使用
     * {@link #get(String, Map, BaseHttpResponse)}替代当前方法
     */
    @Deprecated
    @Override
    public void get(String url, String json, BaseHttpResponse listener) {
        setListener(listener);
        String fullUrl = getFullUrl(url);
        //构造请求对象
        Request request = new Request.Builder()
                .url(getFullUrl(fullUrl))//请求的url,10.0.3.2为genymotion访问本地服务器ip
                .get()
                .build();
        Call call = mOkHttpClient.newCall(request);
        executeTask(call);
    }

    /**
     * TODO 还么有将参数设置到请求中
     * get请求,该方法用于替换就版本{@link #get(String, String, BaseHttpResponse)},就版本get方法每次都需要
     * 手动生成timestamp和RSA sign.新版本get方法已将生成sign统一封装,避免每次都需要生成.
     *
     * @param url
     * @param params   Map格式参数,
     * @param listener
     */
    @Override
    void get(String url, Map<String, String> params, BaseHttpResponse listener) {
        setListener(listener);
        String fullUrl = getFullUrl(url);
        //构造请求对象
        Request request = new Request.Builder()
                .url(getFullUrl(fullUrl))//请求的url,10.0.3.2为genymotion访问本地服务器ip
                .get()
                .build();
        Call call = mOkHttpClient.newCall(request);
        executeTask(call);
    }


    /**
     * post请求
     *
     * @param url
     * @param json     json格式参数
     * @param listener
     * @deprecated 该方法已过期, 请使用{@link #post(String, String, BaseHttpResponse)}替代当前方法
     */
    @Deprecated
    @Override
    public void post(String url, String json, BaseHttpResponse listener) {
        setListener(listener);
        String fullUrl = getFullUrl(url);

        RequestBody requestBody = getRequestBody(json);
        //构造请求对象
        Request request = new Request.Builder()
                .url(fullUrl)//请求的url,10.0.3.2为genymotion访问本地服务器ip
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        executeTask(call);
    }

    /**
     * post请求,该方法用于替换就版本{@link #post(String, String, BaseHttpResponse)} )},就版本post方法每次都需要
     * 手动生成timestamp和RSA sign.新版本get方法已将生成sign统一封装,避免每次都需要生成.
     *
     * @param url
     * @param params   Map格式参数,
     * @param listener
     */
    @Override
    void post(String url, Map<String, String> params, Context context, BaseHttpResponse listener) throws Exception {
        setListener(listener);
        String fullUrl = getFullUrl(url);
        String json = assembleReqParams(context, params);
        RequestBody requestBody = getRequestBody(json);
        //构造请求对象
        Request request = new Request.Builder()
                .url(fullUrl)//请求的url,10.0.3.2为genymotion访问本地服务器ip
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        executeTask(call);
    }

    /**
     * 将json参数转成请求RequestBody对象
     * @param json
     * @return
     */
    @NotNull
    private RequestBody getRequestBody(String json) {
        return RequestBody.create(MediaType.parse(CONTENT_TYPE)
                    , json);
    }

    /**
     * 执行任务
     *
     * @param call
     */
    private void executeTask(Call call) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendFailedToTarger(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "Okhttp onResponse: thread name = " + Thread.currentThread().getName());
                ResponseBody body = response.body();
                if (body != null) {
                    sendSuccessToTarger(body.string());
                    return;
                }
                sendSuccessToTarger(null);
            }
        });
    }


    private static void initOkHttp() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS).build();
    }



}
