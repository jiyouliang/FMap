package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.BaseHttpTask;
import com.jiyouliang.fmap.server.net.HttpTaskClient;

/**
 * @author YouLiang.Ji
 * <p>
 * 短信验证码登录presenter层
 */
public class UserLoginSendSmsPresenter implements HttpTaskClient.OnHttpResponseListener<UserLoginData> {

    private IUserLoginSendSmsModel model;
    private IUserLoginSendSmsView view;
    private Context mContext;

    public UserLoginSendSmsPresenter(Context context, IUserLoginSendSmsView view) {
        this.mContext = context;
        this.view = view;
        this.model = new UserLoginSendSmsModel();
    }

    /**
     * 短信验证码登录
     * @param mobile
     * @param sms
     */
    public void loginBySms(String mobile, String sms){
        view.showLoading();
        try {
            model.loginBySms(mContext, mobile, sms, this);
        } catch (Exception e) {
            e.printStackTrace();
            view.showUnknownError(e);
        }
    }

    @Override
    public void onException(Exception e) {
        view.hideLoading();
        view.showUnknownError(e);
    }

    @Override
    public void onResponse(UserLoginData response) {
        view.hideLoading();
        //成功
        if(response.getCode() == 0){
            view.loginSuccess(response);
        }else if(response.getCode() != 0){
            view.loginFailed(response);
        }
    }

}
