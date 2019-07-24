package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.view.base.BaseIconView;

/**
 * 组队
 */
public class GroupTeamView extends BaseIconView {

    private static final String TAG = "GroupTeamView";

    public GroupTeamView(Context context) {
        this(context, null);
    }

    public GroupTeamView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.IconViewStyle);
    }

    public GroupTeamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    @Override
    public boolean createBackground() {
        setBackgroundResource(R.drawable.icon_middle_selector);
        return true;
    }

    @Override
    public boolean createIcon() {
        setIconBackground(R.drawable.icon_c_group);
        return true;
    }

}
