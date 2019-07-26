package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.HttpTaskClient;

/**
 * @author YouLiang.Ji
 * <p>
 * 短信验证码登录presenter层
 */
public class UserLoginSendSmsPresenter implements HttpTaskClient.OnHttpResponseListener<UserLoginData> {

    private IUserLoginSendSmsModel model;
    private IUserLoginSendSmsView mView;
    private Context mContext;
    /**
     * 请求码:登录
     */
    private static final int REQ_CODE_LOGIN = 0;
    /**
     * 请求码：发送验证码
     */
    private static final int REQ_CODE_SMS = 1;

    public UserLoginSendSmsPresenter(Context context, IUserLoginSendSmsView view) {
        this.mContext = context;
        this.mView = view;
        this.model = new UserLoginSendSmsModel();
    }

    /**
     * 短信验证码登录
     *
     * @param mobile
     * @param sms
     */
    public void loginBySms(String mobile, String sms) {
        mView.showLoading();
        try {
            model.loginBySms(mContext, mobile, sms, REQ_CODE_LOGIN, this);
        } catch (Exception e) {
            e.printStackTrace();
            mView.showUnknownError(e);
        }
    }

    /**
     * 发送短信验证码
     *
     * @param mobile
     */
    public void sendSms(String mobile) {
        mView.showLoading();
        try {
            model.sendSms(mContext, mobile, REQ_CODE_SMS, this);
        } catch (Exception e) {
            e.printStackTrace();
            mView.showUnknownError(e);
        }
    }

    @Override
    public void onException(Exception e) {
        mView.hideLoading();
        mView.showUnknownError(e);
    }

    @Override
    public void onResponse(int reqCode, UserLoginData response) {
        mView.hideLoading();
        switch (reqCode) {
            // 登录
            case REQ_CODE_LOGIN:
                //成功
                if (response.getCode() == 0) {
                    mView.loginSuccess(response);
                } else if (response.getCode() != 0) {
                    mView.loginFailed(response);
                    mView.showSmsCodeError();
                }
                break;
            // 发送验证码
            case REQ_CODE_SMS:
                if (response.getCode() == 0) {
                    mView.sendSmsSuccess();
                } else if (response.getCode() != 0) {
                    mView.sendSmsError(response);
                }
                break;
            default:
                //谷歌建议保留default
        }

    }

}
