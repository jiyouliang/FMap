package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.DeviceUtils;

/**
 * @author YouLiang.Ji
 * <p>
 * 常用设置Item控件
 */
public class SettingItemView extends RelativeLayout {

    private static final String TAG = "SettingItemView";

    private TextView mTvTitle;
    private TextView mTvSubtitle;
    private ImageView mIvArrow;
    /**
     * 默认标题文字大小,dp
     */
    private static final int DEFAULT_TITLE_TEXT_SIZE = 14;

    /**
     * 默认副标题文字大小,dp
     */
    private static final int DEFAULT_SUB_TITLE_TEXT_SIZE = 12;
    private ImageView mIvRight;
    private int h;
    /**
     * 高度测量模式
     */
    private int mHeightMesasureMode;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // 关联布局
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.setting_item_view, this, true);
        initViews();
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initViews() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        mIvArrow = (ImageView) findViewById(R.id.iv_arrow);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
    }

    /**
     * 初始化自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        // 读取属性值
        String titleText = ta.getString(R.styleable.SettingItemView_sivTitle);
        boolean titleVisiable = ta.getBoolean(R.styleable.SettingItemView_sivTitleVisiable, true);
        int titleSize = ta.getDimensionPixelSize(R.styleable.SettingItemView_sivTitleSize, getDefaultTitleTextSize());
        int titleColor = ta.getColor(R.styleable.SettingItemView_sivTitleColor, 0);
        boolean titleIsBlod = ta.getBoolean(R.styleable.SettingItemView_sivTitleBlod, false);

        String subTitleText = ta.getString(R.styleable.SettingItemView_sivSubtitle);
        boolean subTitleVisiable = ta.getBoolean(R.styleable.SettingItemView_sivSubtitleVisiable, false);
        int subTitleSize = ta.getDimensionPixelSize(R.styleable.SettingItemView_sivSubtitleSize, getDefaultSubTitleTextSize());

        //右侧图片
        Drawable rightBackground = ta.getDrawable(R.styleable.SettingItemView_sivRightIvBackground);
        boolean rightVisiable = ta.getBoolean(R.styleable.SettingItemView_sivRightIvVisiable, false);

        //设置内容
        setTitleText(titleText);
        setTitleVisiable(titleVisiable);
        setTitleSize(titleSize);
        if (titleColor != 0) {
            setTitleColor(titleColor);
        }
        //设置文字加粗
        if (titleIsBlod) {
            TextPaint paint = mTvTitle.getPaint();
            paint.setFakeBoldText(true);
        }

        setSubTitleText(subTitleText);
        setSubTitleVisiable(subTitleVisiable);
        setSubTitleSize(subTitleSize);

        //右侧ImageView
        setRightImageBackground(rightBackground);
        setRightImageVisiable(rightVisiable);

        ta.recycle();
    }

    public void setTitleText(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        mTvTitle.setText(title);
    }

    public void setTitleVisiable(boolean visiable) {
        mTvTitle.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右侧图片是否可见
     *
     * @param visiable
     */
    public void setRightImageVisiable(boolean visiable) {
        if (mIvRight == null) {
            return;
        }
        // 已经显示,避免重绘
        if (visiable && mIvRight.getVisibility() == View.VISIBLE) {
            return;
        }
        mIvRight.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右侧图片背景
     *
     * @param drawable
     */
    public void setRightImageBackground(Drawable drawable) {
        if (mIvRight == null || drawable == null) {
            return;
        }
        mIvRight.setBackground(drawable);
    }

    /**
     * 设置右侧图片背景资源
     *
     * @param resId
     */
    public void setRightImageBackground(int resId) {
        setRightImageBackground(getResources().getDrawable(resId));
    }

    /**
     * 文字大小
     *
     * @param size px
     */
    public void setTitleSize(float size) {
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * 设置标题文字颜色
     *
     * @param color
     */
    public void setTitleColor(int color) {
        mTvTitle.setTextColor(color);
    }

    public void setSubTitleText(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        mTvSubtitle.setText(title);
    }

    public void setSubTitleVisiable(boolean visiable) {
        mTvSubtitle.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    /**
     * 文字大小
     *
     * @param size px
     */
    public void setSubTitleSize(float size) {
        mTvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    private int getDefaultTitleTextSize() {
        return DeviceUtils.dip2px(getContext(), DEFAULT_TITLE_TEXT_SIZE);
    }

    private int getDefaultSubTitleTextSize() {
        return DeviceUtils.dip2px(getContext(), DEFAULT_SUB_TITLE_TEXT_SIZE);
    }



}
