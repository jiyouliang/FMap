package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.HttpTaskClient;

/**
 * @author YouLiang.Ji
 */
public class UserSettingPresenter implements HttpTaskClient.OnHttpResponseListener<UserLoginData> {
    private static final int REQ_CODE_LOGOUT = 1;
    private IUserSettingView mView;
    private IUserSettingModel mModel;
    private Context mContext;

    public UserSettingPresenter(IUserSettingView mView, Context context) {
        this.mView = mView;
        this.mModel = new UserSettingModel();
        this.mContext = context;
    }

    public void logout(String mobile){
        mView.showLoading();
        mModel.logout(mobile, REQ_CODE_LOGOUT, mContext, this);
    }

    @Override
    public void onException(Exception e) {
        mView.hideLoading();
        mView.onLogoutFailed(e);
    }

    @Override
    public void onResponse(int reqCode, UserLoginData response) {
        mView.hideLoading();
        mView.onLogoutSuccess(response);
    }
}
