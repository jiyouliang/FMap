package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.DeviceUtils;

/**
 * @author YouLiang.Ji
 * 垂直布局View：上部分是ImageView,下部分为TextView
 */
public class VerticalIconTextView extends LinearLayout {

    private ImageView mIvIcon;
    private TextView mTvTitle;

    public VerticalIconTextView(Context context) {
        this(context, null);
    }

    public VerticalIconTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalIconTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VerticalIconTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        initViews();
        initFromAttributes(attrs);
    }

    /**
     * 初始化View
     */
    private void initViews() {
        mIvIcon = new ImageView(getContext());
        mIvIcon.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTvTitle = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.padding_size);
        mTvTitle.setLayoutParams(lp);
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, DeviceUtils.dip2px(getContext(), 14));
        mTvTitle.setTextColor(getResources().getColor(R.color.user_detail_color_black));

        removeAllViews();
        addView(mIvIcon);
        addView(mTvTitle);
    }

    /**
     * 初始化自定义属性,并设置到控件中
     * @param attrs
     */
    private void initFromAttributes(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalIconTextView);
        Drawable drawable = ta.getDrawable(R.styleable.VerticalIconTextView_vitIcon);
        String title = ta.getString(R.styleable.VerticalIconTextView_vitTitle);

        setIconDrawable(drawable);
        setTitleText(title);

        ta.recycle();
    }

    public void setTitleText(String title){
        if(TextUtils.isEmpty(title)){
            return;
        }
        mTvTitle.setText(title);
    }

    public void setIconDrawable(Drawable drawable){
        if(drawable == null){
            return;
        }
        mIvIcon.setImageDrawable(drawable);
    }
}
