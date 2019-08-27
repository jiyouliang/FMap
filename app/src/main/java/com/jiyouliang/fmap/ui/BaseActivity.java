package com.jiyouliang.fmap.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jiyouliang.fmap.R;

public class BaseActivity extends AppCompatActivity {

    private View mToastView;
    private TextView mTvToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToast();
    }

    private void initToast() {
        mToastView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null, false);
        mTvToast = mToastView.findViewById(R.id.tv_toast);
    }

    protected void showToast(String text){
        if(mToastView == null){
            initToast();
        }
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mTvToast.setText(text);
        toast.setView(mToastView);
        toast.setGravity(Gravity.CENTER, 0, 200);
        toast.show();
    }
}
