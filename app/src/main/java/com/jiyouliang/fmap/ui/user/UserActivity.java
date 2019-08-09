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
        showUserDetailFragment();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        log(uri.toString());
        log(uri.getFragment());
        if(uri == null || TextUtils.isEmpty(uri.getFragment())){
            return;
        }

        String fragment = uri.getFragment();
        if(fragment.equals("UserSendSmsFragment")){
            showUserSemdSmsFragment();
        }
    }

    /**
     * 用户详情Fragment
     */
    public void showUserDetailFragment(){
        // 用于沉浸式状态栏
        mRootContainer.setBackground(getResources().getDrawable(R.drawable.user_detail_bg));
        FragmentTransaction ft = fm.beginTransaction();
        UserDetailFragment fragment = UserDetailFragment.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    /**
     * 发端短信验证码Fragment
     */
    public void showUserSemdSmsFragment(){
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        UserSendSmsFragment fragment = UserSendSmsFragment.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void log(String msg){
        LogUtil.d(TAG, msg);
    }
}
