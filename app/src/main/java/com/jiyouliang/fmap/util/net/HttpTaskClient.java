package com.jiyouliang.fmap.util.net;

/**
 * 网络请求相关
 * @author jiyouliang
 */
public class HttpTaskClient {
    private static final String TAG = "HttpTaskClient";
    private static HttpTaskClient instance;

    //发送短信验证码接口
    private static final String URL_SEND_SMS = "sms/sendSms";


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

    public void get(String url, String json, BaseHttpTask.OnHttpResponseListener listener) {
        //调用OkHttp请求网络,后期如果需要修改，至今修改get方法获取post方法即可
        OkHttpTaskClient.getInstance().get(url, json, listener);

    }

    public void post(String url, String json, BaseHttpTask.OnHttpResponseListener listener) {
        OkHttpTaskClient.getInstance().post(url, json, listener);
    }

    /**
     * 发送短信验证码
     * @param json
     * @param listener
     */
    public void sendSms(String json, BaseHttpTask.OnHttpResponseListener listener){
        post(URL_SEND_SMS, json, listener);
    }




}
