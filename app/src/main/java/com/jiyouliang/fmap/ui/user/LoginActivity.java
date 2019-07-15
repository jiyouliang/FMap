package com.jiyouliang.fmap.ui.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.DeviceUtils;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.net.BaseHttpTask;
import com.jiyouliang.fmap.util.net.HttpTaskClient;
import com.jiyouliang.fmap.util.security.KeystoreUtil;
import com.jiyouliang.fmap.util.security.RSACrypt;
import com.jiyouliang.fmap.util.security.ValidateUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtPhone;
    private Button mBtnLogin;
    private static final String TAG = "LoginActivity";
    private static final boolean DEBUGGING = true;
    private String timestamp;
    private String sign;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        mBtnLogin = (Button) findViewById(R.id.btn_send_sms);

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

        //请求参数
        String jsonParams = getHttpRequestParams();
        HttpTaskClient.getInstance().sendSms(jsonParams, new BaseHttpTask.OnHttpResponseListener() {
            @Override
            public void onFailed(Exception e) {
                log("发送验证码失败,"+e.getMessage());
            }

            @Override
            public void onSuccess(String response) {
                log("发送验证码成功:" + response);
            }
        });
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
     * 生成签名：签名格式=keystore md5 + timestamp
     *
     * @return
     */
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
