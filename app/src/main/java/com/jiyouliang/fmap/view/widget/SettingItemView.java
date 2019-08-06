package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiyouliang.fmap.R;

/**
 * @author YouLiang.Ji
 * <p>
 * 常用设置Item控件
 */
public class SettingItemView extends RelativeLayout {

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
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
        initViews();
    }

    private void initViews() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        mIvArrow = (ImageView) findViewById(R.id.iv_arrow);
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

        String subTitleText = ta.getString(R.styleable.SettingItemView_sivSubtitle);
        boolean subTitleVisiable = ta.getBoolean(R.styleable.SettingItemView_sivSubtitleVisiable, false);
        int subTitleSize = ta.getDimensionPixelSize(R.styleable.SettingItemView_sivSubtitleSize, getDefaultSubTitleTextSize());

        //设置内容
        setTitleText(titleText);
        setTitleVisiable(titleVisiable);
        setTitleSize(titleSize);
        if(titleColor != 0){
            setTitleColor(titleColor);
        }

        setSubTitleText(subTitleText);
        setSubTitleVisiable(subTitleVisiable);
        setSubTitleSize(subTitleSize);

        ta.recycle();
    }

    public void setTitleText(String title) {
        if(TextUtils.isEmpty(title)){
            return;
        }
        mTvTitle.setText(title);
    }

    public void setTitleVisiable(boolean visiable) {
        mTvTitle.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    /**
     * 文字大小
     * @param size px
     */
    public void setTitleSize(float size) {
        mTvTitle.setTextSize(size);
    }

    /**
     * 设置标题文字颜色
     * @param color
     */
    public void setTitleColor(int color){
        mTvTitle.setTextColor(color);
    }

    public void setSubTitleText(String title) {
        if(TextUtils.isEmpty(title)){
            return;
        }
        mTvSubtitle.setText(title);
    }

    public void setSubTitleVisiable(boolean visiable) {
        mTvSubtitle.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    /**
     * 文字大小
     * @param size px
     */
    public void setSubTitleSize(float size) {
        mTvSubtitle.setTextSize(size);
    }

    private int getDefaultTitleTextSize(){
        return (int) dp2px(DEFAULT_TITLE_TEXT_SIZE);
    }

    private int getDefaultSubTitleTextSize() {
        return (int) dp2px(DEFAULT_SUB_TITLE_TEXT_SIZE);
    }

    private float dp2px(float dpValue) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources()
                        .getDisplayMetrics());
    }
}
