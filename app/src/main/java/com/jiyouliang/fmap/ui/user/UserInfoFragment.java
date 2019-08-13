package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseFragment;
import com.jiyouliang.fmap.util.UserUtils;
import com.jiyouliang.fmap.view.widget.SettingItemView;
import com.jiyouliang.fmap.view.widget.TopTitleView;

/**
 * 用户信息Fragment,登录成功后点击用户头像进入该页面
 */
public class UserInfoFragment extends BaseFragment implements TopTitleView.OnTopTitleViewClickListener, View.OnClickListener {
    private static final String KEY_PHONE = "phone";

    private String mPhone;

    private OnFragmentInteractionListener mListener;
    private TopTitleView mTopTitleView;
    private SettingItemView mSivNickName;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * @param phone
     * @return
     */
    public static UserInfoFragment newInstance(String phone) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PHONE, phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhone = getArguments().getString(KEY_PHONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        initView(rootView);
        setListener();
        return rootView;
    }

    private void initView(View rootView) {
        mTopTitleView = (TopTitleView) rootView.findViewById(R.id.ttv);
        mSivNickName = (SettingItemView)rootView.findViewById(R.id.siv_nickname);
        if(rootView instanceof ViewGroup){
            int count = ((ViewGroup) rootView).getChildCount();
            for (int i = 0; i < count; i++) {
                View child = ((ViewGroup) rootView).getChildAt(i);
                if(child instanceof ViewGroup){
                    child.setOnClickListener(this);
                }
            }
        }
        if(!TextUtils.isEmpty(mPhone)){
            mSivNickName.setSubTitleText(UserUtils.getUserName(mPhone));
        }

    }

    private void setListener() {
                mTopTitleView.setOnTopTitleViewClickListener(this);
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

    @Override
    public void onLeftClick(View v) {
        back();
    }

    /**
     * 返回上一页
     */
    private void back() {
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            builder.appendQueryParameter("fragment", "back");
            Uri uri = Uri.parse(builder.toString());
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void onClick(View v) {

    }
}
