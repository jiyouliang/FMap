package com.jiyouliang.fmap.ui.user;

import com.jiyouliang.fmap.server.data.UserLoginData;

/**
 * @author YouLiang.Ji
 * 用户登录发送短信验证码View模块
 */
public interface IUserLoginSendSmsView {

    /**
     * 发送验证码到服务器显示加载中状态
     */
    void showLoading();

    /**
     * 隐藏加载中状态
     */
    void hideLoading();

    /**
     * 验证码错误:红色文字提示+左右摇摆动画
     */
    void showSmsCodeError();


    /**
     * 登录成功,跳转用户主页
     */
    void loginSuccess(UserLoginData userData);

    /**
     * 登录失败,提示登录失败原因,失败原因通过服务器返回获得
     */
    void loginFailed(UserLoginData userData);

    /**
     * 未知异常
     * @param e
     */
    void showUnknownError(Exception e);

    /**
     * 验证码发送成功
     */
    void sendSmsSuccess();

    /**
     * 验证码发送失败
     * @param response
     */
    void sendSmsError(UserLoginData response);
}
