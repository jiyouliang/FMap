package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseFragment;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.view.widget.ButtonLoadingView;
import com.jiyouliang.fmap.view.widget.ClearEditText;

/**
 * @author jiyouliang
 * 发送短信验证码
 * 由于Activity包含多个Fragment,Fragment之间通信通过接口回调{@link BaseFragment.OnFragmentInteractionListener}
 * 处理,该回调由包含的Activity实现,并通过Fragment分发通信.
 *
 * 通过 {@link UserSendSmsFragment#newInstance} 该Fragment实例对象.
 */
public class UserSendSmsFragment extends BaseFragment implements View.OnClickListener, IUserLoginView{

    private ClearEditText mEtPhone;
    private ButtonLoadingView mBtnLogin;
    private static final boolean DEBUGGING = true;
    private static final String TAG = "UserSendSmsFragment";
    private String timestamp;
    private String sign;
    private String phone;
    private static final int REQ_CODE_SMS = 0;
    private Context mContext;
    private UserLoginPresenter mPresenter;

    private OnFragmentInteractionListener mListener;

    public UserSendSmsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserSendSmsFragment.
     */
    public static UserSendSmsFragment newInstance() {
        UserSendSmsFragment fragment = new UserSendSmsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_send_sms, container, false);
        initView(rootView);
        setListener();
        initData();
        return rootView;
    }

    private void initView(View rootView) {
        ScrollView scrollView = (ScrollView) rootView;
        RelativeLayout child = (RelativeLayout) scrollView.getChildAt(0);
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point pont = new Point();
        wm.getDefaultDisplay().getSize(pont);
        lp.height = pont.y;
        child.setLayoutParams(lp);


        mEtPhone = (ClearEditText) rootView.findViewById(R.id.et_phone);
        mBtnLogin = (ButtonLoadingView) rootView.findViewById(R.id.btn_send_sms);
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
        mContext = getContext();
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
        // TODO 通过 mListener.onFragmentInteraction回调跳转下一个Fragment
        /*Intent intent = new Intent(LoginActivity.this, UserLoginSendSmsActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);
        finish();*/
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
