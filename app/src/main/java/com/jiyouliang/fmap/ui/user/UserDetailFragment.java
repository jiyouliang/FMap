package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.server.task.SharedPreferencesTask;
import com.jiyouliang.fmap.ui.BaseFragment;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.StatusBarUtils;
import com.jiyouliang.fmap.util.UserUtils;
import com.jiyouliang.fmap.view.widget.SettingItemView;
import com.jiyouliang.fmap.view.widget.TopTitleView;

/**
 * @author jiyouliang
 * 用户详情
 * 由于Activity包含多个Fragment,Fragment之间通信通过接口回调{@link BaseFragment.OnFragmentInteractionListener}
 * 处理,该回调由包含的Activity实现,并通过Fragment分发通信.
 * <p>
 * 通过 {@link UserSendSmsFragment#newInstance} 该Fragment实例对象.
 */
public class UserDetailFragment extends BaseFragment implements TopTitleView.OnTopTitleViewClickListener, OnItemClickListener {

    private static final String TAG = "UserDetailFragment";
    private OnFragmentInteractionListener mListener;
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
    private static final String KEY_PHONE = "phone";
    private TopTitleView mTopTitleView;
    private int mTitleHeight;
    private int mTotalDy;
    private String mPhone;
    private UserDetailAdapter mAdapter;

    public UserDetailFragment() {
        // Required empty public constructor
    }

    public static UserDetailFragment newInstance(String phone) {
        UserDetailFragment fragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        if(!TextUtils.isEmpty(phone)){
            bundle.putString(KEY_PHONE, phone);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhone = getArguments().getString(KEY_PHONE);
        }
        SharedPreferencesTask spTask = new SharedPreferencesTask();
        String savedPhone = spTask.getUserPhone(getContext());
        // 传递手机号参数为空,并且存储手机号不为空,UI显示存储手机号
        if (TextUtils.isEmpty(mPhone) && !TextUtils.isEmpty(savedPhone)) {
            mPhone = savedPhone;
            return;
        }
        // 手机号已经存储过
        if (!TextUtils.isEmpty(savedPhone) && mPhone.equals(savedPhone)) {
            return;
        }
        //手机号没有存储,或者和存储手机号不一致,重新存储
        spTask.saveUserPhone(getContext(), mPhone);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_detail, container, false);
        initView(rootView);
        initData();
        return rootView;
    }

    private void initView(View rootView) {
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(layoutManager);
        mTopTitleView = (TopTitleView) rootView.findViewById(R.id.ttv);

        StatusBarUtils.getInstance().enableTranslucentStatusBar(getActivity(), mTitleHeight);
        setListener();
    }

    private void setListener() {
        mTopTitleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTitleHeight = mTopTitleView.getMeasuredHeight();
            }
        });

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalDy -= dy;
                log(String.format("onScrolled, dy=%s, mTotalDy=%s", dy, mTotalDy));

                int t = Math.abs(mTotalDy);
                if (t <= 0) {
                    //顶部图处于最顶部，标题栏透明
                    mTopTitleView.setBackgroundColor(Color.argb(0, 255, 255, 255));
                    getActivity().getWindow().setStatusBarColor(Color.argb((int) 0, 255, 255, 255));
                    mTopTitleView.setRightDrawable(R.drawable.user_detail_setting_light_icon);
                    mTopTitleView.setLeftDrawable(R.drawable.user_detail_close_light_icon);
                } else if (t > 0 && t < mTitleHeight) {
                    //滑动过程中，渐变
                    float scale = (float) t / mTitleHeight;//算出滑动距离比例
                    float alpha = (255 * scale);//得到透明度
                    log(String.format("alpha=%s", alpha));
                    mTopTitleView.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                    getActivity().getWindow().setStatusBarColor(Color.argb((int) alpha, 255, 255, 255));
                    // 标题栏深色icon
                    mTopTitleView.setRightDrawable(R.drawable.user_detail_setting_black_icon);
                    mTopTitleView.setLeftDrawable(R.drawable.user_detail_close_black_icon);
                } else {
                    //过顶部图区域，标题栏定色
                    mTopTitleView.setBackgroundColor(Color.argb(255, 255, 255, 255));
                    getActivity().getWindow().setStatusBarColor(Color.argb(255, 255, 255, 255));
                    mTopTitleView.setRightDrawable(R.drawable.user_detail_setting_black_icon);
                    mTopTitleView.setLeftDrawable(R.drawable.user_detail_close_black_icon);
                }
            }
        });

        mTopTitleView.setOnTopTitleViewClickListener(this);
    }

    private void initData() {
        mAdapter = new UserDetailAdapter(mPhone, this);
//        mAdapter.setOnItemClickListener(this);
        mRecycleView.setAdapter(mAdapter);
    }

    private void showUserInfoPage() {
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            builder.appendQueryParameter("fragment", UserInfoFragment.class.getSimpleName());
            if(!TextUtils.isEmpty(mPhone)){
                builder.appendQueryParameter("phone", mPhone);
            }
            Uri uri = Uri.parse(builder.toString());
            mListener.onFragmentInteraction(uri);
        }
    }

    private void showUserLoginPage() {
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            builder.appendQueryParameter("fragment", "UserSendSmsFragment");
            Uri uri = Uri.parse(builder.toString());
            mListener.onFragmentInteraction(uri);
        }
    }

    private void showUserSettingPage() {
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            builder.appendQueryParameter("fragment", UserSettingFragment.class.getSimpleName());
            // 避免出现phone="null",而不是phone=null
            if(!TextUtils.isEmpty(mPhone)){
                builder.appendQueryParameter("phone", mPhone);
            }
            Uri uri = Uri.parse(builder.toString());
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onItemClick(View v, int position) {
        log(String.format("onItemClick, position=%s, v=%s", position, v));
        if(position == TYPE_LOGIN){
            if(TextUtils.isEmpty(mPhone)){
                showUserLoginPage();
            }else {
                showUserInfoPage();
            }
        }
    }

    @Override
    public void onLeftClick(View v) {
        // 回退页面
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            builder.appendQueryParameter("fragment", "back");
            Uri uri = Uri.parse(builder.toString());
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onRightClick(View v) {
        // 进入设置页面
        showUserSettingPage();
    }


    /**
     * 用户详情页Adapter
     */
    private static class UserDetailAdapter extends RecyclerView.Adapter<UserDetailViewHolder> implements View.OnClickListener {

        private static final String[] TITLES = new String[]{"我是商家", "我的反馈", "我的订单", "我的钱包", "我的小程序", "我的评论", "特别鸣谢", "帮助中心"};
        private static final String[] SUBTITLES = new String[]{"【免费】新增地点、认领店铺", "", "查看我的全部订单", "", "", "", "感谢高德热心用户", ""};
        private OnItemClickListener mListener;
        private String mPhone;

        private Context mContext;

        public UserDetailAdapter(String phone, OnItemClickListener listener) {
            this.mPhone = phone;
            this.mListener = listener;
        }


        public void setPhone(String phone) {
            this.mPhone = phone;
            //刷新
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UserDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            this.mContext = parent.getContext();
            int viewType = getItemViewType(position);
            View itemView = null;
            switch (position) {
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
            if (itemView != null) {
                itemView.setTag(position);
                itemView.setOnClickListener(this);
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
            int viewType = getItemViewType(position);

            switch (viewType) {
                case TYPE_HEADER:
                    // 头部item
                    break;
                case TYPE_LOGIN:
                    // 登录模块
                    // 手机号存在,说明登录成功,修改用户详情UI
                    setLoginUI(viewHolder);
                    break;
                case TYPE_FAVORITE:
                    break;
                case TYPE_MEDAL:
                    if (!TextUtils.isEmpty(mPhone)) {
                        viewHolder.tvMyAchieved.setVisibility(View.GONE);
                    }
                    break;
                case TYPE_DATA_CONTRIBUTE:
                    break;
                default:
                    // 末尾常规项
                    if (position >= 5) {
                        String title = TITLES[position - 5];
                        String subtitle = SUBTITLES[position - 5];
                        if (TextUtils.isEmpty(subtitle)) {
                            viewHolder.mSettingItemView.setSubTitleVisiable(false);
                        } else {
                            viewHolder.mSettingItemView.setSubTitleVisiable(true);
                            viewHolder.mSettingItemView.setSubTitleText(subtitle);
                        }
                        viewHolder.mSettingItemView.setTitleText(title);
                    }
                    break;
            }

        }

        /**
         * 设置登录用户UI
         *
         * @param viewHolder
         */
        private void setLoginUI(@NonNull UserDetailViewHolder viewHolder) {
            if (!TextUtils.isEmpty(mPhone)) {
                viewHolder.ivLogo.setBackgroundResource(R.drawable.user_detail_logined_boy);
                viewHolder.tvLoginTip.setVisibility(View.GONE);
                viewHolder.tvLogin.setText(UserUtils.getUserName(mPhone));
                viewHolder.setLoginStatus(true);
                viewHolder.setPhone(mPhone);
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


        @Override
        public void onClick(View v) {
            if (v == null || mListener == null) {
                return;
            }
            // 回调OnItemClick
            if (v.getTag() instanceof Integer) {
                mListener.onItemClick(v, (Integer) v.getTag());
            }
        }
    }

    /**
     * ViewHolder
     */
    private static class UserDetailViewHolder extends RecyclerView.ViewHolder {

        private int mViewType;
        private Context mContext;
        private boolean mLoginStatus;
        ImageView ivLogo;
        SettingItemView mSettingItemView;
        TextView tvLoginTip;
        TextView tvLogin;
        TextView tvMyAchieved;
        String mPhone;


        private UserDetailViewHolder(Context context, @NonNull View itemView, int viewType) {
            super(itemView);
            this.mContext = context;
            mViewType = viewType;
            switch (viewType) {
                case TYPE_HEADER:
                    break;
                case TYPE_LOGIN:
                    ivLogo = itemView.findViewById(R.id.iv_user_logo);
                    tvLoginTip = (TextView) itemView.findViewById(R.id.tv_experience_more);
                    tvLogin = (TextView) itemView.findViewById(R.id.tv_login);
                    break;
                case TYPE_FAVORITE:
                    break;
                case TYPE_MEDAL:
                    tvMyAchieved = (TextView) itemView.findViewById(R.id.tv_my_achieved);
                    break;
                case TYPE_DATA_CONTRIBUTE:
                    break;
                default:
                    //常规列
                    mSettingItemView = itemView.findViewById(R.id.siv);
                    break;
            }
        }

        /**
         * 登录状态
         *
         * @param status
         */
        void setLoginStatus(boolean status) {
            this.mLoginStatus = status;
        }

        void setPhone(String phone) {
            this.mPhone = phone;
        }

    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
