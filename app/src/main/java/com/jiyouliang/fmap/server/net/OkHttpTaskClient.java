package com.jiyouliang.fmap.server.net;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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


    @Override
    public void post(String url, String json, BaseHttpResponse listener) {
        setListener(listener);
        String fullUrl = getFullUrl(url);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        //构造请求对象
        Request request = new Request.Builder()
                .url(fullUrl)//请求的url,10.0.3.2为genymotion访问本地服务器ip
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        executeTask(call);
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
