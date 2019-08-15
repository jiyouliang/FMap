package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.jiyouliang.fmap.server.net.HttpTaskClient;

/**
 * @author YouLiang.Ji
 * <p>
 * 用户登录发送短信验证码Model模块,处理具体业务逻辑
 */
public interface IUserLoginSendSmsModel<UserLoginData> {

    /**
     * 通过短信验证码登录,实现该方法子类发送网络请求,调用服务器api接口
     *
     * @param context
     * @param mobile
     * @param sms
     * @param reqCode
     * @param listener
     * @throws Exception
     */
    void loginBySms(Context context, String mobile, String sms, int reqCode, HttpTaskClient.OnHttpResponseListener listener) throws Exception;

    /**
     * 将服务器返回json字符串解析为Bean
     *
     * @param json
     */
    UserLoginData parseData(String json);

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @param reqCode
     */
    void sendSms(Context context, String mobile, int reqCode, HttpTaskClient.OnHttpResponseListener listener) throws Exception;
}
