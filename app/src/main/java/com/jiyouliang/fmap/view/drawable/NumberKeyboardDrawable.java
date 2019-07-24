package com.jiyouliang.fmap.view.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author YouLiang.Ji
 * 自定义数字键盘圆角矩形
 */
public class NumberKeyboardDrawable extends Drawable {
    private int w;
    private int h;
    private Paint mPaint;
    //圆角
    private static final int RADIUS = 20;
    private static final int COLOR_STROKE = 0xFFDBDEE9;
    private static final int COLOR_FILL = 0xFFF4F4F4;
    private static final int STROKE_WIDTH = 4;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PRESSED = 1;

    public NumberKeyboardDrawable(int state) {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //填充或者绘制线条
        if (state == STATE_NORMAL) {
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(0xFFFFFFFF);
        } else if (state == STATE_PRESSED) {
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(COLOR_FILL);
        }
    }

    public NumberKeyboardDrawable(int state, int w, int h) {
       this(state);
       setWidthAndHeight(w, h);
    }

    public void setWidth(int w){
        this.w = w;
    }

    public void setHeight(int h){
        this.h = h;
    }

    public void setWidthAndHeight(int w, int h){
        setWidth(w);
        setHeight(h);
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        //绘制圆角矩形
        canvas.drawRoundRect(STROKE_WIDTH / 2F, STROKE_WIDTH / 2F, w - STROKE_WIDTH / 2F, h - STROKE_WIDTH / 2F,
                RADIUS, RADIUS, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
