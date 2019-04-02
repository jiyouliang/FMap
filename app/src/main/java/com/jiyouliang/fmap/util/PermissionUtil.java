package com.jiyouliang.fmap.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 运行时权限
 */
public class PermissionUtil {

    /**
     * 判断是否授予权限
     *
     * @param context
     * @param permission 权限 @see android.Manifest.permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断多个权限是否授予
     * @param context
     * @param permissions
     * @return
     */
    public static boolean checkPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkFineLocationPermission(Context context) {
        return checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * 存储权限
     *
     * @param context
     * @return
     */
    public static boolean checkWriteExtrenalStoragePermission(Context context) {
        return checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 申请运行时权限
     *
     * @param activity
     * @param permissions
     * @param reqCode
     */
    public static void requestPermission(Activity activity, String[] permissions, int reqCode) {
        ActivityCompat.requestPermissions(activity, permissions, reqCode);
    }

    /**
     * GPS定位
     */
    public static void requestAccessFineLocation(Activity activity, int reqCode) {
        requestPermission(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
    }

    /**
     * 网络定位
     */
    public static void requestAccessCoarsELocation(Activity activity, int reqCode) {
        requestPermission(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, reqCode);
    }

    /**
     * 网络定位
     */
    public static void requestWriteExtrenalStorage(Activity activity, int reqCode) {
        requestPermission(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqCode);
    }
}
