package com.jiyouliang.fmap.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;

import com.jiyouliang.fmap.MapActivity;
import com.jiyouliang.fmap.R;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity implements Animator.AnimatorListener {

    /**
     * 动画时长
     */
    private static final long DURATION = 800;
    private ValueAnimator mAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
        mAnimator = ValueAnimator.ofFloat(0, 80);
        mAnimator.setDuration(DURATION);
        mAnimator.addListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    /**
     * 动画结束,进入主页MapActivity
     * @param animation
     */
    @Override
    public void onAnimationEnd(Animator animation) {
        showMapPage();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    /**
     * 关闭动画,释放动画资源
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(mAnimator.isRunning()){
            mAnimator.cancel();
        }
        if(mAnimator != null){
            mAnimator = null;
        }
    }

    /**
     * 进入主页
     */
    private void showMapPage(){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }
}
