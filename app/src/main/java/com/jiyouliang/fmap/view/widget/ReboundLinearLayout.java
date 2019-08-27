package com.jiyouliang.fmap.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.jiyouliang.fmap.util.LogUtil;

/**
 * @author YouLiang.Ji
 * 阻尼回弹控件
 */
public class ReboundLinearLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "ReboundLinearLayout";
    /**
     * 摩擦力,摩擦力越大view移动距离越小
     */
    private static final float FORCE = 3.5f;

    /**
     * 滑动值
     */
    private int scrolls = 0;

    private NestedScrollingParentHelper mNestedHelper;
    private int dy;
    private ValueAnimator mAnimator;
    private boolean isScrollY;

    public ReboundLinearLayout(@NonNull Context context) {
        this(context, null);
    }

    public ReboundLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReboundLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mNestedHelper = new NestedScrollingParentHelper(this);
        initReboundAnimator();

    }

    /**
     * 回弹动画
     */
    private void initReboundAnimator() {
        mAnimator = ValueAnimator.ofInt(scrolls, 0);
        mAnimator.setDuration(200);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                log(String.format("value=%s", value));
                scrollTo(0, value);
            }
        });

        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scrolls = 0;
            }
        });
    }


    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        log("onStartNestedScroll");
//        return target instanceof RecyclerView;
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        log("onNestedScrollAccepted");
        mNestedHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        log("onStopNestedScroll");
        // 不判断scrolls,第一次onNestedScroll前会回调onStopNestedScroll,导致阻尼scroll不顺畅
        mNestedHelper.onStopNestedScroll(target);
        if(scrolls != 0 && !mAnimator.isRunning()){
            mAnimator.setIntValues(scrolls, 0);
            mAnimator.start();
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        log(String.format("onNestedPreScroll, dy=%s, getScrollY=%s, getScrollX=%s", dy, getScrollY(), getScrollX()));
        this.dy = dy;
        // Y轴方向滑动
        isScrollY = Math.abs(dy) > Math.abs(dx);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        getParent().requestDisallowInterceptTouchEvent(true);

        /*
         * ViewCompat.TYPE_TOUCH: 手指触发滑动,即非Filing,
         * dy <= 0: 只允向下滑动(只显示头部阻尼效果),
         * !mAnimator.isRunning(): 执行动画期间调用scrollTo方法导致onNestedScroll回调,这里需要屏蔽
         */
        /*if(type == ViewCompat.TYPE_TOUCH && dy <= 0 && !mAnimator.isRunning()){
            log("onNestedScroll");
            scrolls += dyUnconsumed / FORCE;
            scrollTo(0, scrolls);
        }*/

        if(dy <= 0 && !mAnimator.isRunning() && isScrollY){
            log("onNestedScroll");
            scrolls += dyUnconsumed / FORCE;
            scrollTo(0, scrolls);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        log(String.format("onNestedPreFling, velocityX=%s, velocityY=%s, getBottom()=%s=, getMeasuredHeight()=%s", velocityX, velocityY, getBottom(), getHeight()));
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        log(String.format("onNestedFling, velocityX=%s, velocityY=%s", velocityX, velocityY));
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }


    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }
}
