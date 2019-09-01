package com.jiyouliang.fmap.view.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jiyouliang.fmap.R;

/**
 * @author YouLiang.Ji
 * 下划线渐变,带清除功能的EditText
 */
@SuppressLint("AppCompatCustomView")
public class ClearEditText extends EditText implements View.OnClickListener {
    private static final String TAG = "ClearEditText";
    private static final boolean DEBUGGING = true;

    private static final int COLOR_LIGHT = 0xFFE4E4E4;
    private static final int COLOR_GRAY = 0xFF484848;
    private Paint mPaint;
    private Path mPath;
    private Path mPathTransform;
    private Paint mPaintTransform;
    private int mHeight;
    private int mWidth;
    //线条宽度 dp
    private static final float STROKE_WIDTH = 1;
    //线条宽度px
    private float mStrokeWidth;
    private float mAnimatedValue;
    private Bitmap mBtmClose;
    private float left;
    private int top;
    private ValueAnimator mAnimator;
    private float mDownX;
    private float mDownY;
    private OnClearEditClickListener mListener;


    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.WHITE);
        setFocusable(false);
        setFocusableInTouchMode(false);
        //取消默认弹出软键盘
//        setInputType(InputType.TYPE_NULL);
        mStrokeWidth = dp2px(STROKE_WIDTH);
        //文字垂直居中
        setGravity(Gravity.CENTER_VERTICAL);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(COLOR_LIGHT);
        mPaint.setStrokeWidth(mStrokeWidth);

        mPaintTransform = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTransform.setStyle(Paint.Style.STROKE);
        mPaintTransform.setColor(COLOR_GRAY);
        mPaintTransform.setStrokeWidth(mStrokeWidth);

        mPath = new Path();
        mPathTransform = new Path();

        mBtmClose = BitmapFactory.decodeResource(getResources(), R.drawable.commute_tips_close2);

        setOnClickListener(this);
    }

    //布局文件解析完成回调
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        System.out.println(size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mHeight = h;
        this.mWidth = w;


        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.setDuration(500);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedValue = (float) animation.getAnimatedValue();
                //刷新UI
                invalidate();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        //绘制bitmap
        left = mWidth - mBtmClose.getWidth() - getPaddingRight();
        top = (mHeight - mBtmClose.getHeight()) / 2;
        canvas.drawBitmap(mBtmClose, left, top, mPaint);
        mPath.moveTo(mStrokeWidth, mHeight - mStrokeWidth / 2);
        mPath.lineTo(mWidth - mStrokeWidth, mHeight - mStrokeWidth / 2);
        canvas.drawPath(mPath, mPaint);

        //绘制渐变线条
        if (isFocusable() && !isInEditMode()) {
            //透明度0~255
            mPaintTransform.setAlpha((int) (mAnimatedValue * 255));
            mPathTransform.moveTo(mStrokeWidth, mHeight - mStrokeWidth / 2);
            mPathTransform.lineTo(mWidth * mAnimatedValue, mHeight - mStrokeWidth / 2);
            canvas.drawPath(mPathTransform, mPaintTransform);
        }
    }

    @Override
    public void onClick(View v) {
        //避免点击动画重复播放
        if (!isFocusable() || !isFocusableInTouchMode()) {
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();
            InputMethodManager inputManager =
                    (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
            mAnimator.start();
        }
    }


    private float dp2px(float dpValue) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources()
                        .getDisplayMetrics());
    }

    /**
     * 处理点击close图标
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                if(isCloseClick(mDownX, mDownY) && isCloseClick(upX, upY)){
                    //点击了close图标回调
                    if(mListener != null){
                        mListener.onDelete();
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void log(String msg) {
        if (DEBUGGING) {
            Log.d(TAG, msg);
        }
    }

    /**
     * 判断是否点击了close图标
     * @return
     */
    private boolean isCloseClick(float x, float y){
        float offset = 5;//偏移量:在close icon范围(-offset ~ + offset)内均视为点击成功
        //x轴方向判断
        boolean isXIn = (x >= left-offset) && (x <= left + mBtmClose.getWidth() + offset);
        //y轴方向判断
        boolean isYIn = (y >= top - offset) && (y <= top + mBtmClose.getHeight() + offset);
        return isXIn && isYIn;
    }

    public void setOnClearEditClickListener(OnClearEditClickListener listener){
        this.mListener = listener;
    }

    public interface OnClearEditClickListener{
        /**
         * 点击删除文字回调
         */
        void onDelete();
    }

}
