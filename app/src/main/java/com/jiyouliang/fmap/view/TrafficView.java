package com.jiyouliang.fmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.LogUtil;

/**
 * 交通
 */
public class TrafficView extends BaseIconView {

    private static final String TAG = "TrafficView";


    public TrafficView(Context context) {
        this(context, null);
    }

    public TrafficView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrafficView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public boolean createBackground() {
        setBackgroundResource(R.drawable.icon_up_selector);
        return true;
    }

    @Override
    public boolean createIcon() {
        setIconBackground(R.drawable.icon_c_traffic_open);
        return true;
    }
}
