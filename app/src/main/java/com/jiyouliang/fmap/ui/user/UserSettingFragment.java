package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.server.data.UserLoginData;
import com.jiyouliang.fmap.ui.BaseFragment;
import com.jiyouliang.fmap.util.security.ValidateUtil;
import com.jiyouliang.fmap.view.widget.LoadingDialog;
import com.jiyouliang.fmap.view.widget.SettingItemView;
import com.jiyouliang.fmap.view.widget.TopTitleView;

/**
 * 用户设置页面
 */
public class UserSettingFragment extends BaseFragment implements View.OnClickListener, IUserSettingView{

    private static final String KEY_PHONE = "phone";
    private OnFragmentInteractionListener mListener;
    private String mPhone;
    private TopTitleView mTopTitleView;
    private SettingItemView mSivLogout;
    private View mLogoutContainer;
    private SettingItemView mSivDownload;
    private SettingItemView mSivMsgPush;
    private LoadingDialog mLoadingDialog;
    private UserSettingPresenter mPresenter;

    public UserSettingFragment() {
        // Required empty public constructor
    }

    public static UserSettingFragment newInstance(String phone) {
        UserSettingFragment fragment = new UserSettingFragment();
        Bundle bundle = new Bundle();
        // 默认bundle添加null字符串会设置为"null"
        if(!TextUtils.isEmpty(phone)){
            bundle.putString(KEY_PHONE, phone);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhone = getArguments().getString(KEY_PHONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_setting, container, false);
        initView(rootView);
        setListener();
        return rootView;
    }

    private void initView(View rootView) {
        mTopTitleView = (TopTitleView) rootView.findViewById(R.id.ttv);
        final ViewGroup llContainer = rootView.findViewById(R.id.ll_container);
        if (llContainer != null && llContainer.getChildCount() > 0) {
            int count = llContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = llContainer.getChildAt(i);
                if (child instanceof SettingItemView) {
                    child.setOnClickListener(this);
                }
            }
        }
        mSivLogout = (SettingItemView) rootView.findViewById(R.id.siv_logout);
        mSivMsgPush = (SettingItemView) rootView.findViewById(R.id.sivMsgPush);
        mSivDownload = (SettingItemView) rootView.findViewById(R.id.sivDownloadNew);
        mLogoutContainer = rootView.findViewById(R.id.ll_login_container);

        mLogoutContainer.setVisibility(TextUtils.isEmpty(mPhone) ? View.GONE : View.VISIBLE);
        // 加载进度
        mLoadingDialog = new LoadingDialog(getContext());

        // 注销Presenter
        mPresenter = new UserSettingPresenter(this, getContext());

    }

    private void setListener() {
        mTopTitleView.setOnTopTitleViewClickListener(new TopTitleView.OnTopTitleViewClickListener() {
            @Override
            public void onLeftClick(View v) {
                back();
            }

            @Override
            public void onRightClick(View v) {

            }
        });
        mSivLogout.setOnClickListener(this);
    }

    /**
     * 返回
     */
    private void back() {
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            builder.appendQueryParameter("fragment", "back");
            Uri uri = Uri.parse(builder.toString());
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * 通知Activity注销成功
     */
    private void logoutSuccess() {
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            builder.appendQueryParameter("fragment", "logoutSuccess");
            Uri uri = Uri.parse(builder.toString());
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

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        if (v == mSivMsgPush) {
            mSivMsgPush.setChecked(!mSivMsgPush.isChecked());
        }
        if (v == mSivDownload) {
            mSivDownload.setChecked(!mSivDownload.isChecked());
        }
        // 注销
        if(v == mSivLogout){
            if(!TextUtils.isEmpty(mPhone) && ValidateUtil.isPhone(mPhone)){
                mPresenter.logout(mPhone);
            }
        }
    }

    @Override
    public void showLoading() {
        if(mLoadingDialog != null && !mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.hide();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLoadingDialog != null){
            mLoadingDialog = null;
        }
    }

    @Override
    public void onLogoutSuccess(UserLoginData response) {
        logoutSuccess();
    }

    @Override
    public void onLogoutFailed(Exception e) {
        if(e != null && !TextUtils.isEmpty(e.getMessage())){
            showToast(e.getMessage());
        }
    }
}
