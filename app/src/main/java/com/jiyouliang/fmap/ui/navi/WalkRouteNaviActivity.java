package com.jiyouliang.fmap.ui.navi;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.jiyouliang.fmap.R;


/**
 * 步行导航
 */
public class WalkRouteNaviActivity extends BaseAMapNaviActivity {

    private static final String PARAMS = "params";;
    private static final String KEY_STARTLATLNG = "startLatLng";;
    private static final String KEY_STOPTLATLNG = "stopLatLng";;
    private LatLng startLatLng;
    private LatLng stopLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_amap_walk_navi);
        initView(savedInstanceState);
        initData();

    }

    private void initView(Bundle savedInstanceState) {
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
    }

    private void initData() {
        Intent intent = getIntent();

        if(intent != null && intent.getBundleExtra(PARAMS) != null){
            // 获取页面传递过来的起点和终点坐标
            Bundle bundle = intent.getBundleExtra(PARAMS);

            startLatLng = bundle.getParcelable(KEY_STARTLATLNG);
            stopLatLng = bundle.getParcelable(KEY_STOPTLATLNG);
        }
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        if(startLatLng != null && stopLatLng != null){
//            mAMapNavi.calculateWalkRoute(new NaviLatLng(22.560888, 113.884791), new NaviLatLng(22.552335, 113.887538));
            mAMapNavi.calculateWalkRoute(new NaviLatLng(startLatLng.latitude, startLatLng.longitude),
                    new NaviLatLng(stopLatLng.latitude, stopLatLng.longitude));
        }

    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        if(startLatLng != null && stopLatLng != null){
            mAMapNavi.startNavi(NaviType.GPS);
        }
    }
}
