package com.jiyouliang.fmap.ui.user;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.ui.BaseFragment;

/**
 * 用户相关Activity,通过该Activity分发用户相关Fragment,比如UerLoginFragment
 * UserSendSmsFragment
 */
public class UserActivity extends FragmentActivity implements BaseFragment.OnFragmentInteractionListener {

    private FrameLayout mFragmentContainer;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
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
    }

    /**
     * 打开用户页面Fragment
     */
    public void showUserDetailFragment(){
        FragmentTransaction ft = fm.beginTransaction();
        UserSendSmsFragment fragment = UserSendSmsFragment.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }
}
