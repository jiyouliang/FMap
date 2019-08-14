package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.HttpTaskClient;

/**
 * @author YouLiang.Ji
 *
 * 用户登录presenter
 */
public class UserLoginPresenter {

    private static final int REQ_CODE_SMS = 0;
    private IUserLoginModel mModel;
    private IUserLoginView mView;
    private Context mContext;

    public UserLoginPresenter(IUserLoginView mView, Context mContext) {
        this.mModel = new UserLoginModel();
        this.mView = mView;
        this.mContext = mContext;
    }

    /**
     * 发送短信验证码
     * @param phone
     */
    public void sendSms(String phone){
        //校验手机号
        if(!mModel.verifyPhone(phone)){
            mView.showMoibleError();
            return;
        }
        mView.showLoadingBtn();
        mView.hideInput();
        mModel.sendSms(phone, REQ_CODE_SMS, mContext, new HttpTaskClient.OnHttpResponseListener<UserLoginData>() {
            @Override
            public void onException(Exception e) {
                mView.sendSmsException(e);
                mView.showNormalBtn();
            }

            @Override
            public void onResponse(int reqCode, UserLoginData response) {
                mView.showNormalBtn();
                if(null == response){
                    mView.sendSmsException(new Exception("UserLoginData can't be null"));
                    return;
                }
                if(response.getCode() == 0){
                    mView.sendSmsSuccess();
                }else{
                    mView.sendSmsFailed(response.getMsg());
                }
            }
        });
    }
}
