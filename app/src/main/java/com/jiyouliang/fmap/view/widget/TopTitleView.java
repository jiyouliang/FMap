package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiyouliang.fmap.R;

/**
 * @author YouLiang.Ji
 * <p>
 * 顶部标题
 */
public class TopTitleView extends RelativeLayout implements View.OnClickListener {
    private ImageView mIvLeft;
    private TextView mTextRight;
    private boolean visiable;
    private ImageView mIvRight;
    private OnTopTitleViewClickListener mListener;
    private TextView mTvCenter;

    public TopTitleView(Context context) {
        this(context, null);
    }

    public TopTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化布局
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_top_title, this, true);

        initView();
        //加载自定义属性
        initFromAttributes(context, attrs);
        setListener();
    }

    /**
     * 统一处理事件相关
     */
    private void setListener() {
        mIvLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);
    }

    private void initFromAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopTitleView);
        boolean rightTitleVisiable = ta.getBoolean(R.styleable.TopTitleView_ttvRightTitleVisiable, true);
        Drawable drawableRight = ta.getDrawable(R.styleable.TopTitleView_ttvRightIcon);
        Drawable drawableLeft = ta.getDrawable(R.styleable.TopTitleView_ttvLeftIcon);
        String rightText = ta.getString(R.styleable.TopTitleView_ttvRightText);
        int rightTextSize = ta.getDimensionPixelSize(R.styleable.TopTitleView_ttvRightTextSize, 0);
        int rightTextColor = ta.getColor(R.styleable.TopTitleView_ttvRightTextColor, getResources().getColor(R.color.poi_detail_loc));

        setRightDrawable(drawableRight);
        setLeftDrawable(drawableLeft);
        setRightTextVisibility(rightTitleVisiable);

        ViewGroup.LayoutParams lpLeft = mIvLeft.getLayoutParams();
        ViewGroup.LayoutParams lpRight = mIvRight.getLayoutParams();
        // 左侧ImageView大小,默认位控件自身布局属性大小
        int leftSize = ta.getDimensionPixelSize(R.styleable.TopTitleView_ttvLeftSize, lpLeft.width);
        int rightSize = ta.getDimensionPixelSize(R.styleable.TopTitleView_ttvRightSize, lpRight.width);
        // 中间TextView
        String centerText = ta.getString(R.styleable.TopTitleView_ttvCenterText);
        int centerTextSize = ta.getDimensionPixelSize(R.styleable.TopTitleView_ttvCenterTextSize, 0);

        lpLeft.width = leftSize;
        lpLeft.height = leftSize;
        mIvLeft.setLayoutParams(lpLeft);

        lpRight.width = rightSize;
        lpRight.height = rightSize;
        mIvRight.setLayoutParams(lpRight);

        //右侧文字
        setRightText(rightText);
        setRightTextSize(rightTextSize);
        setRightTextColor(rightTextColor);
        //中间文字
        if (!TextUtils.isEmpty(centerText)) {
            setCenterText(centerText);
        }
        setCenterTextSize(centerTextSize);

        ta.recycle();
    }

    private void initView() {
        mIvLeft = findViewById(R.id.iv_left);
        mTextRight = findViewById(R.id.tv_subtitle);
        mTvCenter = findViewById(R.id.tv_center);
        mTextRight.setVisibility(View.GONE);

        mIvRight = findViewById(R.id.iv_right);
        mIvRight.setVisibility(View.GONE);
    }

    /**
     * 右侧文字
     *
     * @param text
     */
    public void setRightText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mIvRight.setVisibility(View.GONE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText(text);
    }

    public void setRightTextVisibility(boolean visiable) {
        mTextRight.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右侧图片
     *
     * @param drawable
     */
    public void setRightDrawable(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        mIvRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.GONE);
        mIvRight.setImageDrawable(drawable);
    }

    /**
     * 设置右侧图片资源
     *
     * @param resId
     */
    public void setRightDrawable(int resId) {
        setRightDrawable(getResources().getDrawable(resId));
    }


    /**
     * 设置左侧图片
     *
     * @param drawable
     */
    public void setLeftDrawable(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        mIvLeft.setVisibility(View.VISIBLE);
        mIvLeft.setImageDrawable(drawable);
    }

    /**
     * 设置左侧图片资源
     *
     * @param resId
     */
    public void setLeftDrawable(int resId) {
        setLeftDrawable(getResources().getDrawable(resId));
    }


    /**
     * 右侧文字大小
     *
     * @param textSize
     */
    public void setRightTextSize(float textSize) {
        if (textSize <= 0) {
            return;
        }
        mTextRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
    }

    /**
     * 右侧文字颜色
     *
     * @param textColor
     */
    private void setRightTextColor(int textColor) {
        mTextRight.setTextColor(textColor);
    }


    /**
     * 设置中间文字
     *
     * @param text
     */
    public void setCenterText(String text) {
        if (mTvCenter.getVisibility() != View.VISIBLE) {
            mTvCenter.setVisibility(View.VISIBLE);
        }
        mTvCenter.setText(text);
    }

    /**
     * 设置中间文字大小
     *
     * @param textSize
     */
    public void setCenterTextSize(float textSize) {
        if (textSize <= 0) {
            return;
        }
        mTvCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    /**
     * 获取中间TextView文字
     *
     * @return
     */
    public String getCenterText() {
        if (TextUtils.isEmpty(mTvCenter.getText())) {
            return null;
        }
        return mTvCenter.getText().toString().trim();
    }

    public void setOnTopTitleViewClickListener(OnTopTitleViewClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null || v == null) {
            return;
        }
        if (v == mIvLeft) {
            mListener.onLeftClick(v);
        }
        if (v == mIvRight) {
            mListener.onRightClick(v);
        }
    }

    /**
     * 点击回调监听
     */
    public interface OnTopTitleViewClickListener {

        /**
         * 点击左侧图片
         *
         * @param v
         */
        void onLeftClick(View v);

        /**
         * 点击右侧图片
         *
         * @param v
         */
        void onRightClick(View v);
    }
}
