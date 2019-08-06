package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseActivity;

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
                default:
                    break;
            }

            return new UserDetailViewHolder(mContext, itemView, viewType);
        }

        /**
         * 布局生成View
         * @param parent
         * @param resId
         * @return
         */
        private View inflateLayout(ViewGroup parent, int resId) {
            return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(resId, parent, false);
        }

        @Override
        public void onBindViewHolder(@NonNull UserDetailViewHolder userDetailViewHolder, int position) {
            int viewType = getItemViewType(position);
            /*switch (viewType) {
                case TYPE_HEADER:
                    break;
                default:
                    break;
            }*/
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    /**
     * ViewHolder
     */
    private static class UserDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context mContext;
        ImageView ivLogo;
        private UserDetailViewHolder(Context context, @NonNull View itemView, int viewType) {
            super(itemView);
            this.mContext = context;
            if(viewType == TYPE_LOGIN){
                ivLogo = itemView.findViewById(R.id.iv_user_logo);
                ivLogo.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if(v == ivLogo){
                showUserLoginPage();
            }
        }

        private void showUserLoginPage(){
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        }
    }


}
