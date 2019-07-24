package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jiyouliang.fmap.R;

/**
 * poi详情View，该View固定在地图底部
 */
public class PoiDetailBottomView extends ConstraintLayout implements View.OnClickListener {
    private TextView mTvPoiDetail;
    private OnPoiDetailBottomClickListener mListener;
    private int poiDetailState;//显示状态
    //查看详情
    public static final int STATE_DETAIL = 0;
    //显示地图
    public static final int STATE_MAP = 1;

    public PoiDetailBottomView(Context context) {
        this(context, null);
    }

    public PoiDetailBottomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PoiDetailBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //关联布局文件
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.poi_detail_fix_bottom, this, true);

        //init View
        initView();
        setListener();
    }


    private void initView() {
        mTvPoiDetail = (TextView) findViewById(R.id.tv_detail);
    }

    private void setListener() {
        mTvPoiDetail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (null != mListener) {
            if (v == mTvPoiDetail) {
                mListener.onDetailClick();
            }
        }
    }

    public void setOnPoiDetailBottomClickListener(OnPoiDetailBottomClickListener listener) {
        this.mListener = listener;
    }

    /**
     * PoiDetailBottomView点击监听
     */
    public interface OnPoiDetailBottomClickListener {
        /**
         * 点击查看详情
         */
        void onDetailClick();

        /**
         * 打车
         */
        void onCallTaxiClick();

        /**
         * 路线
         */
        void onRouteClick();
    }

    /**
     * 显示查看详情状态
     */
    private void showPoiDetailState() {
        if (null == mTvPoiDetail) {
            return;
        }
        Drawable drawableLeft = getResources().getDrawable(R.drawable.poi_indicator_details_selector);
        mTvPoiDetail.setText("查看详情");
        mTvPoiDetail.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);

    }

    /**
     * 显示返回地图状态
     */
    private void showBackMapState() {
        if (null == mTvPoiDetail) {
            return;
        }
        Drawable drawableLeft = getResources().getDrawable(R.drawable.poi_indicator_map_selector);
        mTvPoiDetail.setText("显示地图");
        mTvPoiDetail.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
    }

    /**
     * 设置poi detail显示状态
     *
     * @param state @see {@link #STATE_DETAIL}{@link #STATE_MAP}
     *
     */
    public void setPoiDetailState(int state) {
        this.poiDetailState = state;
        switch (state) {
            case STATE_DETAIL:
                showPoiDetailState();
                break;
            case STATE_MAP:
                showBackMapState();
                break;
        }
    }

    /**
     * 返回poi detail显示状态
     *
     * @return
     */
    public int getPoiDetailState() {
        return poiDetailState;
    }
}
