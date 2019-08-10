package com.jiyouliang.fmap.ui.user;

import android.content.Context;

/**
 * @author YouLiang.Ji
 *
 * 用户详情presenter层,使用MVP是为了后续扩展与维护,考虑到UserDetailFragment目前没有网络请求
 * 模块,暂时不使用MVP TODO
 */
public class UserDetailPresenter {

    private IUserDetailModel mModel;
    private IUserLoginView mView;

    public UserDetailPresenter(Context context, IUserLoginView mView) {
        this.mView = mView;
        this.mModel = new UserDetailModel(context);
    }

    /**
     * 存储手机号
     * @param phone
     */
    public void savePhone(String phone){
        mModel.savePhoneToSP(phone);
    }

    /**
     * 获取存储的手机号
     */
    public void getPhone(){
       mModel.getPhoneFromSP();
    }
}
