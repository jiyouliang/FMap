package com.jiyouliang.fmap.ui.user;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseFragment;
import com.jiyouliang.fmap.util.LogUtil;

/**
 * 用户相关Activity,通过该Activity分发用户相关Fragment,比如UerLoginFragment
 * UserSendSmsFragment
 */
public class UserActivity extends FragmentActivity implements BaseFragment.OnFragmentInteractionListener {

    private FrameLayout mFragmentContainer;
    private FragmentManager fm;
    private static final String TAG = "UserActivity";
    private LinearLayout mRootContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mRootContainer = (LinearLayout) findViewById(R.id.ll_root_container);
        fm = getSupportFragmentManager();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        showUserDetailFragment(null);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        log(uri.toString());

        dispatchFragment(uri);

    }

    /**
     * 分发Fragment
     *
     */
    private void dispatchFragment(Uri uri) {
        String fragment = uri.getQueryParameter("fragment");
        if (TextUtils.isEmpty(fragment)) {
            LogUtil.e(TAG, "fragment name cannot be null");
            return;
        }
        // 发送短信验证码
        if(fragment.equals(UserSendSmsFragment.class.getSimpleName())){
            showUserSemdSmsFragment();
        }
        // 短信验证码登录
        if(fragment.equals(UserLoginBySmsFragment.class.getSimpleName())){
            String phone = uri.getQueryParameter("phone");
            showUserLoginBySmsFragment(phone);
        }

        // 用户详情Fragment
        if(fragment.equals(UserDetailFragment.class.getSimpleName())){
            String phone = uri.getQueryParameter("phone");
            showUserDetailFragment(phone);
        }

        // 用户信息Fragment
        if(fragment.equals(UserInfoFragment.class.getSimpleName())){
            String phone = uri.getQueryParameter("phone");
            showUerInfoFragment(phone);
        }

    }


    /**
     * 用户详情Fragment
     * @param phone
     */
    public void showUserDetailFragment(String phone) {
        // 用于沉浸式状态栏
        mRootContainer.setBackground(getResources().getDrawable(R.drawable.user_detail_bg));
        FragmentTransaction ft = fm.beginTransaction();
        UserDetailFragment fragment = UserDetailFragment.newInstance(phone);
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    /**
     * 发端短信验证码Fragment
     */
    public void showUserSemdSmsFragment() {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        UserSendSmsFragment fragment = UserSendSmsFragment.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    /**
     * 短信验证码登录Fragment
     */
    public void showUserLoginBySmsFragment(String phone) {
        FragmentTransaction ft = fm.beginTransaction();
        UserLoginBySmsFragment fragment = UserLoginBySmsFragment.newInstance(phone);
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void showUerInfoFragment(String phone) {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        UserInfoFragment fragment = UserInfoFragment.newInstance(phone);
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }
}
