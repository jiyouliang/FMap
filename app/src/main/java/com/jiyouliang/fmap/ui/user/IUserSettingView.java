package com.jiyouliang.fmap.ui.user;

import com.jiyouliang.fmap.server.data.UserLoginData;

/**
 * @author YouLiang.Ji
 *
 * 用户设置中心View接口
 */
public interface IUserSettingView {

    /**
     * 显示加载进度条
     */
    void showLoading();

    /**
     * 隐藏加载进度条
     */
    void hideLoading();

    /**
     * 注销成功
     */
    void onLogoutSuccess(UserLoginData response);

    /**
     * 注销失败
     * @param e
     */
    void onLogoutFailed(Exception e);
}
