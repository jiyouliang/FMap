package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.LogUtil;

/**
 * 常用
 */
public class FrequentView extends AppCompatImageButton {
    private static final String TAG = "FrequentView";

    //    private final Bitmap mBitmap2;
    private Bitmap mBitmap;
    private Paint mPaint;

    public FrequentView(Context context) {
        this(context, null);
    }

    public FrequentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrequentView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_frequent, null, false);
//        setImageDrawable(getResources().getDrawable(R.drawable.icon_frequent_location_btn));
        setBackgroundResource(R.drawable.icon_circle_selector);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_frequent_location_btn);
        mPaint = new Paint();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 测量控件大小
     */
   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        setMeasuredDimension(width, height);
    }*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        LogUtil.d(TAG, "height=" + height + ",width=" + width);
        //居中显示
        canvas.drawBitmap(mBitmap, (width - mBitmap.getWidth()) / 2f, (height - mBitmap.getHeight()) / 2f, mPaint);
    }
}
