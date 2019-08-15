package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.InputMethodUtils;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.view.widget.ButtonLoadingView;
import com.jiyouliang.fmap.view.widget.ClearEditText;

/**
 * 用户登录
 * @deprecated 已经改成Fragment
 * @see UserSendSmsFragment
 */
@Deprecated
public class LoginActivity extends BaseActivity implements View.OnClickListener, IUserLoginView {

    private ClearEditText mEtPhone;
    private ButtonLoadingView mBtnLogin;
    private static final String TAG = "LoginActivity";
    private static final boolean DEBUGGING = true;
    private String timestamp;
    private String sign;
    private String phone;
    private static final int REQ_CODE_SMS = 0;
    private Context mContext;
    private UserLoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sms_login);
        initView();
        setListener();
        initData();
    }

    private void initView() {
        mEtPhone = (ClearEditText) findViewById(R.id.et_phone);
        mBtnLogin = (ButtonLoadingView) findViewById(R.id.btn_send_sms);
        mBtnLogin.setEnabled(false);


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

        mEtPhone.setOnClearEditClickListener(new ClearEditText.OnClearEditClickListener() {
            @Override
            public void onDelete() {
                //清空文字
                mEtPhone.setText("");
                mBtnLogin.setEnabled(false);
            }
        });

        mBtnLogin.setOnClickListener(this);
    }

    private void initData() {
        mContext = this;
        mPresenter = new UserLoginPresenter(this, mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_sms:
                if(mEtPhone.getText() != null){
                    phone = mEtPhone.getText().toString();
                }
                mPresenter.sendSms(phone);
                break;
        }
    }

    private void log(String msg) {
        if (DEBUGGING) {
            LogUtil.d(TAG, msg);
        }

    }

    @Override
    public void showMoibleError() {
        showToast(getString(R.string.please_input_correct_mobile));
    }

    /**
     * 暂时不需要加载Dialog,Button已有进度显示
     */
    @Override
    public void hideLoadingDialog() {

    }

    @Override
    public void showLoadingBtn() {
        //显示button加载
        mBtnLogin.startLoading();
    }

    @Override
    public void showNormalBtn() {
        mBtnLogin.stopLoading();
    }

    @Override
    public void sendSmsSuccess() {
        // 发送验证码成功,跳转下一个页面
        Intent intent = new Intent(LoginActivity.this, UserLoginSendSmsActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);
        finish();
    }

    @Override
    public void sendSmsException(Exception e) {
        if(e != null){
            showToast(e.getMessage());
        }
    }

    @Override
    public void sendSmsFailed(String erroMsg) {
        if(!TextUtils.isEmpty(erroMsg)){
            showToast(erroMsg);
        }
    }

    @Override
    public void hideInput() {
        InputMethodUtils.hideInput(this);
    }
}
