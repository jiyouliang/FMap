package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseFragment;

/**
 * 用户信息Fragment,登录成功后点击用户头像进入该页面
 */
public class UserInfoFragment extends BaseFragment {
    private static final String KEY_PHONE = "phone";

    private String mPhone;

    private OnFragmentInteractionListener mListener;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    /**
     *
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
        return rootView;
    }

    private void initView(View rootView) {

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
