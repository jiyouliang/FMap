package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.jiyouliang.fmap.R;

public class SupendPartitionView extends ConstraintLayout {
    public SupendPartitionView(Context context) {
        this(context, null);
    }

    public SupendPartitionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SupendPartitionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_supend_partition, this, true);
    }
}
