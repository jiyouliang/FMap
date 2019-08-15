package com.jiyouliang.fmap.ui.user;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.HttpTaskClient;

import java.util.HashMap;

/**
 * @author YouLiang.Ji
 * <p>
 * 短信验证码登录业务层
 */
@SuppressWarnings("unchecked")
public class UserLoginSendSmsModel extends BaseModel implements IUserLoginSendSmsModel {
    @Override
    public void loginBySms(Context context, String mobile, String sms, int reqCode, HttpTaskClient.OnHttpResponseListener listener) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("sms", sms);
        String json = assembleReqParams(context, params);
        HttpTaskClient.getInstance().loginBySms(json, reqCode, UserLoginData.class, listener);
    }

    @Override
    public UserLoginData parseData(String json) {
        return JSONObject.parseObject(json, UserLoginData.class);
    }

    @Override
    public void sendSms(Context context, String mobile, int reqCode, HttpTaskClient.OnHttpResponseListener listener) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        String json = assembleReqParams(context, params);
        HttpTaskClient.getInstance().sendSms(json, reqCode, UserLoginData.class, listener);
    }
}
