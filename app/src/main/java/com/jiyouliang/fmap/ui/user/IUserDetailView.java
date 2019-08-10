package com.jiyouliang.fmap.ui.user;

/**
 * @author YouLiang.Ji
 * 用户详情View层接口
 */
public interface IUserDetailView {

    /**
     * 从sp中获取存储手机号成功,View层刷新UI
     */
    void onGetPhoneSuccess(String phone);

    /**
     * 从sp中获取存储手机号失败,可能手机号不存在获取数据存在异常
     */
    void onGetPhoneFailed(String msg);
}
