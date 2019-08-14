package com.jiyouliang.fmap.ui.user;

/**
 * @author YouLiang.Ji
 * 用户登录view接口
 */
public interface IUserLoginView {

    /**
     * 手机号不正确
     */
    void showMoibleError();

    /**
     * 显示加载中Dialog
     *//*
    void showLoadingDialog();*/

    /**
     * 隐藏加载中Dialog
     */
    void hideLoadingDialog();

    /**
     * 显示发送验证码Button动画
     */
    void showLoadingBtn();

    /**
     * 显示常规状态Button
     */
    void showNormalBtn();

    /**
     * 发送验证码成功,显示下一个Activity页面
     */
    void sendSmsSuccess();

    /**
     * 发送验证异常
     */
    void sendSmsException(Exception e);

    /**
     * 发送验证码失败
     * @param erroMsg
     */
    void sendSmsFailed(String erroMsg);

    /**
     * 关闭软键盘
     */
    void hideInput();
}
