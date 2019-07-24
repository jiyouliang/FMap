package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiyouliang.fmap.R;

/**
 * 头部
 */
public class MapHeaderView extends RelativeLayout implements View.OnClickListener {
    private TextView mTvSearch;
    private ImageView mIvScan;
    private ImageView mIvVoice;
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


        initView();
        setListener();
    }


    private void initView() {
        mIvUser = findViewById(R.id.iv_user);
        mTvSearch = findViewById(R.id.tv_search);
        mIvScan = findViewById(R.id.iv_qr_scan);
        mIvVoice = findViewById(R.id.iv_voice);
    }

    private void setListener() {
        mIvUser.setOnClickListener(this);
        mTvSearch.setOnClickListener(this);
        mIvScan.setOnClickListener(this);
        mIvVoice.setOnClickListener(this);
    }

    public void setOnMapHeaderViewClickListener(OnMapHeaderViewClickListener listener) {
        if (listener != null) {
            this.mListener = listener;
        }
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        if (v == mIvUser) {
            mListener.onUserClick();
        } else if (v == mTvSearch) {
            mListener.onSearchClick();
        } else if (v == mIvScan) {
            mListener.onQrScanClick();
        } else if (v == mIvVoice) {
            mListener.onVoiceClick();
        }
    }

    /**
     * MapHeaderView点击监听
     */
    public interface OnMapHeaderViewClickListener {
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
