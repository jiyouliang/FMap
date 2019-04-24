package com.jiyouliang.fmap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jiyouliang.fmap.util.LogUtil;

public class GPSView extends android.support.v7.widget.AppCompatImageButton implements View.OnClickListener {
    private static final String TAG = "GPSView";
    private OnGPSViewClickListener mGPSClickListener;

    public GPSView(Context context) {
        this(context, null);
    }

    public GPSView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GPSView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                setSelected(true);
                setPressed(true);
                break;

            case MotionEvent.ACTION_UP:
                setPressed(false);
//                setSelected(false);
                break;
        }
//        return super.onTouchEvent(event);
        return true;
    }*/

    @Override
    public void onClick(View v) {
        LogUtil.d(TAG, "onClick");
        if(mGPSClickListener == null){
            return;
        }
        mGPSClickListener.onGPSClick();
    }

    public interface OnGPSViewClickListener{
        void onGPSClick();
    }

    public void setOnGPSViewClickListener(OnGPSViewClickListener listener){
        if(listener == null){
            return;
        }
        this.mGPSClickListener = listener;
    }
}
