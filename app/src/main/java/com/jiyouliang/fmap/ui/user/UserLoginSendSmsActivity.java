package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.os.CountDownThreadTimer;
import com.jiyouliang.fmap.view.widget.KeyboardContainerView;
import com.jiyouliang.fmap.view.widget.KeyboardInputView;
import com.jiyouliang.fmap.view.widget.LoadingDialog;
import com.jiyouliang.fmap.view.widget.TopTitleView;

/**
 * 发送短信验证码登录
 */
public class UserLoginSendSmsActivity extends BaseActivity implements IUserLoginSendSmsView, View.OnClickListener {

    private static final String TAG = "UserLoginSendSmsActivity";

    private TopTitleView mTopTitleView;
    private TextView mTvLoginSendTip;
    private TextView mTvSmsError;
    private TextView mTvLoginResendSms;
    private KeyboardContainerView mKeyboardContainerView;
    private LoadingDialog mDialog;
    private KeyboardInputView mKeyboardInputView;
    private Context mContext;
    private UserLoginSendSmsPresenter mPresenter;
    private CountDownThreadTimer mCountDownTimer;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_sms_code);
        initData();
        initView();
        setListener();
    }

    private void initView() {
        mTopTitleView = (TopTitleView) findViewById(R.id.ttv);
        mTvLoginSendTip = (TextView) findViewById(R.id.tv_login_send_tip);
        mTvSmsError = (TextView) findViewById(R.id.tv_sms_error);
        mTvLoginResendSms = (TextView) findViewById(R.id.tv_login_resend_sms);
        mKeyboardContainerView = (KeyboardContainerView) findViewById(R.id.keyboard_container_view);
        mDialog = new LoadingDialog(this);
        mKeyboardInputView = findViewById(R.id.keyboard_input_view);
        //设置手机号
        StringBuilder hidePhone = new StringBuilder();
        hidePhone.append(mPhone.substring(0, 3)).append("****")
                .append(mPhone.substring(7, 11));
        mTvLoginSendTip.setText(String.format(mTvLoginSendTip.getText().toString(), hidePhone));

        mContext = this;
        //初始化Presenter
        mPresenter = new UserLoginSendSmsPresenter(mContext, this);
        showCountDown();
    }

    private void initData() {
        mPhone = getIntent().getStringExtra("phone");
    }

    /**
     * 设置监听
     */
    private void setListener() {
        mKeyboardContainerView.setOnTextChangedListener(new KeyboardContainerView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                //不允许发送请求过程中多次输入验证码
                if (mDialog.isShowing()) {
                    return;
                }
                //短信验证码长度4位
                if (text.length() == 4) {
                    mPresenter.loginBySms(mPhone, text);
                }
            }
        });


        mTvLoginResendSms.setOnClickListener(this);
    }

    @Override
    public void showLoading() {
        mTvSmsError.setVisibility(View.GONE);
        mDialog.show();
        mKeyboardContainerView.setInputEnable(false);
    }

    @Override
    public void hideLoading() {
        mDialog.dismiss();
        mKeyboardContainerView.setInputEnable(true);
    }

    @Override
    public void showSmsCodeError() {
        mTvSmsError.setVisibility(View.VISIBLE);
        //左右摇摆动画
        mKeyboardInputView.clearAnimation();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mKeyboardInputView.startAnimation(shake);
        //显示重新发送验证码
        mTvLoginResendSms.clearAnimation();

        mTvLoginResendSms.setText(getString(R.string.resend_sms_code));
        mTvLoginResendSms.setEnabled(true);
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void loginSuccess(UserLoginData userData) {
        /// 登录成功,关闭当前页面进入用户首页 TODO 别忘了存储用户数据到本地
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
        finish();
    }

    @Override
    public void loginFailed(UserLoginData userData) {
        if (userData != null) {
            showToast(String.format("登录失败(%s, %s)", userData.getMsg(), userData.getCode()));
        } else {
            LogUtil.e(TAG, "loginFailed userData is null");
        }
        //清空输入框验证码
        mKeyboardInputView.clearAllText();
    }

    @Override
    public void showUnknownError(Exception e) {
        if (e != null) {
            showToast(String.format("登录异常(%s)", e.getMessage()));
        }
        //清空输入框验证码
        mKeyboardInputView.clearAllText();
    }

    @Override
    public void sendSmsSuccess() {
        mTvSmsError.setVisibility(View.GONE);
        showToast("验证码发送成功");
    }

    @Override
    public void sendSmsError(UserLoginData response) {
        mTvSmsError.setVisibility(View.GONE);
        showToast(String.format("验证码发送失败(%s, %s)", response.getMsg(), response.getCode()));
    }

    /**
     * 显示倒计时60m
     */
    private void showCountDown() {
        mTvLoginResendSms.setEnabled(false);
        if(mCountDownTimer != null){
            // 关闭上一个任务
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownThreadTimer(60 * 1000, 1000) {

            @Override
            public void onTick(long mills) {
                //谷歌建议使用String.format拼接字符串,而不是:字符串+字符串方式
                mTvLoginResendSms.setText(String.format("%ss后重新发送", mills/1000));
            }

            @Override
            public void onFinish() {
                mTvLoginResendSms.setEnabled(true);
                mTvLoginResendSms.setText(getString(R.string.resend_sms_code));
            }
        };

        mCountDownTimer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
       if(mCountDownTimer != null){
           mCountDownTimer.cancel();
       }
    }

    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }
        if(v == mTvLoginResendSms){
            //重新发送验证码
            showCountDown();
            mPresenter.sendSms(mPhone);
        }
    }
}
