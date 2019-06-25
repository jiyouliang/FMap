package com.jiyouliang.fmap.base.view;

/**
 * MapActivity 接口：将常用功能抽取出来，便于后面管理
 */
public interface MapViewInterface {

    /**
     * 显示Poi detail
     */
    void showPoiDetail();

    /**
     * 隐藏poi detail
     */
    void hidePoiDetail();

    /**
     * poi detail显示：显示地图
     */
    void showBackToMapState();

    /**
     * poi detail显示:查看详情
     */
    void showPoiDetailState();

    /**
     * BottomSheet展开后，缩小地图
     */
    void minMapView();

    /**
     * BottomSheet折叠或者隐藏后,放大地图,全屏显示
     */
    void maxMapView();
}
