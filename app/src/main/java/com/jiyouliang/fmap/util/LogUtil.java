package com.jiyouliang.fmap.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * 控制台日子
 */
public class LogUtil {

    /**
     * Priority constant for the println method; use Log.v.
     */
    private static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    private static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    private static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    private static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    private static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    private static final int ASSERT = 7;
    private static final boolean DEBUGGING = false;

    /**
     * 当前日志级别
     */
    private static final int CURRENT_LEVEL = ERROR;

    public static void v(String tag, String msg) {
        if (VERBOSE >= CURRENT_LEVEL && DEBUGGING) {
            if(TextUtils.isEmpty(msg)){
                Log.e(tag, "parameter msg cannot be null");
                return;
            }
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG >= CURRENT_LEVEL && DEBUGGING) {
            if(TextUtils.isEmpty(msg)){
                Log.e(tag, "parameter msg cannot be null");
                return;
            }
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (INFO >= CURRENT_LEVEL && DEBUGGING) {
            if(TextUtils.isEmpty(msg)){
                Log.e(tag, "parameter msg cannot be null");
                return;
            }
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (WARN >= CURRENT_LEVEL && DEBUGGING) {
            if(TextUtils.isEmpty(msg)){
                Log.e(tag, "parameter msg cannot be null");
                return;
            }
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ERROR >= CURRENT_LEVEL && DEBUGGING) {
            if(TextUtils.isEmpty(msg)){
                Log.e(tag, "parameter msg cannot be null");
                return;
            }
            Log.e(tag, msg);
        }
    }

   /* public static void a(String tag, String msg) {
        if (ASSERT >= CURRENT_LEVEL && DEBUGGING) {
            Log.a(tag, msg);
        }
    }*/
}
