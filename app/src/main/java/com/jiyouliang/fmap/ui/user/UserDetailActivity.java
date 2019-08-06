package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.view.widget.SettingItemView;

/**
 * 用户详情页
 */
public class UserDetailActivity extends BaseActivity {

    private RecyclerView mRecycleView;
    /**
     * 头部图片
     */
    private static final int TYPE_HEADER = 0;

    /**
     * 点击登录部分
     */
    private static final int TYPE_LOGIN = 1;

    /**
     * 收藏夹、离线地图等工具
     */
    private static final int TYPE_FAVORITE = 2;

    /**
     * 我的成就、勋章
     */
    private static final int TYPE_MEDAL = 3;

    /**
     * 数据贡献
     */
    private static final int TYPE_DATA_CONTRIBUTE = 4;

    /**
     * 常规列
     */
    private static final int TYPE_NORMAL = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initView();
        initData();
    }

    private void initView() {
        mRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
    }

    private void initData() {
        UserDetailAdapter adapter = new UserDetailAdapter();
        mRecycleView.setAdapter(adapter);
    }

    /**
     * 用户详情页Adapter
     */
    private static class UserDetailAdapter extends RecyclerView.Adapter<UserDetailViewHolder> {

        private static final String[] TITLES = new String[]{"我是商家", "我的反馈", "我的订单", "我的钱包", "我的小程序", "我的评论", "特别鸣谢", "帮助中心"};
        private static final String[] SUBTITLES = new String[]{"【免费】新增地点、认领店铺", "", "查看我的全部订单", "", "", "", "感谢高德热心用户", ""};

        private Context mContext;

        @NonNull
        @Override
        public UserDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            this.mContext = parent.getContext();
            int viewType = getItemViewType(position);
            View itemView = null;
            switch (viewType) {
                case TYPE_HEADER:
                    itemView = inflateLayout(parent, R.layout.user_detail_header_recycle_item);
                    break;
                case TYPE_LOGIN:
                    itemView = inflateLayout(parent, R.layout.user_detail_login_recycle_item);
                    break;
                case TYPE_FAVORITE:
                    itemView = inflateLayout(parent, R.layout.user_detail_fav_tools_recycle_item);
                    break;
                case TYPE_MEDAL:
                    itemView = inflateLayout(parent, R.layout.user_detail_medal_level_recycle_item);
                    break;
                case TYPE_DATA_CONTRIBUTE:
                    itemView = inflateLayout(parent, R.layout.user_detail_data_contribute_recycle_item);
                    break;
                default:
                    itemView = inflateLayout(parent, R.layout.user_detail_normal_recycle_item);
                    break;
            }

            return new UserDetailViewHolder(mContext, itemView, viewType);
        }

        /**
         * 布局生成View
         *
         * @param parent
         * @param resId
         * @return
         */
        private View inflateLayout(ViewGroup parent, int resId) {
            return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(resId, parent, false);
        }

        @Override
        public void onBindViewHolder(@NonNull UserDetailViewHolder viewHolder, int position) {
            //int viewType = getItemViewType(position);
            // 末尾常规项
            if(position >= 5){
                String title = TITLES[position - 5];
                String subtitle = SUBTITLES[position - 5];
                if(TextUtils.isEmpty(subtitle)){
                    viewHolder.mSettingItemView.setSubTitleVisiable(false);
                }else{
                    viewHolder.mSettingItemView.setSubTitleVisiable(true);
                    viewHolder.mSettingItemView.setSubTitleText(subtitle);
                }
                viewHolder.mSettingItemView.setTitleText(title);
            }
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 5 + TITLES.length;
        }
    }

    /**
     * ViewHolder
     */
    private static class UserDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context mContext;
        ImageView ivLogo;
        SettingItemView mSettingItemView;

        private UserDetailViewHolder(Context context, @NonNull View itemView, int viewType) {
            super(itemView);
            this.mContext = context;
            switch (viewType) {
                case TYPE_LOGIN:
                    ivLogo = itemView.findViewById(R.id.iv_user_logo);
                    ivLogo.setOnClickListener(this);
                    break;
                default:
                    //常规列
                    mSettingItemView = itemView.findViewById(R.id.siv);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            if (v == ivLogo) {
                showUserLoginPage();
            }
        }

        private void showUserLoginPage() {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        }
    }


}
