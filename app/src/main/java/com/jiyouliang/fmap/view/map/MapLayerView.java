package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.view.base.BaseIconView;

/**
 * 图层
 */
public class MapLayerView extends BaseIconView {

    private static final String TAG = "MapLayerView";


    public MapLayerView(Context context) {
        this(context, null);
    }

    public MapLayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapLayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.d(TAG, "width=" + getMeasuredWidth() + ",height=" + getMeasuredHeight());
    }

    @Override
    public boolean createBackground() {
        setIconBackground(R.drawable.map_widget_layer_icon);
        return true;
    }

    @Override
    public boolean createIcon() {
        setBackgroundResource(R.drawable.single_bg_selector);
        return true;
    }
}
