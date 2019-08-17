package com.jiyouliang.fmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
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
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.LatLonSharePoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.share.ShareSearch;
import com.jiyouliang.fmap.ui.navi.WalkRouteNaviActivity;
import com.jiyouliang.fmap.ui.user.UserActivity;
import com.jiyouliang.fmap.util.Constants;
import com.jiyouliang.fmap.util.InputMethodUtils;
import com.jiyouliang.fmap.util.MyAMapUtils;
import com.jiyouliang.fmap.util.WechatApi;
import com.jiyouliang.fmap.util.WechatUtil;
import com.jiyouliang.fmap.view.base.MapViewInterface;
import com.jiyouliang.fmap.harware.SensorEventHelper;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.DeviceUtils;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.view.map.FrequentView;
import com.jiyouliang.fmap.view.map.GPSView;
import com.jiyouliang.fmap.view.map.MapHeaderView;
import com.jiyouliang.fmap.view.map.NearbySearchView;
import com.jiyouliang.fmap.view.map.PoiDetailBottomView;
import com.jiyouliang.fmap.view.map.RouteView;
import com.jiyouliang.fmap.view.map.SupendPartitionView;
import com.jiyouliang.fmap.view.map.TrafficView;
import com.jiyouliang.fmap.view.widget.OnItemClickListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends BaseActivity implements GPSView.OnGPSViewClickListener, NearbySearchView.OnNearbySearchViewClickListener, AMapGestureListener, AMapLocationListener, LocationSource, TrafficView.OnTrafficChangeListener, View.OnClickListener, MapViewInterface, PoiDetailBottomView.OnPoiDetailBottomClickListener, ShareSearch.OnShareSearchListener, AMap.OnPOIClickListener, TextWatcher, Inputtips.InputtipsListener, MapHeaderView.OnMapHeaderViewClickListener, OnItemClickListener {
    private static final String TAG = "MapActivity";
    /**
     * 首次进入申请定位、sd卡权限
     */
    private static final int REQ_CODE_INIT = 0;
    private static final int REQ_CODE_FINE_LOCATION = 1;
    private static final int REQ_CODE_STORAGE = 2;
    private TextureMapView mMapView;
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
    private LatLng mClickPoiLatLng;//当前点击的poi经纬度
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
    private PoiDetailBottomView mPoiDetailTaxi;
    private int mPadding;
    //poi detail动画时长
    private static final int DURATION = 100;
    private View mGspContainer;
    private float mTransY;
    private float mOriginalGspY;
    private int moveY;
    private int[] mBottomSheetLoc = new int[2];
    private String mPoiName;
    private TextView mTvLocation;
    private MapHeaderView mMapHeaderView;
    private View mFeedbackContainer;
    private SupendPartitionView mSupendPartitionView;
    private int mScreenHeight;
    private int mScreenWidth;
    private boolean isMinMap; //缩小显示地图
    private boolean onScrolling;//正在滑动地图
    private MyLocationStyle mLocationStyle;
    private boolean slideDown;//向下滑动
    private LinearLayout mShareContainer;
    private IWXAPI api;
    private BroadcastReceiver mWechatBroadcast;
    private ShareSearch mShareSearch;
    private AMapLocation mAmapLocation;
    // 分享url到微信图片大小
    private static final int THUMB_SIZE = 150;
    private ImageButton mImgBtnBack;
    private TextView mTvLocTitle;
    // 当前是否正在处理POI点击
    private boolean isPoiClick;
    private TextView mTvRoute;
    private LinearLayout mLLSearchContainer;
    // 搜索结果存储
    private List<Tip> mSearchData = new ArrayList<>();

    /**
     * 当前地图模式
     */
    private MapMode mMapMode = MapMode.NORMAL;
    private RecyclerView mRecycleViewSearch;
    private ImageView mIvLeftSearch;
    private EditText mEtSearchTip;
    private SearchAdapter mSearchAdapter;
    private String mCity;
    private ProgressBar mSearchProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView(savedInstanceState);
        initData();
        setListener();

    }

    private void initView(Bundle savedInstanceState) {
        mGpsView = (GPSView) findViewById(R.id.gps_view);
        mRouteView = (RouteView) findViewById(R.id.route_view);
        mFrequentView = (FrequentView) findViewById(R.id.fv);
        //获取地图控件引用
        mMapView = (TextureMapView) findViewById(R.id.map);
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
        mPoiDetailTaxi = (PoiDetailBottomView) findViewById(R.id.poi_detail_taxi);
        mPoiDetailTaxi.setVisibility(View.GONE);
        mGspContainer = findViewById(R.id.gps_view_container);

        mTvLocTitle = (TextView) findViewById(R.id.tv_title);
        mTvLocation = (TextView) findViewById(R.id.tv_my_loc);
        mMapHeaderView = (MapHeaderView) findViewById(R.id.mhv);
        mFeedbackContainer = findViewById(R.id.feedback_container);
        mFeedbackContainer.setVisibility(View.GONE);
        mSupendPartitionView = (SupendPartitionView) findViewById(R.id.spv);
        // 分享组件
        mShareContainer = (LinearLayout)findViewById(R.id.rl_right);
        mImgBtnBack= (ImageButton)findViewById(R.id.ib_back);
        // 路线
        mTvRoute = (TextView)findViewById(R.id.tv_route);
        // 搜索区域
        mLLSearchContainer = (LinearLayout)findViewById(R.id.ll_search_container);
        mRecycleViewSearch = (RecyclerView)findViewById(R.id.rv_search);
        mIvLeftSearch = (ImageView)findViewById(R.id.iv_search_left);
        mEtSearchTip = (EditText)findViewById(R.id.et_search_tip);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleViewSearch.setLayoutManager(layoutManager);
        mSearchProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        setBottomSheet();
        setUpMap();

        mPadding = getResources().getDimensionPixelSize(R.dimen.padding_size);
        /*int statusBarHeight = DeviceUtils.getStatusBarHeight(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.WHITE);
        log(String.format("statusBarHeight=%s", statusBarHeight));*/
//        SystemUIModes.setTranslucentStatus(this, true);
    }

    private void initData() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);
        // 高德地图分享
        mShareSearch = new ShareSearch(this);
        // 搜索结果RecyclerView
        mSearchAdapter = new SearchAdapter(mSearchData);
        mRecycleViewSearch.setAdapter(mSearchAdapter);

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
        if (null == wm) {
            LogUtil.e(TAG, "获取WindowManager失败:" + wm);
            return;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        //屏幕高度3/5
        mScreenHeight = point.y;
        mScreenWidth = point.x;
        //设置bottomsheet高度为屏幕 3/5
        int height = mScreenHeight * 3 / 5;
        mMaxPeekHeight = height;
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

            private float lastSlide;//上次slideOffset
            private float currSlide;//当前slideOffset


            //BottomSheet状态改变回调
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    //展开
                    case BottomSheetBehavior.STATE_EXPANDED:
                        log("STATE_EXPANDED");
                        smoothSlideUpMap();
                        break;
                    //折叠
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        log("STATE_COLLAPSED");
                        /*if (slideDown) {
                            maxMapView();
                            slideDown = false;
                        }*/
                        onPoiDetailCollapsed();
                        slideDown = false;

                        break;
                    //隐藏
                    case BottomSheetBehavior.STATE_HIDDEN:
                        log("STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //拖拽
                        log("STATE_DRAGGING");

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //结束：释放
                        log("STATE_SETTLING");

                        break;

                }
            }

            /**
             * BottomSheet滑动回调
             */

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                currSlide = slideOffset;
                log("onSlide:slideOffset=" + slideOffset + ",getBottom=" + bottomSheet.getBottom() + ",currSlide=" + currSlide + ",lastSlide=" + lastSlide);
                if (slideOffset > 0) {
                    // > 0:向上拖动
                    mPoiColseView.setVisibility(View.GONE);
                    showBackToMapState();
                    if (slideOffset < 1) {

                    }
                    mMoveToCenter = false;
                    if (currSlide - lastSlide > 0) {
                        log(">>>>>向上滑动");
                        slideDown = false;
                        onPoiDetailExpanded();
                        //smoothSlideUpMap(slideOffset);
                    } else if (currSlide - lastSlide < 0) {
                        log("<<<<<向下滑动");
                        //smoothSlideDownMap(slideOffset);
                        if (!slideDown) {
                            smoothSlideDownMap();
                        }
                    }
                } else if (slideOffset == 0) {
                    //滑动到COLLAPSED状态
                    mPoiColseView.setVisibility(View.VISIBLE);
                    showPoiDetailState();
                } else if (slideOffset < 0) {
                    //从COLLAPSED向HIDDEN状态滑动，此处禁止BottomSheet隐藏
                    //setHideable(false)禁止Behavior执行：可以实现禁止向下滑动消失
                    mBehavior.setHideable(false);
                }

                lastSlide = currSlide;

            }
        });

        mPoiDetailTaxi.setOnPoiDetailBottomClickListener(this);

        //头部View点击处理
        mMapHeaderView.setOnMapHeaderViewClickListener(this);

        mShareContainer.setOnClickListener(this);
        // 注册高德地图分享回调
        mShareSearch.setOnShareSearchListener(this);
        // 头部返回
        mImgBtnBack.setOnClickListener(this);
        // 地图poi点击
        aMap.setOnPOIClickListener(this);
        // 点击路径进入导航页面
        mTvRoute.setOnClickListener(this);
        // 搜索布局左侧返回箭头图标
        mIvLeftSearch.setOnClickListener(this);
        // 搜索输入框
        mEtSearchTip.addTextChangedListener(this);
        mSearchAdapter.setOnItemClickListener(this);
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
        this.mAmapLocation = location;
        if (onScrolling) {
            LogUtil.e(TAG, "MapView is Scrolling by user,can not operate...");
            return;
        }
        //获取经纬度
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        // 当前poiname和上次不相等才更新显示
        if(location.getPoiName() != null && !location.getPoiName().equals(mPoiName)){
            if(!isPoiClick){
                // 点击poi时,定位位置和点击位置不一定一样
                mPoiName = location.getPoiName();
                showPoiNameText(String.format("在%s附近", mPoiName));
            }
        }
        //LogUtil.d(TAG, "定位成功，onLocationChanged： lng" + lng + ",lat=" + lat + ",mLocMarker=" + mLocMarker + ",poiName=" + mPoiName+",getDescription="+location.getDescription()+", address="+location.getAddress()+",getLocationDetail"+location.getLocationDetail()+",street="+location.getStreet());

        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        mLatLng = new LatLng(lat, lng);
        if(!location.getCity().equals(mCity)){
            mCity = location.getCity();
        }

        //首次定位,选择移动到地图中心点并修改级别到15级
        //首次定位成功才修改地图中心点，并移动
        mAccuracy = location.getAccuracy();
        LogUtil.d(TAG, "accuracy=" + mAccuracy + ",mFirstLocation=" + mFirstLocation);
        if (mFirstLocation) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoomLevel), new AMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mCurrentGpsState = STATE_LOCKED;
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
            //BottomSheet顶上显示,地图缩小显示
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
                mLocationClient.startLocation();
                //运行时权限
                /*if (PermissionUtil.checkPermissions(this)) {
                    mLocationClient.startLocation();
                } else {
                    //未授予权限，动态申请
                    PermissionUtil.initPermissions(this, REQ_CODE_INIT);
                }*/
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
        if (null == mLocationStyle) {
            mLocationStyle = new MyLocationStyle();
            mLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
            mLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明
        }
        //定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
        aMap.setMyLocationStyle(mLocationStyle.myLocationType(mMapType));
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
        /*if (requestCode == REQ_CODE_INIT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationClient.startLocation();
        }*/
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
        //避免重复调用闪屏，当手指up才重置为false
        if (!onScrolling) {
            onScrolling = true;
            LogUtil.d(TAG, "onScroll,x=" + v + ",y=" + v1);
            //旋转不移动到中心点
            mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
            mCurrentGpsState = STATE_UNLOCKED;
            //当前没有正在定位才能修改状态
            if (!mFirstLocation) {
                mGpsView.setGpsState(mCurrentGpsState);
            }
            mMoveToCenter = false;
            setLocationStyle();
            resetLocationMarker();
        }
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
        onScrolling = false;
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
        isPoiClick = false;
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
            showPoiDetail("我的位置", String.format("在%s附近", mPoiName));
            moveGspButtonAbove();
        }else{
            mTvLocTitle.setText("我的位置");
            mTvLocation.setText(String.format("在%s附近", mPoiName));
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
        if (mGpsView.getGpsState() == GPSView.STATE_ROTATE) {
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
        //registerWechatBroadcast();
    }

    /**
     * 注册微信广播
     */
    /*private void registerWechatBroadcast() {
        //建议动态监听微信启动广播进行注册到微信
        IntentFilter filter = new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP);
        // 将该app注册到微信
        mWechatBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 将该app注册到微信
                api.registerApp(Constants.APP_ID);
            }
        };
        registerReceiver(mWechatBroadcast, filter);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterWechatBroadcast();
    }

    /**
     * 反注册微信广播
     */
  /*  private void unregisterWechatBroadcast() {
        if(mWechatBroadcast != null){
            unregisterReceiver(mWechatBroadcast);
        }
        if(mWechatBroadcast != null){
            mWechatBroadcast = null;
        }

    }*/

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

    /**
     * 跳转用户登录
     */
    private void userLogin() {
        startActivity(new Intent(MapActivity.this, UserActivity.class));
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
        if(v == null){
            return;
        }
        // 点击关闭POI detail
        if (v == mPoiColseView) {
            mBehavior.setHideable(true);
            resetGpsButtonPosition();
            hidePoiDetail();
            return;
        }

        // 分享
        if(v == mShareContainer){
            if(mAmapLocation != null && mLatLng != null){
                if(isPoiClick){
                    // 分享点击poi的位置
                    shareLocation(mPoiName, mClickPoiLatLng.latitude, mClickPoiLatLng.longitude);
                }else{
                    // 分享当前定位位置
                    shareLocation(mPoiName, mLatLng.latitude, mLatLng.longitude);
                }
            }
            return;
        }

        // 头部返回ImageButton
        if(v == mImgBtnBack){
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        // 路线,进入导航页面
        if(v == mTvRoute){
            if(mLatLng == null){
                showToast(getString(R.string.location_failed_hold_on));
                return;
            }
            if(mClickPoiLatLng == null){
                showToast(getString(R.string.please_select_dest_loc));
                return;
            }
            Intent intent = new Intent(this, WalkRouteNaviActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("startLatLng", mLatLng);
            bundle.putParcelable("stopLatLng", mClickPoiLatLng);

            intent.putExtra("params", bundle);
            startActivity(intent);
            return;
        }

        // 点击搜索左侧返回箭头
        if(v == mIvLeftSearch){
            hideSearchTipView();
            showMapView();
            return;
        }

    }

    /**
     * 分享网页url到微信
     */
    private void shareToWechat(String url, String title) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        // msg.description = description;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WechatUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = WechatApi.buildTransaction("webpage");
        req.message = msg;
        req.scene = WechatApi.mTargetScene;
        api.sendReq(req);
    }

    /**
     * 隐藏底部POI详情
     */
    @Override
    public void hidePoiDetail() {
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
     * @param locTitle 定位标题,比如当前所在位置名称
     * @param locInfo 定位信息,比如当前在什么附近/距离当前位置多少米
     */
    @Override
    public void showPoiDetail(String locTitle, String locInfo) {
        mGpsView.setVisibility(View.VISIBLE);
        mBottomSheet.setVisibility(View.VISIBLE);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mRouteView.setVisibility(View.GONE);
        mFrequentView.setVisibility(View.GONE);
        mNearbySearcyView.setVisibility(View.GONE);
        //底部：打车、路线...
        mPoiDetailTaxi.setVisibility(View.VISIBLE);
        //我的位置
        mTvLocTitle.setText(locTitle);
        mTvLocation.setText(locInfo);
//        showPoiNameText();

        //int poiTaxiHeight = mPoiDetailTaxi.getMeasuredHeight(); //为0
        int poiTaxiHeight = getResources().getDimensionPixelSize(R.dimen.setting_item_large_height);

        mBehavior.setHideable(true);
        mBehavior.setPeekHeight(mMinPeekHeight + poiTaxiHeight);

    }

    /**
     * 显示当前所在poi点信息
     */
    private void showPoiNameText(String locInfo) {
        mTvLocation.setText(locInfo);
    }

    /**
     * 将GpsButton移动到poi detail上面
     */
    private void moveGspButtonAbove() {

        mBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mGpsView.isAbovePoiDetail()) {
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
                if (!mGpsView.isAbovePoiDetail()) {
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


    @Override
    public void showBackToMapState() {
        //显示:查看详情
        mPoiDetailTaxi.setPoiDetailState(PoiDetailBottomView.STATE_MAP);
        //BottomSheet展开:这里不建议修改BottomSheet状态，backToMap方法可能在BottomSheet状态回调中调用，避免互相调用死循环
    }

    @Override
    public void showPoiDetailState() {
        mPoiDetailTaxi.setPoiDetailState(PoiDetailBottomView.STATE_DETAIL);
    }

    @Override
    public void minMapView() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMapView.getLayoutParams();
        //避免重复设置LayoutParams
        if (lp.bottomMargin == mMaxPeekHeight) {
            return;
        }
        lp.bottomMargin = mMaxPeekHeight;
        mMapView.setLayoutParams(lp);


    }

    @Override
    public void maxMapView() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMapView.getLayoutParams();
        //避免重复设置LayoutParams
        if (lp.bottomMargin == 0) {
            return;
        }
        lp.bottomMargin = 0;
        mMapView.setLayoutParams(lp);
    }

    @Override
    public void onDetailClick() {
        int state = mPoiDetailTaxi.getPoiDetailState();
        switch (state) {
            case PoiDetailBottomView.STATE_DETAIL:
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //minMapView();
                break;
            case PoiDetailBottomView.STATE_MAP:
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


                break;
        }
    }

    @Override
    public void onPoiDetailCollapsed() {
        //BottomSheet折叠：显示头部搜索、隐藏反馈、显示右边侧边栏
        mPoiColseView.setVisibility(View.VISIBLE);
        mMapHeaderView.setVisibility(View.VISIBLE);
        mFeedbackContainer.setVisibility(View.GONE);
        mSupendPartitionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPoiDetailExpanded() {
        //BottomSheet展开：隐藏头部搜索、显示反馈、隐藏右边侧边栏
        mMapHeaderView.setVisibility(View.GONE);
        mFeedbackContainer.setVisibility(View.VISIBLE);
        mSupendPartitionView.setVisibility(View.GONE);
    }

    /**
     * 地图平滑上移，重置新的marker
     */
    private void slideUpMarker() {
        aMap.clear();
        mLocMarker = null;
        addRotateMarker(mLatLng);
        if (null != mLocMarker) {
            mSensorHelper.setCurrentMarker(mLocMarker);
        }
        addCircle(mLatLng, mAccuracy);

//        aMap.clear();
//        mLocMarker = null;
//        if (mGpsView.getGpsState() == GPSView.STATE_ROTATE) {
//            //ROTATE模式不需要方向传感器
//            //mSensorHelper.unRegisterSensorListener();
//
//        } else {
//            //mSensorHelper.registerSensorListener();
//            addMarker(mLatLng);
//            if (null != mLocMarker) {
//                mSensorHelper.setCurrentMarker(mLocMarker);
//            }
//        }
//
//        addCircle(mLatLng, mAccuracy);
    }

    @Override
    public void smoothSlideUpMap() {
        switch (mGpsView.getGpsState()) {
            case GPSView.STATE_ROTATE:
                if(!isPoiClick){
                    mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                }
                break;
            case GPSView.STATE_UNLOCKED:
            case GPSView.STATE_LOCKED:
                // 当前没D有操作poi点击
                if(!isPoiClick){
                    mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                }
                break;
        }
        setLocationStyle();
        //禁用手势操作
        aMap.getUiSettings().setAllGesturesEnabled(false);
        if(!isPoiClick){
            mMoveToCenter = true;
        }else{
            mMoveToCenter = false;
        }
        ViewGroup.LayoutParams lp = mMapView.getLayoutParams();
        lp.height = mScreenHeight * 2 / 5;
        mMapView.setLayoutParams(lp);
    }

    @Override
    public void smoothSlideDownMap() {
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mMoveToCenter = false;
        slideDown = true;
        ViewGroup.LayoutParams lp = mMapView.getLayoutParams();
        lp.height = mScreenHeight;
        mMapView.setLayoutParams(lp);
        //启用手势操作
        aMap.getUiSettings().setAllGesturesEnabled(true);
        switch (mGpsView.getGpsState()) {
            case GPSView.STATE_ROTATE:
                mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                break;
            case GPSView.STATE_UNLOCKED:
            case GPSView.STATE_LOCKED:
                mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                break;
        }
        setLocationStyle();
//        resetLocationMarker();
        mMoveToCenter = false;
    }

    /**
     * 高德地图位置转短串分享
     * @param snippet 位置名称
     * @param lat 维度
     * @param lng 经度
     */
    private void shareLocation(String snippet, double lat, double lng) {
        if(TextUtils.isEmpty(snippet)){
            return;
        }
        // addTestLocationMarker(snippet);
        LatLonSharePoint point = new LatLonSharePoint(lat,
                lng, snippet);
        // showProgressDialog();
        mShareSearch.searchLocationShareUrlAsyn(point);
    }

    /**
     * 高德地图回调
     */
    @Override
    public void onCallTaxiClick() {

    }

    @Override
    public void onRouteClick() {

    }

    @Override
    public void onPoiShareUrlSearched(String s, int i) {

    }

    /**
     * 高德地图分享位置短串回调
     * @param url 网页url
     * @param errorCode 错误码
     */
    @Override
    public void onLocationShareUrlSearched(String url, int errorCode) {
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            shareToWechat(url, mPoiName);
        } else {
            showToast(String.format("分享失败:%s", errorCode));
        }
    }

    @Override
    public void onNaviShareUrlSearched(String s, int i) {

    }

    @Override
    public void onBusRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public void onWalkRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public void onDrivingRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 处理返回键
        if(keyCode == KeyEvent.KEYCODE_BACK){
            MapMode mode = mMapMode;
            if(mode == MapMode.NORMAL){
                // BottomSheet展开,折叠BottomSheet不关闭Activity
                if(mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    return true;
                }else{
                    return super.onKeyDown(keyCode, event);
                }
            }else if(mode == MapMode.SEARCH){
                hideSearchTipView();
                showMapView();
                mMapMode = MapMode.NORMAL;

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 地图POI点击
     * @param poi
     */
    @Override
    public void onPOIClick(Poi poi) {
        LogUtil.d(TAG, "onPOIClick,poi="+poi);
        if(poi == null || poi.getCoordinate() == null || TextUtils.isEmpty(poi.getName())){
            return;
        }
        // 当前点击坐标
        mClickPoiLatLng = poi.getCoordinate();
        // 当前正在处理poi点击
        isPoiClick = true;
        addPOIMarderAndShowDetail(poi.getCoordinate(), poi.getName());

    }

    /**
     * 添加POImarker
     */
    private void addPOIMarderAndShowDetail(LatLng latLng, String poiName) {
        animMap(latLng);
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mCurrentGpsState = STATE_UNLOCKED;
        //当前没有正在定位才能修改状态
        if (!mFirstLocation) {
            mGpsView.setGpsState(mCurrentGpsState);
        }
        mMoveToCenter = false;
        // 添加marker标记
        addPOIMarker(latLng);
        showClickPoiDetail(latLng, poiName);
    }

    /**
     * 显示poi点击底部BottomSheet
     */
    private void showClickPoiDetail(LatLng latLng, String poiName) {
        mPoiName = poiName;
        mTvLocTitle.setText(poiName);
        String distanceStr = MyAMapUtils.calculateDistanceStr(mLatLng, latLng);
        if (mBottomSheet.getVisibility() == View.GONE || mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showPoiDetail(poiName, String.format("距离您%s", distanceStr));
            moveGspButtonAbove();
        }else{
            mTvLocTitle.setText(poiName);
            mTvLocation.setText(String.format("距离您%s", distanceStr));
        }
    }

    private void addPOIMarker(LatLng latLng) {
        aMap.clear();
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(latLng);
        markOptiopns.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_mark));
        aMap.addMarker(markOptiopns);
    }

    /**
     * 移动地图中心点到指定位置
     * @param latLng
     */
    private void animMap(LatLng latLng){
        if(latLng != null){
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel));
        }
    }

    /**
     * 隐藏地图图层
     */
    private void hideMapView(){
        mMapView.setVisibility(View.GONE);
        mMapHeaderView.setVisibility(View.GONE);
        mSupendPartitionView.setVisibility(View.GONE);
    }

    /**
     * 显示地图图层
     */
    private void showMapView(){
        mMapView.setVisibility(View.VISIBLE);
        mMapHeaderView.setVisibility(View.VISIBLE);
        mSupendPartitionView.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏搜索提示布局
     */
    private void hideSearchTipView(){
        InputMethodUtils.hideInput(this);
        mLLSearchContainer.setVisibility(View.GONE);
        mEtSearchTip.setVisibility(View.VISIBLE);
        mEtSearchTip.setFocusable(true);
        mEtSearchTip.setFocusableInTouchMode(true);
        mSearchData.clear();
        mSearchAdapter.notifyDataSetChanged();
        mEtSearchTip.setText("");
    }

    /**
     * 显示搜索提示布局
     */
    private void showSearchTipView(){
        mLLSearchContainer.setVisibility(View.VISIBLE);
        InputMethodUtils.showInput(this, mEtSearchTip);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * EditText输入内容后回调
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        if(s == null || TextUtils.isEmpty(s.toString())){
            mSearchProgressBar.setVisibility(View.GONE);
            return;
        }
        String content = s.toString();
        if(!TextUtils.isEmpty(content) && !TextUtils.isEmpty(mCity)){
            // 调用高德地图搜索提示api
            InputtipsQuery inputquery = new InputtipsQuery(content, mCity);
            inputquery.setCityLimit(true);
            Inputtips inputTips = new Inputtips(this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
            mSearchProgressBar.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 高德地图搜索提示回调
     * @param list
     * @param i
     */
    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        mSearchProgressBar.setVisibility(View.GONE);
        if(list == null || list.size() == 0){
            return;
        }
        mSearchData.clear();
        mSearchData.addAll(list);
        // 刷新RecycleView
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUserClick() {
        userLogin();
    }

    @Override
    public void onSearchClick() {
        // 显示搜索layout,隐藏地图图层,并设置当前地图操作模式
        showSearchTipView();
        hideMapView();
        mMapMode = MapMode.SEARCH;
    }

    @Override
    public void onVoiceClick() {

    }

    @Override
    public void onQrScanClick() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemClick(View v, int position) {
        if(mSearchData.size() > 0){

            Tip tip = mSearchData.get(position);
            if(tip == null){
                return;
            }
            hideSearchTipView();
            showMapView();
            mMoveToCenter = false;
            isPoiClick = true;
            LatLonPoint point = tip.getPoint();
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            addPOIMarderAndShowDetail(latLng, tip.getName());
            showClickPoiDetail(latLng, tip.getName());
        }
    }

    /**
     * 地图模式
     */
    private enum MapMode{
        /**
         * 普通模式:显示地图图层
         */
        NORMAL,

        /**
         * 搜索模式:显示搜索提示和搜索结果
         */
        SEARCH
    }

    /**
     * 搜索Adapter
     */
    private static class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> implements View.OnClickListener {

        private List<Tip> mData;
        private OnItemClickListener mListener;

        public SearchAdapter(List<Tip> data) {
            this.mData = data;
        }

        /**
         * 设置RecycleView条目点击
         * @param listener
         */
        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View itemView = ((LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.search_tip_recycle_item, viewGroup, false);
            itemView.setTag(position);
            itemView.setOnClickListener(this);
            return new SearchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            Tip tip = mData.get(position);
            holder.tvSearchTitle.setText(tip.getName());
            holder.tvSearchLoc.setText(tip.getAddress());
        }

        @Override
        public int getItemCount() {
            if(mData != null && mData.size() > 0){
                return mData.size();
            }
            return 0;
        }

        @Override
        public void onClick(View v) {
            if(v != null && mListener != null){
                int postion = (int) v.getTag();
                mListener.onItemClick(v, postion);
            }
        }
    }


    /**
     * 搜索ViewHolder
     */
    private static class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView tvSearchTitle;
        TextView tvSearchLoc;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchTitle = itemView.findViewById(R.id.tv_search_title);
            tvSearchLoc = itemView.findViewById(R.id.tv_search_loc);
        }
    }


}
