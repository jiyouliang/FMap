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
 * 打的
 */
public class CallTaxiView extends BaseIconView {

    private static final String TAG = "CallTaxiView";


    public CallTaxiView(Context context) {
        this(context, null);
    }

    public CallTaxiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallTaxiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean createBackground() {
        setBackgroundResource(R.drawable.icon_middle_selector);
        return true;
    }

    @Override
    public boolean createIcon() {
        setIconBackground(R.drawable.poi_indicator_call_taxi);
        return true;
    }
}
