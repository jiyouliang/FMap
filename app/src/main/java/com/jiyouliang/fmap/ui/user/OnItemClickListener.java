package com.jiyouliang.fmap.ui.user;

/**
 * @author YouLiang.Ji
 */

import android.view.View;

/**
 * 自定义RecyclerView item点击监听
 */
public interface OnItemClickListener {
    /**
     * 条目点击回调
     *
     * @param v        itemview对象
     * @param position item位置
     */
    void onItemClick(View v, int position);
}

