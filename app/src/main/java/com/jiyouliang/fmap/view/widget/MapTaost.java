package com.jiyouliang.fmap.view.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * @author YouLiang.Ji
 */
public class MapTaost extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public MapTaost(Context context) {
        super(context);


    }




    @Override
    public void setView(View view) {
        super.setView(view);
    }
}
