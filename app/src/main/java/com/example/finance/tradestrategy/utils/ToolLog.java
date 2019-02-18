package com.example.finance.tradestrategy.utils;


import android.util.Log;

/**
 * 日志工具类
 */
public class ToolLog {
    public static boolean isDebug = true;
    private static String TAG = "测测";

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, getMethodLine() + msg);
        }
    }

    public static void i(String tag1, String tag2, String msg) {
        if (isDebug) {
            Log.i(TAG, getMethodLine() + msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, getMethodLine() + msg);
        }
    }

    public static void d(String tag1, String tag2, String msg) {
        if (isDebug) {
            Log.d(TAG, getMethodLine() + msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, getMethodLine() + msg);
        }
    }

    public static void e(String tag1, String tag2, String msg) {
        if (isDebug) {
            Log.e(TAG, getMethodLine() + msg);
        }
    }


    public static void e(String tag1, String tag2, String tag3, String msg) {
        if (isDebug) {
            Log.e(getMethodLine(), getMethodLine() + msg);
        }
    }

    private static String getMethodLine() {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        final int index = 4;
        return getSimpleName(stackTraceElement[index].getClassName()) + "." + stackTraceElement[index].getMethodName() + "  (" + stackTraceElement[index].getFileName() + ":" + stackTraceElement[index].getLineNumber() + ")  ";
    }

    private static String getSimpleName(String className) {
        int index = className.lastIndexOf('.');
        if (-1 < index) {
            return className.substring(index + 1, className.length());
        } else {
            return className;
        }
    }
}
