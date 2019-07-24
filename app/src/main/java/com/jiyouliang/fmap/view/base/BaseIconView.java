package com.jiyouliang.fmap.view.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.jiyouliang.fmap.R;

/**
 * 带图标控件
 *
 * @version 1.2
 */
public abstract class BaseIconView extends RelativeLayout implements IconViewInterface {

    private View mIconView;

    public BaseIconView(Context context) {
        this(context, null);
    }

    public BaseIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init Views
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_base_icon, this, true);
        mIconView = findViewById(R.id.iv_icon);
        boolean isCreateBg = createBackground();
        boolean isCreateIcon = createIcon();
        //未设置背景图片和icon图片，才读取自定义属性
        if (!(isCreateBg && isCreateIcon)) {
            //自定义属性
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseIconView);
            if (null != ta) {
                if (null == getBackground()) {
                    //未设置background属性，则解析自定义background属性值
                    int bgResId = ta.getResourceId(R.styleable.BaseIconView_biv_background, 0);
                    if (bgResId != 0) {
                        setBackgroundResource(bgResId);
                    }
                }
                //控件中心图片资源属性
                int iconResId = ta.getResourceId(R.styleable.BaseIconView_biv_icon, 0);
                if (iconResId != 0 && null != mIconView) {

                    mIconView.setBackgroundResource(iconResId);
                }
                ta.recycle();
            }
        }
        //initBackground();
    }

    /**
     * 设置icon背景
     *
     * @param resId
     */
    public void setIconBackground(int resId) {
        if (mIconView != null) {
            mIconView.setBackgroundResource(resId);
        }
    }


    /**
     * 设置icon背景
     *
     * @param drawable
     */
    public void setIconBackground(Drawable drawable) {
        if (mIconView != null) {
            mIconView.setBackground(drawable);
        }
    }

    public void setIconViewSelected(boolean pressed) {
        if (mIconView != null) {
            mIconView.setSelected(pressed);
        }
    }

    /**
     * 获取icon所属View selected状态
     * @return
     */
    public boolean isIconViewSelected(){
        if(mIconView != null){
            return mIconView.isSelected();
        }
        return false;
    }

    public View getIconView() {
        return mIconView;
    }

}
