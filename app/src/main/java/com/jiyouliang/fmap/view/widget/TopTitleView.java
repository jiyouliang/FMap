package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
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
    private TextView mTvRight;
    private boolean visiable;

    public TopTitleView(Context context) {
        this(context, null);
    }

    public TopTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_top_title, this, true);

        initView();
        //加载自定义属性
        initFromAttributes(context, attrs);
    }

    private void initFromAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopTitleView);
        boolean rightTitleVisiable = ta.getBoolean(R.styleable.TopTitleView_ttvRightTitleVisiable, true);
        ta.recycle();

        setRightTextVisibility(rightTitleVisiable);
    }

    private void initView() {
        mIvLeft = findViewById(R.id.iv_left);
        mTvRight = findViewById(R.id.tv_right);
    }

    public void setRightTextVisibility(boolean visiable) {
        mTvRight.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }
}
