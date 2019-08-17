package com.jiyouliang.fmap.view.widget;

import android.view.View;

/**
 * @author YouLiang.Ji
 * RecyclerView条目点击回调
 */
public interface OnItemClickListener {
    /**
     * 条目点击回调
     * @param v
     * @param position
     */
    void onItemClick(View v, int position);
}
