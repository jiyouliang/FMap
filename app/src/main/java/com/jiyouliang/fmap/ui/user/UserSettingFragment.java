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
 * 用户设置页面
 */
public class UserSettingFragment extends BaseFragment {

    private static final String KEY_PHONE = "phone";
    private OnFragmentInteractionListener mListener;
    private String mPhone;

    public UserSettingFragment() {
        // Required empty public constructor
    }

    public static UserSettingFragment newInstance(String phone) {
        UserSettingFragment fragment = new UserSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PHONE, phone);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mPhone = getArguments().getString(KEY_PHONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_setting, container, false);
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
