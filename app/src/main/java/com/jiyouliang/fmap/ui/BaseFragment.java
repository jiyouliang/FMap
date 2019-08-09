package com.jiyouliang.fmap.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * @author YouLiang.Ji
 *
 * 所有Fragment均继承该类,便于后续开发与维护
 *
 */
public class BaseFragment extends Fragment {


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener{
        void onFragmentInteraction(Uri uri);
    }

    protected void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
