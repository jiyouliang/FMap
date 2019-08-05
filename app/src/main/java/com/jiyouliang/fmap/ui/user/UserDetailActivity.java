package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseActivity;

/**
 * 用户详情页
 */
public class UserDetailActivity extends BaseActivity {

    private RecyclerView mRecycleView;

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

        /**
         * 头部图片
         */
        private static final int TYPE_HEADER = 0;

        /**
         * 点击登录部分
         */
        private static final int TYPE_LOGIN = 1;

        @NonNull
        @Override
        public UserDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            int viewType = getItemViewType(position);
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null) {
                return null;
            }
            View itemView = null;
            switch (viewType) {
                case TYPE_HEADER:
                    itemView = inflater.inflate(R.layout.user_detail_header_recycle_item, parent, false);
                    break;
                case TYPE_LOGIN:
                    itemView = inflater.inflate(R.layout.user_detail_login_recycle_item, parent, false);
                    break;
                default:
                    break;
            }

            return new UserDetailViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UserDetailViewHolder userDetailViewHolder, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case TYPE_HEADER:

                    break;
                default:
                    break;
            }
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    /**
     * ViewHolder
     */
    private static class UserDetailViewHolder extends RecyclerView.ViewHolder {

        private UserDetailViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
