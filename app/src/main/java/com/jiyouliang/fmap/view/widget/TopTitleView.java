package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.jiyouliang.fmap.R;

/**
 * @author YouLiang.Ji
 *
 * 顶部标题
 */
public class TopTitleView extends RelativeLayout {
    public TopTitleView(Context context) {
        this(context, null);
    }

    public TopTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_top_title, this, true);
    }
}
