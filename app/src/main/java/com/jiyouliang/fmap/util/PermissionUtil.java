package com.jiyouliang.fmap.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时权限
 */
public class PermissionUtil {

    /**
     * 出生时候App需要的权限
     */
    private static final String[] INIT_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET};

    /**
     * 初始化申请权限
     *
     * @param activity
     * @param reqCode
     * @deprecated 过时方法，请使用{@link #requestPermissions(String[], Activity, int)}替代
     */
    @Deprecated
    public static void initPermissions(Activity activity, int reqCode) {
        requestPermission(activity, INIT_PERMISSIONS, reqCode);
    }

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
     *
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

    public static boolean checkPermissions(Context context) {
        for (String permission : INIT_PERMISSIONS) {
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
     * @deprecated 该方法已过时，请使用{@link #requestPermissions(String[], Activity, int)}替代
     */
    @Deprecated
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

    /**
     * 获取未授予权限列表
     */
    public static String[] getNoGrantedPermissions(Context context){
        List<String> permissions = new ArrayList<>();
        for (String p: INIT_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED){
                continue;
            }
            // 添加未授予权限
            permissions.add(p);
        }
        String[] pers = new String[permissions.size()];
        permissions.toArray(pers);
        return pers;
    }

    /**
     * 请求多个权限
     * @param activity
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public static void requestPermissions(Activity activity, int reqCode){
        String[] permissions = getNoGrantedPermissions(activity);
        activity.requestPermissions(permissions, reqCode);
    }

    /**
     * 请求多个权限
     * @param permissions 申请的多个权限
     * @param activity
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public static void requestPermissions(String[] permissions, Activity activity, int reqCode){
        activity.requestPermissions(permissions, reqCode);
    }
}
