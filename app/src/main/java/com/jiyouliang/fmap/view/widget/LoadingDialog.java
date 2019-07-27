package com.jiyouliang.fmap.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jiyouliang.fmap.R;


/**
 * @author YouLiang.Ji
 * 加载进度Dialog
 */
public class LoadingDialog extends Dialog implements View.OnClickListener {

    private ImageView mIvLoading;
    private ImageView mIvClose;

    public LoadingDialog(@NonNull Context context) {
        this(context, 0);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        //通过样式style控制居中显示,背景色等
        super(context, R.style.BaseDialogTheme);
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_layout);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(false);

        mIvLoading = findViewById(R.id.iv_loading);
        mIvClose = findViewById(R.id.iv_close);

        mIvClose.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        //显示gif图片
        Glide.with(getContext()).load(R.drawable.amap_loading).into(mIvLoading);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }
        if(v == mIvClose){
            dismiss();
        }
    }
}
