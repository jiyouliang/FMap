package com.jiyouliang.fmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import com.jiyouliang.fmap.R;

/**
 * 带图标控件
 */
public abstract class BaseIconView extends AppCompatImageButton implements IconViewInterface {

    private Paint mPaint;
    private Bitmap mBitmap;

    public BaseIconView(Context context) {
        this(context, null);
    }

    public BaseIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mBitmap = createBitmap();
        initBackground();
    }

    protected void initBackground() {
        setBackgroundResource(R.drawable.ret_circle_selector);
    }


    /**
     * 绘制到中心
     */
    protected void drawToCenter(Canvas c, Bitmap b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (null != mPaint && b != null) {
            c.drawBitmap(b, (width - b.getWidth()) / 2f, (height - b.getHeight()) / 2f, mPaint);
        }
    }

    protected void drawBitmapToCenter(Canvas c) {
        drawToCenter(c, mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmapToCenter(canvas);
    }
}
