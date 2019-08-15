package com.jiyouliang.fmap.util;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * @author YouLiang.Ji
 */
public class MyAMapUtils {

    /**
     * 计算两点间距离,返回字符串,比如100米,1.8千米
     * @param latLng1
     * @param latLng2
     * @return
     */
    public static String calculateDistanceStr(LatLng latLng1, LatLng latLng2){
        float distance = AMapUtils.calculateLineDistance(latLng1, latLng2);
        StringBuilder builder = new StringBuilder();
        if(distance < 1000F){
            // 1000米以内不保留小数点
            builder.append(((int)distance)).append("米");
        }else if(distance >= 1000F){
            // 保留一位小数点
            DecimalFormat decimalFormat=new DecimalFormat(".0");
            builder.append(decimalFormat.format(distance / 1000F)).append("千米");
        }
        return builder.toString();
    }
}
