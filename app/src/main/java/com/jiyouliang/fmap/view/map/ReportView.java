package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.view.base.BaseIconView;

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
    public boolean createBackground() {
        setIconBackground(R.drawable.funicon_error_tab);
        return true;
    }

    @Override
    public boolean createIcon() {
        setBackgroundResource(R.drawable.icon_middle_selector);
        return true;
    }
}
