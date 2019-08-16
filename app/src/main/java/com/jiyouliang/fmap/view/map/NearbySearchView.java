package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.jiyouliang.fmap.R;

/**
 * 附近搜索
 */
public class NearbySearchView extends ConstraintLayout implements View.OnClickListener {

    private OnNearbySearchViewClickListener mListener;

    public NearbySearchView(Context context) {
        this(context, null);
    }

    public NearbySearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NearbySearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_nearby_searcy, this, true);
        this.setOnClickListener(this);
    }

    public void setOnNearbySearchViewClickListener(OnNearbySearchViewClickListener listener) {
        if (listener == null) {
            return;
        }
        this.mListener = listener;
    }

    public interface OnNearbySearchViewClickListener {
        /**
         * 附近搜索点击回调
         */
        void onNearbySearchClick();
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        mListener.onNearbySearchClick();
    }
}
