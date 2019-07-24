package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.jiyouliang.fmap.R;

/**
 * 地图上Supend容器
 */
public class SupendViewContainer extends ConstraintLayout {
    public SupendViewContainer(Context context) {
        this(context, null);
    }

    public SupendViewContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SupendViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_supend_container, this, true);
    }
}
