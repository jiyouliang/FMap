package com.jiyouliang.fmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;

/**
 * 图层
 */
public class MapLayerView extends BaseIconView {


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
    public Bitmap createBitmap() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.icon_c2);
    }
}
