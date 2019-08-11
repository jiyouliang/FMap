package com.jiyouliang.fmap.ui.user;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
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

    /**
     * 用户详情Fragment栈名
     *
     * @deprecated 第一个页面默认进来即显示, 不需要添加到回退栈
     */
    @Deprecated
    private static final String STACK_NAME_DETAIL = "user_detail";

    /**
     * 用户发送验证码Fragment栈名
     */
    private static final String STACK_NAME_SEND_SMS = "user_send_sms";

    /**
     * 用户短信验证码登录Fragment栈名
     */
    private static final String STACK_NAME_SMS_LOGIN = "user_sms_login";

    /**
     * 用户信息Fragment栈名
     */
    private static final String STACK_NAME_INFO = "user_info";

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
     */
    private void dispatchFragment(Uri uri) {
        // FragmentManager默认不会为空,这里添加是为了为了代码安全,防范于未然
        if (fm == null) {
            return;
        }
        String fragment = uri.getQueryParameter("fragment");
        if (TextUtils.isEmpty(fragment)) {
            LogUtil.e(TAG, "fragment name cannot be null");
            return;
        }
        // 发送短信验证码
        if (fragment.equals(UserSendSmsFragment.class.getSimpleName())) {
            showUserSemdSmsFragment();
        }
        // 短信验证码登录
        if (fragment.equals(UserLoginBySmsFragment.class.getSimpleName())) {
            String phone = uri.getQueryParameter("phone");
            showUserLoginBySmsFragment(phone);
        }

        // 用户详情Fragment
        if (fragment.equals(UserDetailFragment.class.getSimpleName())) {
            String phone = uri.getQueryParameter("phone");
            showUserDetailFragment(phone);
        }

        // 用户信息Fragment
        if (fragment.equals(UserInfoFragment.class.getSimpleName())) {
            String phone = uri.getQueryParameter("phone");
            showUerInfoFragment(phone);
        }

        // 返回上一页
        if (fragment.equals("back")) {
            back();
        }

        // 登录成功,情况回退栈,默认显示用户详情Fragment
        if (fragment.equals("loginSuccessClearStacks")) {
            loginSuccessClearStacks();
        }

    }


    /**
     * 用户详情Fragment
     *
     * @param phone
     */
    public void showUserDetailFragment(String phone) {
        // 用于沉浸式状态栏
        mRootContainer.setBackground(getResources().getDrawable(R.drawable.user_detail_bg));
        FragmentTransaction ft = fm.beginTransaction();
        UserDetailFragment fragment = UserDetailFragment.newInstance(phone);
        ft.replace(R.id.fragment_container, fragment);
        // 添加到回退栈
        // 第一个页面不需要添加到回退栈
        //ft.addToBackStack(STACK_NAME_DETAIL);
        ft.commit();
        // 手机号不为空,说明从登录成功页面跳转过来,需要清空Fragment回退栈信息
        if (!TextUtils.isEmpty(phone)) {
            loginSuccessClearStacks();
        }
    }

    /**
     * 发端短信验证码Fragment
     */
    public void showUserSemdSmsFragment() {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        UserSendSmsFragment fragment = UserSendSmsFragment.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        // 添加到回退栈
        ft.addToBackStack(STACK_NAME_SEND_SMS);
        ft.commit();
    }

    /**
     * 短信验证码登录Fragment
     */
    public void showUserLoginBySmsFragment(String phone) {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        UserLoginBySmsFragment fragment = UserLoginBySmsFragment.newInstance(phone);
        ft.replace(R.id.fragment_container, fragment);
        // 添加到回退栈
        ft.addToBackStack(STACK_NAME_SMS_LOGIN);
        ft.commit();
    }

    private void showUerInfoFragment(String phone) {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        UserInfoFragment fragment = UserInfoFragment.newInstance(phone);
        ft.replace(R.id.fragment_container, fragment);
        // 添加到回退栈
        ft.addToBackStack(STACK_NAME_INFO);
        ft.commit();
    }

    /**
     * 返回上一页,通过Fragment回退栈管理
     */
    private void back() {
        int count = fm.getBackStackEntryCount();
        if (count <= 0) {
            // 没有回退栈关闭当前Activity
            finish();
        }
        if (count == 1) {
            // 避免回退背景变成白色
            mRootContainer.setBackground(getResources().getDrawable(R.drawable.user_detail_bg));
        }
        fm.popBackStack();
    }

    /**
     * 登录成功后,清空回退栈
     */
    private void loginSuccessClearStacks() {
        log("clear fragments stack");
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
            if (!TextUtils.isEmpty(entry.getName())) {
                fm.popBackStack();
            }
        }
//        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }

    /**
     * 处理返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
