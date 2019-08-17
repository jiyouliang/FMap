package com.jiyouliang.fmap.view.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.LogUtil;

/**
 * 自定义GPS定位控件
 */
@SuppressLint("AppCompatCustomView")
public class GPSView extends ImageView implements View.OnClickListener {
    private static final String TAG = "GPSView";
    private OnGPSViewClickListener mGPSClickListener;
    public static final int STATE_UNLOCKED = 0;//未定位状态，默认状态
    public static final int STATE_LOCKED = 1;//定位状态
    public static final int STATE_ROTATE = 2;//根据地图方向旋转状态
    private boolean isAbovePoiDetail;//GPSView当前是否在poi detail上面
    private int mState;

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


    @Override
    public void onClick(View v) {
        LogUtil.d(TAG, "onClick");
        if (mGPSClickListener == null) {
            return;
        }
        mGPSClickListener.onGPSClick();
    }

    /**
     * 设置GPS定位图标
     *
     * @param state
     */
    public void setGpsState(int state) {
        switch (state) {
            case STATE_UNLOCKED:
                setImageResource(R.drawable.icon_gps_unlocked);
                this.mState = state;
                break;
            case STATE_LOCKED:
                setImageResource(R.drawable.icon_gps_locked);
                this.mState = state;
                break;
            case STATE_ROTATE:
                setImageResource(R.drawable.icon_gps_rotate);
                this.mState = state;
                break;
        }

    }

    public int getGpsState(){
        return mState;
    }

    public interface OnGPSViewClickListener {
        void onGPSClick();
    }

    public void setOnGPSViewClickListener(OnGPSViewClickListener listener) {
        if (listener == null) {
            return;
        }
        this.mGPSClickListener = listener;
    }

    /**
     * GPSView是否poi detail上面
     * @return
     */
    public boolean isAbovePoiDetail() {
        return isAbovePoiDetail;
    }

    public void setAbovePoiDetail(boolean above) {
        isAbovePoiDetail = above;
    }
}
