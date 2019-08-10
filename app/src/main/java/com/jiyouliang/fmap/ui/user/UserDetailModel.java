package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.text.TextUtils;

import com.jiyouliang.fmap.server.task.SharedPreferencesTask;

/**
 * @author YouLiang.Ji
 */
public class UserDetailModel implements IUserDetailModel {

    private final Context context;
    private SharedPreferencesTask mSpTask;

    public UserDetailModel(Context context) {
        this.context = context;
        this.mSpTask = new SharedPreferencesTask();
    }

    @Override
    public void savePhoneToSP(String phone) {
        if(TextUtils.isEmpty(phone)){
            return;
        }
        //已存储过该手机号
        if(phone.equals(getPhoneFromSP())){
            return;
        }
        mSpTask.saveUserPhone(context, phone);
    }


    @Override
    public String getPhoneFromSP() {
        return mSpTask.getUserPhone(context);
    }

    /**
     * 处理数据回调:获取手机号会遇到几种情况
     * 1.成功
     * 2.不存在手机号/手机号码被修改过不符合手机号格式
     */
    public interface OnProcessDataListener{
        void onSuccess(String phone);

        void onFailed(String msg);
    }
}
