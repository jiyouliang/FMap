package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.HttpTaskClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YouLiang.Ji
 */
public class UserSettingModel implements IUserSettingModel {


    @Override
    public void logout(String mobile, int reqCode, Context context, HttpTaskClient.OnHttpResponseListener<UserLoginData> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        HttpTaskClient.getInstance().logout(params, reqCode, UserLoginData.class, context, listener);
    }
}
