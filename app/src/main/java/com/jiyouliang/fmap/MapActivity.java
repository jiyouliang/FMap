package com.jiyouliang.fmap;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.AMapGestureListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.jiyouliang.fmap.harware.SensorEventHelper;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.DeviceUtils;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.PermissionUtil;
import com.jiyouliang.fmap.view.FrequentView;
import com.jiyouliang.fmap.view.GPSView;
import com.jiyouliang.fmap.view.NearbySearchView;
import com.jiyouliang.fmap.view.RouteView;
import com.jiyouliang.fmap.view.TrafficView;

public class MapActivity extends BaseActivity implements GPSView.OnGPSViewClickListener, NearbySearchView.OnNearbySearchViewClickListener, AMapGestureListener, AMapLocationListener, LocationSource, TrafficView.OnTrafficChangeListener, View.OnClickListener {
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
    private NearbySearchView mNearbySearcyView;
    private static boolean mFirstLocation = true;//第一次定位
    private int mCurrentGpsState = STATE_UNLOCKED;//当前定位状态
    private static final int STATE_UNLOCKED = 0;//未定位状态，默认状态
    private static final int STATE_LOCKED = 1;//定位状态
    private static final int STATE_ROTATE = 2;//根据地图方向旋转状态
    private int mZoomLevel = 16;//地图缩放级别，最大缩放级别为20
    private LatLng mLatLng;//当前定位经纬度
    private static long mAnimDuartion = 500L;//地图动效时长
    private int mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;//地图状态类型
    private SensorEventHelper mSensorHelper;
    private Marker mLocMarker;//自定义小蓝点
    private Circle mCircle;
    private static final int STROKE_COLOR = Color.argb(240, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private OnLocationChangedListener mLocationListener;
    private float mAccuracy;
    private boolean mMoveToCenter = true;//是否可以移动地图到定位点
    private TrafficView mTrafficView;
    private View mBottomSheet;
    private BottomSheetBehavior<View> mBehavior;
    private int mMaxPeekHeight;//最大高的
    private int mMinPeekHeight;//最小高度
    private View mPoiColseView;
    private RouteView mRouteView;
    private FrequentView mFrequentView;
    private View mPoiDetailTaxi;
    private int mPadding;
    //poi detail动画时长
    private static final int DURATION = 100;
    private View mGspContainer;
    private float mTransY;
    private float mOriginalGspY;
    private int moveY;
    private int[] mBottomSheetLoc = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView(savedInstanceState);
        setListener();

    }

    private void initView(Bundle savedInstanceState) {
        mGpsView = (GPSView) findViewById(R.id.gps_view);
        mRouteView = (RouteView) findViewById(R.id.route_view);
        mFrequentView = (FrequentView) findViewById(R.id.fv);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //交通流量状态控件
        mTrafficView = (TrafficView) findViewById(R.id.tv);
        aMap = mMapView.getMap();
        //显示实时交通
        aMap.setTrafficEnabled(true);

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        mGpsView.setGpsState(mCurrentGpsState);
        mNearbySearcyView = (NearbySearchView) findViewById(R.id.nearby_view);
        //底部弹出BottomSheet
        mBottomSheet = findViewById(R.id.poi_detail_bottom);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheet.setVisibility(View.GONE);
        mPoiColseView = findViewById(R.id.iv_close);
        //底部：查看详情、打车、路线
        mPoiDetailTaxi = findViewById(R.id.poi_detail_taxi);
        mPoiDetailTaxi.setVisibility(View.GONE);
        mGspContainer = findViewById(R.id.gps_view_container);
        setBottomSheet();
        setUpMap();

        mPadding = getResources().getDimensionPixelSize(R.dimen.padding_size);
    }

    /**
     * 设置底部POI详细BottomSheet
     */
    private void setBottomSheet() {
        mMinPeekHeight = mBehavior.getPeekHeight();
        //虚拟键盘高度
        int navigationHeight = DeviceUtils.getNavigationBarHeight(this);
        //加上虚拟键盘高度，避免被遮挡
//        mBehavior.setPeekHeight(mMinPeekHeight + navigationHeight);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        //屏幕高度3/5
        int height = point.y * 3 / 5;
        mMaxPeekHeight = height;
        //设置bottomsheet高度为屏幕 3/5
        ViewGroup.LayoutParams params = mBottomSheet.getLayoutParams();
        params.height = height;
    }

    /**
     * 事件处理
     */
    private void setListener() {
        mGpsView.setOnGPSViewClickListener(this);
        mNearbySearcyView.setOnNearbySearchViewClickListener(this);
        //地图手势事件
        aMap.setAMapGestureListener(this);
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        if (mTrafficView != null) {
            mTrafficView.setOnTrafficChangeListener(this);
        }
        if (null != mPoiColseView) {
            mPoiColseView.setOnClickListener(this);
        }
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            private boolean dragging;

            //BottomSheet状态改变回调
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        log("STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        dragging = true;
                        //拖拽
                        log("STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //结束：释放
                        log("STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        log("STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        log("STATE_HIDDEN");
                        if(dragging){
                            hidePoiDetail();
                            resetGpsButtonPosition();
                            dragging = false;
                        }

                        break;
                }
            }

            //BottomSheet滑动回调
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setUpMap() {
        aMap.setLocationSource(this);//设置定位监听
        //隐藏缩放控件
        aMap.getUiSettings().setZoomControlsEnabled(false);
        //设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        setLocationStyle();
    }

    @Override
    public void onLocationChanged(final AMapLocation location) {


        if (null == mLocationListener || null == location || location.getErrorCode() != 0) {
            if (location != null) {
                LogUtil.d(TAG, "定位失败：errorCode=" + location.getErrorCode() + ",errorMsg=" + location.getErrorInfo());
            }
            return;
        }
        //获取经纬度
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        LogUtil.d(TAG, "定位成功，onLocationChanged： lng" + lng + ",lat=" + lat + ",mLocMarker=" + mLocMarker);

        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        mLatLng = new LatLng(lat, lng);

        //首次定位,选择移动到地图中心点并修改级别到15级
        //首次定位成功才修改地图中心点，并移动
        mAccuracy = location.getAccuracy();
        LogUtil.d(TAG, "accuracy=" + mAccuracy);
        LogUtil.d(TAG, "mFirstLocation=" + mFirstLocation);
        if (mFirstLocation) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoomLevel), new AMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mCurrentGpsState = STATE_UNLOCKED;
                    mGpsView.setGpsState(mCurrentGpsState);
                    mMapType = MyLocationStyle.LOCATION_TYPE_LOCATE;
                    addCircle(mLatLng, mAccuracy);//添加定位精度圆
                    addMarker(mLatLng);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    mFirstLocation = false;
                }

                @Override
                public void onCancel() {

                }
            });
        } else {
            mCircle.setCenter(mLatLng);
            mCircle.setRadius(mAccuracy);
            mLocMarker.setPosition(mLatLng);
            if (mMoveToCenter) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoomLevel));
            }
        }
    }

    /**
     * 激活定位
     *
     * @param listener
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mLocationListener = listener;
        LogUtil.d(TAG, "activate: mLocationListener = " + mLocationListener + "");
        //设置定位回调监听
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationOption.setInterval(2000);//定位时间间隔，默认2000ms
            mLocationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            mLocationOption.setLocationCacheEnable(true);//开启定位缓存
            mLocationClient.setLocationOption(mLocationOption);
//            mLocationClient.startLocation();
            if (null != mLocationClient) {
                mLocationClient.setLocationOption(mLocationOption);
                //运行时权限
                if (PermissionUtil.checkPermissions(this)) {
                    mLocationClient.startLocation();
                } else {
                    //未授予权限，动态申请
                    PermissionUtil.initPermissions(this, REQ_CODE_INIT);
                }
            }
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mLocationListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocMarker = null;
        mLocationClient = null;
    }

    /**
     * 设置地图类型
     */
    private void setLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        locationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明
        //定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
        aMap.setMyLocationStyle(locationStyle.myLocationType(mMapType));
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


    /**
     * 地图手势事件回调：单指双击
     *
     * @param v
     * @param v1
     */
    @Override
    public void onDoubleTap(float v, float v1) {
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mCurrentGpsState = STATE_UNLOCKED;
        mGpsView.setGpsState(mCurrentGpsState);
        setLocationStyle();
        resetLocationMarker();
        mMoveToCenter = false;
    }

    /**
     * 地体手势事件回调：单指单击
     *
     * @param v
     * @param v1
     */
    @Override
    public void onSingleTap(float v, float v1) {

    }

    /**
     * 地体手势事件回调：单指惯性滑动
     *
     * @param v
     * @param v1
     */
    @Override
    public void onFling(float v, float v1) {
        LogUtil.d(TAG, "onFling,x=" + v + ",y=" + v1);
    }

    /**
     * 地体手势事件回调：单指滑动
     *
     * @param v
     * @param v1
     */
    @Override
    public void onScroll(float v, float v1) {
        LogUtil.d(TAG, "onScroll,x=" + v + ",y=" + v1);
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        setLocationStyle();
//        mLocationClient.stopLocation();
//        mLocationOption.setOnceLocation(true);//单次定位
        mCurrentGpsState = STATE_UNLOCKED;
        //当前没有正在定位才能修改状态
        if (!mFirstLocation) {
            mGpsView.setGpsState(mCurrentGpsState);
        }
        mMoveToCenter = false;
    }

    /**
     * 地体手势事件回调：长按
     *
     * @param v
     * @param v1
     */
    @Override
    public void onLongPress(float v, float v1) {

    }

    /**
     * 地体手势事件回调：单指按下
     *
     * @param v
     * @param v1
     */
    @Override
    public void onDown(float v, float v1) {
        LogUtil.d(TAG, "onDown");
    }

    /**
     * 地体手势事件回调：单指抬起
     *
     * @param v
     * @param v1
     */
    @Override
    public void onUp(float v, float v1) {
        LogUtil.d(TAG, "onUp");

    }

    /**
     * 地体手势事件回调：地图稳定下来会回到此接口
     */
    @Override
    public void onMapStable() {

    }


    @Override
    public void onGPSClick() {
        CameraUpdate cameraUpdate = null;
        mMoveToCenter = true;
        //修改定位图标状态
        switch (mCurrentGpsState) {
            case STATE_LOCKED:
                mZoomLevel = 18;
                mAnimDuartion = 500;
                mCurrentGpsState = STATE_ROTATE;
//                setLocationStyle(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
                mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, mZoomLevel, 30, 0));
                break;
            case STATE_UNLOCKED:
            case STATE_ROTATE:
                mZoomLevel = 16;
                mAnimDuartion = 500;
                mCurrentGpsState = STATE_LOCKED;
                mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, mZoomLevel, 0, 0));
                break;
        }
        //显示底部POI详情
        if (mBottomSheet.getVisibility() == View.GONE || mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showPoiDetail();
            moveGspButtonAbove();
        }

        aMap.setMyLocationEnabled(true);
        LogUtil.d(TAG, "onGPSClick:mCurrentGpsState=" + mCurrentGpsState + ",mMapType=" + mMapType);
        //改变定位图标状态
        mGpsView.setGpsState(mCurrentGpsState);
        //执行地图动效
        aMap.animateCamera(cameraUpdate, mAnimDuartion, new AMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {

            }
        });
        setLocationStyle();
        resetLocationMarker();
    }

    /**
     * 根据当前地图状态重置定位蓝点
     */
    private void resetLocationMarker() {
        aMap.clear();
        mLocMarker = null;
        if (mCurrentGpsState == STATE_ROTATE) {
            //ROTATE模式不需要方向传感器
            //mSensorHelper.unRegisterSensorListener();
            addRotateMarker(mLatLng);
        } else {
            //mSensorHelper.registerSensorListener();
            addMarker(mLatLng);
            if (null != mLocMarker) {
                mSensorHelper.setCurrentMarker(mLocMarker);
            }
        }

        addCircle(mLatLng, mAccuracy);
    }

    @Override
    public void onNearbySearchClick() {
        Toast.makeText(this, "点击附近搜索", Toast.LENGTH_SHORT).show();
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        if (null != mLocationClient) {
            LogUtil.d(TAG, "stopLocation");
            mLocationClient.stopLocation();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        LogUtil.d(TAG, "onResume");
        mMapView.onResume();
        if (null == mSensorHelper) {
            aMap.clear();
            mSensorHelper = new SensorEventHelper(this);
            //重新注册
            if (mSensorHelper != null) {
                mSensorHelper.registerSensorListener();
                setUpMap();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mFirstLocation = true;
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        deactivate();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        if (mLocMarker != null) {
            mLocMarker.destroy();
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        /*if (mLocMarker != null) {
            return;
        }*/
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mLocMarker = aMap.addMarker(markerOptions);
    }

    private void addRotateMarker(LatLng latlng) {
       /* if (mLocMarker != null) {
            return;
        }*/
        MarkerOptions markerOptions = new MarkerOptions();
        //3D效果
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_3d)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mLocMarker = aMap.addMarker(markerOptions);
    }


    @Override
    public void onTrafficChanged(boolean selected) {
        aMap.setTrafficEnabled(selected);
    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }

    @Override
    public void onClick(View v) {
        if (v == mPoiColseView) {
            resetGpsButtonPosition();
            hidePoiDetail();
        }
    }

    /**
     * 隐藏底部POI详情
     */
    private void hidePoiDetail() {
        mBottomSheet.setVisibility(View.GONE);
        //底部：打车、路线...
        mPoiDetailTaxi.setVisibility(View.GONE);
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //gsp控件回退到原来位置、并显示底部其他控件
        mRouteView.setVisibility(View.VISIBLE);
        mFrequentView.setVisibility(View.VISIBLE);
        mNearbySearcyView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示底部POI详情
     */
    private void showPoiDetail() {
        mGpsView.setVisibility(View.VISIBLE);
        mBottomSheet.setVisibility(View.VISIBLE);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mRouteView.setVisibility(View.GONE);
        mFrequentView.setVisibility(View.GONE);
        mNearbySearcyView.setVisibility(View.GONE);
        //底部：打车、路线...
        mPoiDetailTaxi.setVisibility(View.VISIBLE);

        //int poiTaxiHeight = mPoiDetailTaxi.getMeasuredHeight(); //为0
        int poiTaxiHeight = getResources().getDimensionPixelSize(R.dimen.setting_item_large_height);

        mBehavior.setHideable(true);
        mBehavior.setPeekHeight(mMinPeekHeight + poiTaxiHeight);
    }

    /**
     * 将GpsButton移动到poi detail上面
     */
    private void moveGspButtonAbove() {

        mBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(mGpsView.isAbovePoiDetail()){
                    //已经在上面，不需要重复调用
                    return;
                }
                LogUtil.d(TAG, "moveGspButtonAbove");
                if (moveY == 0) {
                    //计算Y轴方向移动距离
                    moveY = mGspContainer.getTop() - mBottomSheet.getTop() + mGspContainer.getMeasuredHeight() + mPadding;
                    mBottomSheet.getLocationInWindow(mBottomSheetLoc);
                }
                if (moveY > 0) {
                    mGspContainer.setTranslationY(-moveY);
                    mGpsView.setAbovePoiDetail(true);
                }
            }
        });


    }

    /**
     * 将GpsButton移动到原来位置
     */
    private void resetGpsButtonPosition() {

        mBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if(!mGpsView.isAbovePoiDetail()){
                    //已经在下面，不需要重复调用
                    return;
                }
                //回到原来位置
                mGspContainer.setTranslationY(0);
                mGpsView.setAbovePoiDetail(false);
                LogUtil.d(TAG, "resetGpsButtonPosition");
            }
        });

    }


}
