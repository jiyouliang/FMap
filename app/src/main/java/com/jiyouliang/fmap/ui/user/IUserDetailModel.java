package com.jiyouliang.fmap.ui.user;

/**
 * @author YouLiang.Ji
 * 用户详情model层接口
 */
public interface IUserDetailModel {

    /**
     * 存储手机号到sharedPreference
     */
    void savePhoneToSP(String phone);


    /**
     * 从sharedPreference中获取存储的手机号码
     */
    String getPhoneFromSP();
}
