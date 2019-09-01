package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.jiyouliang.fmap.R;

/**
 * 路线控件
 */
public class RouteView extends RelativeLayout {
    public RouteView(Context context) {
        this(context, null);
    }

    public RouteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RouteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_route, this, true);
    }
}
