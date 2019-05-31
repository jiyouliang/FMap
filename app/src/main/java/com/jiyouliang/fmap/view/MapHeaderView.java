package com.jiyouliang.fmap.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.jiyouliang.fmap.R;

/**
 * 头部
 */
public class MapHeaderView extends ConstraintLayout {
    public MapHeaderView(Context context) {
        this(context, null);
    }

    public MapHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_map_header, this, true);


    }
}
