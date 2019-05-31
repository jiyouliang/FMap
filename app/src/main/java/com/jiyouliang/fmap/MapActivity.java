package com.jiyouliang.fmap;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.PermissionUtil;
import com.jiyouliang.fmap.view.GPSView;
import com.jiyouliang.fmap.view.NearbySearchView;

public class MapActivity extends BaseActivity implements GPSView.OnGPSViewClickListener, NearbySearchView.OnNearbySearchViewClickListener {
    private static final String TAG = "MapActivity";
    /**
     * 首次进入申请定位、sd卡权限
     */
    private static final int REQ_CODE_INIT = 0;
    private static final int REQ_CODE_FINE_LOCATION = 1;
    private static final int REQ_CODE_STORAGE = 2;
    private MapView mMapView;
    private AMap aMap;
    private UiSettings mUiSettings;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private GPSView mGpsView;
    private MyLocationStyle mLocationStyle;
    private Marker mLocationMarker;
    private NearbySearchView mNearbySearcyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        initMap(savedInstanceState);

    }

    private void initView() {
        mGpsView = (GPSView) findViewById(R.id.gps_view);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        mNearbySearcyView = (NearbySearchView) findViewById(R.id.nearby_view);

        mGpsView.setOnGPSViewClickListener(this);
        mNearbySearcyView.setOnNearbySearchViewClick(this);
    }

    private void initMap(Bundle savedInstanceState) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        aMap.setMyLocationEnabled(true);//开启定位蓝点
        setLocationStyle();
        mUiSettings = aMap.getUiSettings();
        //隐藏缩放控件
        mUiSettings.setZoomControlsEnabled(false);

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();  //初始化AMapLocationClientOption对象
        mLocationOption.setLocationCacheEnable(true);//开启定位缓存
        mLocationOption.setInterval(2000);//定位时间间隔
        mLocationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度模式

        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        //  mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            mLocationClient.stopLocation();
            //运行时权限
            if (PermissionUtil.checkPermissions(this)) {
                mLocationClient.startLocation();
            } else {
                //未授予权限，动态申请
                PermissionUtil.initPermissions(this, REQ_CODE_INIT);
            }
        }

    }

    /**
     * 设置自定义定位蓝点
     */
    private void setLocationStyle() {
        // 自定义系统定位蓝点
        mLocationStyle = new MyLocationStyle();
        mLocationStyle.strokeColor(Color.argb(0,0,0,0));
        mLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明
        // 自定义定位蓝点图标
//        mLocationStyle.myLocationIcon(BitmapDescriptorFactory.
//                fromResource(R.drawable.gps_point));
////        mLocationStyle.strokeWidth(0);
//        // 将自定义的 mLocationStyle 对象添加到地图上
        //定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
//        aMap.setMyLocationStyle(mLocationStyle);
        aMap.setMyLocationStyle(mLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_INIT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationClient.startLocation();
        }
    }

    //声明AMapLocationClient类对象
    //声明定位回调监听器

    public final AMapLocationListener mLocationListener = new AMapLocationListener() {
        //定位回调
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null == location) {
                return;
            }
            //获取经纬度
            double lng = location.getLongitude();
            double lat = location.getLatitude();
            LogUtil.d(TAG, "onLocationChanged： lng" + lng + ",lat=" + lat);

            //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
            LatLng latLng = new LatLng(lat, lng);
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 0, 0));
            //定位蓝点
            /*if (null == mLocationMarker) {
                mLocationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point))
                        .anchor(0.0f, 0.0f));


            }*/
            //首次定位,选择移动到地图中心点并修改级别到15级
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            aMap.animateCamera(cameraUpdate, new AMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });

//            aMap.setMyLocationEnabled(false);//开启小蓝点，默认会重复定位

            //避免重复定位
//            mLocationClient.stopLocation();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onGPSClick() {
        mLocationClient.startLocation();
    }

    @Override
    public void onNearbySearchClick() {
        Toast.makeText(this, "点击附近搜索", Toast.LENGTH_SHORT).show();
    }
}
