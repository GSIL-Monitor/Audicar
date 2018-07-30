package com.beautyyan.beautyyanapp.utils;

import android.util.Log;

/**
 * Created by xuelu on 2017/3/24.
 */

public class LogUtil {

    private static final String TAG = "YUYUAN";

    public static void e(String strLog) {
        Log.e(TAG, strLog);
    }

    public static void d(String strLog) {
        Log.d(TAG, strLog);
    }

    public static void i(String strLog) {
        Log.i(TAG, strLog);
    }

    public static void v(String strLog) {
        Log.v(TAG, strLog);
    }
    public static void w(String strLog) {
        Log.w(TAG, strLog);
    }
}
