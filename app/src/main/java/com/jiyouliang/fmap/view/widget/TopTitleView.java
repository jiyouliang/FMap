package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiyouliang.fmap.R;

/**
 * @author YouLiang.Ji
 * <p>
 * 顶部标题
 */
public class TopTitleView extends RelativeLayout {
    private ImageView mIvLeft;
    private TextView mTextRight;
    private boolean visiable;
    private ImageView mIvRight;

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
    }

    private void initFromAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopTitleView);
        boolean rightTitleVisiable = ta.getBoolean(R.styleable.TopTitleView_ttvRightTitleVisiable, true);
        Drawable drawableRight = ta.getDrawable(R.styleable.TopTitleView_ttvRightIcon);
        setRightDrawable(drawableRight);
        ta.recycle();
        setRightTextVisibility(rightTitleVisiable);
    }

    private void initView() {
        mIvLeft = findViewById(R.id.iv_left);
        mTextRight = findViewById(R.id.tv_right);
        mTextRight.setVisibility(View.GONE);

        mIvRight = findViewById(R.id.iv_right);
        mIvRight.setVisibility(View.GONE);
    }

    /**
     * 右侧文字
     * @param text
     */
    public void setRightText(String text){
        mIvRight.setVisibility(View.GONE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText(text);
    }

    public void setRightTextVisibility(boolean visiable) {
        mTextRight.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右侧图片
     * @param drawable
     */
    public void setRightDrawable(Drawable drawable){
        if(drawable == null){
            return;
        }
        mIvRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.GONE);
        mIvRight.setImageDrawable(drawable);
    }

    public void setRightDrawable(int resId){
        mIvRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.GONE);
        mIvRight.setImageDrawable(getResources().getDrawable(resId));
    }
}
