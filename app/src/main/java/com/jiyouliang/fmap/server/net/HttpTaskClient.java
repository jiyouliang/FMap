package com.jiyouliang.fmap.server.net;

import com.alibaba.fastjson.JSON;
import com.jiyouliang.fmap.util.LogUtil;

/**
 * 网络请求相关
 *
 * @author jiyouliang
 */
public class HttpTaskClient {
    private static final String TAG = "HttpTaskClient";
    private static HttpTaskClient instance;

    //发送短信验证码接口
    private static final String URL_SEND_SMS = "sms/sendSms";

    /**
     * 短信验证码登录
     */
    private static final String URL_LOGIN_SMS = "user/loginBySmsCode";


    private HttpTaskClient() {
    }

    public static HttpTaskClient getInstance() {
        if (null == instance) {
            synchronized (HttpTaskClient.class) {
                if (null == instance) {
                    instance = new HttpTaskClient();
                }
            }
        }
        return instance;
    }

    public <T> void get(String url, String json, final int reqCode, final Class<T> clazz, final OnHttpResponseListener<T> listener) {
        LogUtil.d(TAG, String.format("get request url=%s, json=%s", url, json));
        //调用OkHttp请求网络,后期如果需要修改，至今修改get方法获取post方法即可
        OkHttpTaskClient.getInstance().get(url, json, new BaseHttpTask.BaseHttpResponse() {
            @Override
            public void onFailed(Exception e) {
                listener.onException(e);
                if (e != null) {
                    LogUtil.d(TAG, String.format("get method onException %s", e.getMessage()));
                }
            }

            @Override
            public void onSuccess(String response) {
                listener.onResponse(reqCode, parseToObject(response, clazz));
                if (response != null) {
                    LogUtil.d(TAG, String.format("get method onSuccess %s", response));
                }
            }
        });

    }

    public <T> void post(String url, String json, final int reqCode, final Class<T> clazz, final OnHttpResponseListener<T> listener) {
        LogUtil.d(TAG, String.format("post request url=%s, json=%s", url, json));
        OkHttpTaskClient.getInstance().post(url, json, new BaseHttpTask.BaseHttpResponse() {
            @Override
            public void onFailed(Exception e) {
                listener.onException(e);
                if (e != null) {
                    LogUtil.d(TAG, String.format("post method onException %s", e.getMessage()));
                }
            }

            @Override
            public void onSuccess(String response) {
                listener.onResponse(reqCode, parseToObject(response, clazz));
                if (response != null) {
                    LogUtil.d(TAG, String.format("post method onSuccess %s", response));
                }
            }
        });
    }


    /**
     * 发送短信验证码
     *
     * @param json
     * @param listener
     */
    public <T> void sendSms(String json, final int reqCode, Class<T> clazz, OnHttpResponseListener<T> listener) {
        post(URL_SEND_SMS, json, reqCode, clazz, listener);
    }


    /**
     * 短信验证码登录
     *
     * @param json
     * @param listener
     */
    public <T> void loginBySms(String json, final int reqCode, Class<T> clazz, OnHttpResponseListener<T> listener) {
        post(URL_LOGIN_SMS, json, reqCode, clazz, listener);
    }

    /**
     * 网络请求回调接口
     */
    public interface OnHttpResponseListener<T> {

        /**
         * 请求异常
         *
         * @param e
         */
        void onException(Exception e);

        /**
         * 服务器成功返回数据,客户端需要根据错误状态码code判断成功与失败
         *
         * @param reqCode  请求状态码,用于标识不同请求,以便同一个回调方法处理不通请求
         * @param response 对于数据Bean对象
         */
        void onResponse(int reqCode, T response);
    }

    /**
     * 通过fastjson将字符串解析为bean
     *
     * @param data
     * @param clazz
     * @return
     */
    private <T> T parseToObject(String data, Class<T> clazz) {
        return JSON.parseObject(data, clazz);
    }


}
