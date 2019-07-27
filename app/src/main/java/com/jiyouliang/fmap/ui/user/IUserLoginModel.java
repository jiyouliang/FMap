package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.HttpTaskClient;

/**
 * @author YouLiang.Ji
 * <p>
 * 用户登录Model层接口
 */
public interface IUserLoginModel {
    /**
     * 发送短信验证码
     *
     * @param phone
     */
    void sendSms(String phone, int reqCode, Context context, HttpTaskClient.OnHttpResponseListener<UserLoginData> listener);

    /**
     * 校验手机号格式
     * @param phone
     * @return
     */
    boolean verifyPhone(String phone);
}
