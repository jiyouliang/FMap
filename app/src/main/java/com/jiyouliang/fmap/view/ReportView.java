package com.jiyouliang.fmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.LogUtil;

/**
 * 上报
 */
public class ReportView extends BaseIconView {

    private static final String TAG = "ReportView";


    public ReportView(Context context) {
        this(context, null);
    }

    public ReportView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.IconViewStyle);
    }

    public ReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void initBackground() {
        setBackgroundResource(R.drawable.icon_middle_selector);
    }

    @Override
    public Bitmap createBitmap() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.funicon_error_tab);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogUtil.d(TAG, "width="+getMeasuredWidth()+",height="+getMeasuredHeight());
        super.onDraw(canvas);
    }
}
