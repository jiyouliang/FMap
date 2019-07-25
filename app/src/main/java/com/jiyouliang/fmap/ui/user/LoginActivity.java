package com.jiyouliang.fmap.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.server.net.HttpTaskClient;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.DeviceUtils;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.security.KeystoreUtil;
import com.jiyouliang.fmap.util.security.RSACrypt;
import com.jiyouliang.fmap.util.security.ValidateUtil;
import com.jiyouliang.fmap.view.widget.ButtonLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtPhone;
    private ButtonLoadingView mBtnLogin;
    private static final String TAG = "LoginActivity";
    private static final boolean DEBUGGING = true;
    private String timestamp;
    private String sign;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sms_login);
        initView();
        setListener();
    }

    private void setListener() {
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                log("beforeTextChanged, count=" + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                log("onTextChanged, count=" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                log("afterTextChanged, s=" + s.toString());
                String phone = s.toString();
                if (TextUtils.isEmpty(phone)) {
                    return;
                }
                //输入框状态设置
                mBtnLogin.setEnabled(phone.length() == 11);

            }
        });
    }

    private void initView() {
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mBtnLogin = (ButtonLoadingView) findViewById(R.id.btn_send_sms);

        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_sms:
                sendSms();
                break;
        }
    }

    /**
     * 发送短信验证码
     */
    private void sendSms() {
        if(mBtnLogin.getLoadingState()){
            //避免重复点击请求
            return;
        }
        if (!mEtPhone.isEnabled()) {
            return;
        }
        phone = mEtPhone.getText().toString().trim();
        if (!ValidateUtil.isPhone(phone)) {
            showToast("请输入正确手机号码");
            return;
        }
        timestamp = String.valueOf(DeviceUtils.getTimestamp());
        sign = getSign();
        if (TextUtils.isEmpty(sign)) {
            showToast("RSA签名失败");
            return;
        }

        //显示button加载
        mBtnLogin.startLoading();
        //请求参数
        String jsonParams = getHttpRequestParams();
        HttpTaskClient.getInstance().sendSms(jsonParams, UserLoginData.class, new HttpTaskClient.OnHttpResponseListener<UserLoginData>() {
            @Override
            public void onException(Exception e) {
                log("发送验证码失败,"+e.getMessage());
                mBtnLogin.stopLoading();
            }

            @Override
            public void onResponse(UserLoginData response) {
                log("发送验证码成功:" + response);
                mBtnLogin.stopLoading();
                if(response.getCode() == 0){
                    showInputSmsCode();
                }else{
                    Toast.makeText(LoginActivity.this, "发送验证码失败(code:"+response.getCode()+",msg:"+response.getMsg()+")" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 跳转输入短信验证码页面
     */
    private void showInputSmsCode() {
        Intent intent = new Intent(LoginActivity.this, UserLoginSendSmsActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 生成http请求参数
     *
     * @return
     */
    private String getHttpRequestParams() {
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", phone);
            json.put("timestamp", timestamp);
            json.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     *
     * 生成签名：签名格式=keystore md5 + timestamp
     * @deprecated 请使用RSACrypt.genSign
     * @see RSACrypt#genSign
     * @return
     */
    @Deprecated
    private String getSign() {
        String keystoreMD5 = KeystoreUtil.getMD5Signatures(getPackageManager(), getPackageName());
        if (TextUtils.isEmpty(keystoreMD5)) {
            return null;
        }
        StringBuilder data = new StringBuilder();
        data.append(keystoreMD5).append(timestamp);
        try {
            //生成RSA 签名
            return RSACrypt.sign(data.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void log(String msg) {
        if (DEBUGGING) {
            LogUtil.d(TAG, msg);
        }

    }
}
