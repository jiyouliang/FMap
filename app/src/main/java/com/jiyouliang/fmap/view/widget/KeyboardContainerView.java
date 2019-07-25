package com.jiyouliang.fmap.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author YouLiang.Ji
 * 用将NumberKeyboardView和KeyboardInputView两个控件包装起来,便于统一处理键盘点击和键盘输入文字
 */
public class KeyboardContainerView extends LinearLayout {

    private KeyboardInputView mKeyboardInputView;
    private NumberKeyboardView mNumberKeyboardView;
    private OnTextChangedListener mListener;
    // 当前是否可以输入验证码,用于屏同一时间多次输入问题
    private boolean mInputEnable = true;

    public KeyboardContainerView(Context context) {
        this(context, null);
    }

    public KeyboardContainerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 布局文件加载完毕
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int count = getChildCount();
        if (count == 0) {
            return;
        }
        //find views in layout
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof KeyboardInputView) {
                mKeyboardInputView = (KeyboardInputView) child;
                mKeyboardInputView.setOnTextChangedListener(new KeyboardInputView.OnTextChangedListener() {
                    @Override
                    public void beforeTextChanged(String text) {

                    }

                    @Override
                    public void afterTextChanged(String text) {
                        if(mListener != null){
                            mListener.onTextChanged(text);
                        }
                    }
                });
            }
            if (child instanceof NumberKeyboardView) {
                mNumberKeyboardView = (NumberKeyboardView) child;
                mNumberKeyboardView.setOnKeyboardClickListener(new NumberKeyboardView.OnKeyboardClickListener() {
                    @Override
                    public void onNumberKeyClick(String num) {
                        if (TextUtils.isEmpty(num)) {
                            return;
                        }
                        if(mInputEnable){
                            mKeyboardInputView.setText(num);
                        }
                    }

                    @Override
                    public void onDeleteClick() {
                        mKeyboardInputView.deleteText();
                    }
                });
            }
        }

    }

    public void setOnTextChangedListener(OnTextChangedListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置当前是否可以向键盘输入内容
     * @param state
     */
    public void setInputEnable(boolean state){
        this.mInputEnable = state;
    }


    /**
     * 输入文本改变回调,当点击键盘上的内容,回调监听器方法
     */
    public interface OnTextChangedListener {
        /**
         * 一次返回输入的所有文本信息
         *
         * @param text
         */
        void onTextChanged(String text);
    }
}
