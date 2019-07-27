package com.jiyouliang.fmap.view.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import java.util.ArrayList;


/**
 * @author YouLiang.Ji
 * Button加载效果
 */
@SuppressLint("AppCompatCustomView")
public class ButtonLoadingView extends Button {
    private int mWidth;
    private int mHeight;
    //circle个数
    private static final int CIRCLE_COUNT = 3;
    private static final int RADIUS = 10;
    //circle水平边距
    private static final int CIRCLE_PADDING = 18;
    //第一个圆形中心点x坐标
    private float mCricleCenterX;
    private float mPadding;
    private final ArrayList<Paint> mPaintList = new ArrayList<>();
    private final ArrayList<Path> mPathtList = new ArrayList<>();
    private static final int START_COLOR = 0xFFFFFFFF;
    private int[] mAnimValues = new int[CIRCLE_COUNT];
    private AnimatorSet mAnimatorSet;
    private static final int DURATION = 400;
    private static final int DELAY = 130;
    private String mText;
    private boolean mLoadingState;

    public ButtonLoadingView(Context context) {
        this(context, null);
    }

    public ButtonLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        for (int i = 0; i < CIRCLE_COUNT; i++) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(START_COLOR);
            mPaintList.add(paint);
        }

        mText = getText().toString().trim();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;

        mPadding = dp2px(CIRCLE_PADDING);
        mCricleCenterX = mWidth / 2f - mPadding;

        mAnimatorSet = new AnimatorSet();
        for (int i = 0; i < CIRCLE_COUNT; i++) {
            //通过path设置路径,是为了后面停止动画便于清空画布
            Path path = new Path();
            path.moveTo(mCricleCenterX + i * mPadding, mHeight / 2f);
            path.addCircle(mCricleCenterX + i * mPadding, mHeight / 2f, RADIUS, Path.Direction.CW);
            mPathtList.add(path);
            ValueAnimator animator = ValueAnimator.ofInt(0, 255);
            animator.setDuration(DURATION);
            animator.setStartDelay(i * DELAY);

            animator.setInterpolator(new DecelerateInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            final int finalI = i;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAnimValues[finalI] = (int) animation.getAnimatedValue();
                    postInvalidate();
                }
            });

            mAnimatorSet.playTogether(animator);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if(mLoadingState){
            for (int i = 0; i < CIRCLE_COUNT; i++) {
                Paint paint = mPaintList.get(i);
                Path path = mPathtList.get(i);
                path.reset();
                path.moveTo(mCricleCenterX + i * mPadding, mHeight / 2f);
                path.addCircle(mCricleCenterX + i * mPadding, mHeight / 2f, RADIUS, Path.Direction.CW);
                //透明度
                paint.setAlpha(mAnimValues[i]);
                canvas.drawPath(path, paint);
            }
        }
        canvas.restore();
    }

    private float dp2px(float dpValue) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources()
                        .getDisplayMetrics());
    }

    public void startLoading(){
        //清空文字
        setText("");
        mAnimatorSet.start();
        mLoadingState = true;
    }

    /**
     * 停止加载动画
     */
    public void stopLoading(){
        if(!TextUtils.isEmpty(mText)){
            setText(mText);
        }
        //清空画布
        for (int i = 0; i < CIRCLE_COUNT; i++) {
           postInvalidate();
        }
        mAnimatorSet.end();
        mLoadingState = false;
    }

    public boolean getLoadingState(){
        return mLoadingState;
    }

}
