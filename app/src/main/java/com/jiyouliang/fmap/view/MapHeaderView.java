package com.jiyouliang.fmap.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiyouliang.fmap.R;

/**
 * 头部
 */
public class MapHeaderView extends RelativeLayout {
    private OnMapHeaderViewClickListener mListener;
    private ImageView mIvUser;

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


        mIvUser = findViewById(R.id.iv_user);
    }

    public void setOnMapHeaderViewClickListener(OnMapHeaderViewClickListener listener){
        if(listener != null){
            this.mListener = listener;
        }
    }

    /**
     * MapHeaderView点击监听
     */
    public interface OnMapHeaderViewClickListener{
        /**
         * 点击用户
         */
        void onUserClick();

        /**
         * 点击搜索
         */
        void onSearchClick();

        /**
         * 点击语音识别
         */
        void onVoiceClick();

        /**
         * 点击二维码扫描
         */
        void onQrScanClick();
    }
}
