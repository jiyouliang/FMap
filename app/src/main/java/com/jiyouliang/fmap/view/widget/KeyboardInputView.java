package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.view.base.BaseView;

/**
 * @author YouLiang.Ji
 * <p>
 * 键盘输入控件,类似EditText,没有文本有下划线,输入文本后对于位置不显示下划线
 */
public class KeyboardInputView extends BaseView {
    //setStrokeWidth使用,dp
    private static final int COLOR_STROKE = 0xFF262626;
    private static final int STROKE_WIDTH_DP = 4;
    //绘制单个线条长度dp
    private static final int STROKE_LENGTH_DP = 20;
    // 线条直接间距与线条长度的倍数
    private static final float SPACE_TIMES = 1.5F;
    //最大输入长度,默认4个字符
    private static int mInputMaxSize = 4;
    // 每个线条长度
    private float mStrokeLength;
    private Paint mPaint;
    private Path mPath;
    private int mWidth;
    private float mStrokeWidth;
    private int mHeight;
    private int mStartIndex = 0;
    private int mTextSize;
    private StringBuilder mStringBuilder;
    private OnTextChangedListener mListener;

    public KeyboardInputView(Context context) {
        this(context, null);
    }

    public KeyboardInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR_STROKE);
        mPaint.setStrokeWidth(dp2px(STROKE_WIDTH_DP));

        mPaint.setTextAlign(Paint.Align.CENTER);
        mTextSize = getResources().getDimensionPixelSize(R.dimen.text_size_keyboard);
        mPaint.setTextSize(mTextSize);

        mPath = new Path();

        mStrokeLength = dp2px(STROKE_LENGTH_DP);
        mStrokeWidth = dp2px(STROKE_WIDTH_DP);
        mStringBuilder = new StringBuilder();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        //所有间距 + 所有线条长度
        int measureWidth = (int) ((mInputMaxSize + 1) * mStrokeLength * SPACE_TIMES + mInputMaxSize * mStrokeLength);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mPath.reset();
        drawTexts(mStringBuilder.toString(),canvas);
        drawLines(mStartIndex, canvas);
        canvas.restore();
    }

    /**
     * 绘制文字
     */
    private void drawTexts(String text, Canvas canvas) {
        if(TextUtils.isEmpty(text)){
            return;
        }
        mPaint.setStyle(Paint.Style.FILL);
        char[] chars = text.toCharArray();
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        //计算文字基线,文字绘制y轴从baeline开始,并不是从控件一半
        int baseline = (mHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        mStartIndex = chars.length;
        for (int i = 0; i < chars.length; i++) {
            float x = SPACE_TIMES * mStrokeLength + mStrokeLength/2F + i * (SPACE_TIMES + 1) * mStrokeLength;
            canvas.drawText(String.valueOf(chars[i]), x, baseline, mPaint);
        }
    }

    /**
     * 绘制线条
     * @param startIndex 绘制起点
     * @param canvas
     */
    private void drawLines(int startIndex, Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = startIndex; i < mInputMaxSize; i++) {
            float startX = SPACE_TIMES * mStrokeLength + i * (SPACE_TIMES + 1) * mStrokeLength;
            float startY = mHeight / 2F;
            float endX = startX + mStrokeLength;
            mPath.moveTo(startX, startY);
            mPath.lineTo(endX, startY);
        }
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 设置文字,view会将文字绘制到指定位置
     * @param text
     * @return true:设置成功, false:设置失败,传入文字大于控件容量
     */
    public boolean setText(String text){
        //没有剩余控件输入文字
        if(mStringBuilder.length() >= mInputMaxSize){
            return false;
        }
        if(mListener != null){
            mListener.beforeTextChanged(mStringBuilder.toString());
        }
        mStringBuilder.append(text);
        postInvalidate();
        if(mListener != null){
            mListener.afterTextChanged(mStringBuilder.toString());
        }
        return true;
    }

    /**
     * 删除文字,每次删除一个字符
     */
    public void deleteText(){
        if(TextUtils.isEmpty(mStringBuilder)){
            return;
        }
        StringBuilder sb = new StringBuilder(mStringBuilder);
        mStringBuilder.deleteCharAt(mStringBuilder.length() - 1);
        //删除一个文字,线条绘制起点变小(绘制个数增加)
        mStartIndex = mStartIndex -1;
        if(mListener != null){
            mListener.beforeTextChanged(sb.toString());
            mListener.afterTextChanged(mStringBuilder.toString());
        }
        postInvalidate();
    }

    /**
     * 清除所有文字
     */
    public void clearAllText(){
        if(mStringBuilder == null || mStringBuilder.length() == 0){
            return;
        }
        mStringBuilder.delete(0, mStringBuilder.length());
        mStartIndex = 0;
        postInvalidate();
    }

    public String getText(){
        return mStringBuilder.toString();
    }

    /**
     * 设置控件最大输入
     * @return
     */
    public void setInputMaxSize(int maxSize){
        mInputMaxSize = maxSize;
    }

    /**
     * 获取控件最大输入
     * @return
     */
    public int getInputMaxSize(){
        return mInputMaxSize;
    }

    public void setOnTextChangedListener(OnTextChangedListener listener){
        this.mListener = listener;
    }

    /**
     * 文字改变回调
     */
    public interface OnTextChangedListener{
        /**
         * 改变前
         * @param text
         */
        void beforeTextChanged(String text);

        /**
         * 改变后
         * @param text
         */
        void afterTextChanged(String text);
    }
}
