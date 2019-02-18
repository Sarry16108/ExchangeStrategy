package com.example.finance.tradestrategy.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.finance.tradestrategy.base.BaseApplication;

import java.util.Set;

/**
 * Created by yanghj on 2017/6/5.
 */

public class ToolSharePre {
    static class Init {
        private static SharedPreferences  mShared = BaseApplication.mContext.getSharedPreferences("tradestrategy", Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        if (null == key || null == value) {
            return;
        }

        Init.mShared.edit().putString(key, value).commit();
    }

    public static String getString(String key) {
        return Init.mShared.getString(key, "");
    }


    public static void putLong(String key, long value) {
        Init.mShared.edit().putLong(key, value).commit();
    }

    public static long getLong(String key) {
        return Init.mShared.getLong(key, 0);
    }

    public static <T> T getObject(String key, Class<T> type) {
        return ToolGson.castJsonObject(getString(key), type);
    }

    public static <T> void putObject(String key, T obj) {
        putString(key, ToolGson.castObjectJson(obj));
    }

    public static <T> Set<T> getObjectSet(String key, Class<T> type) {
        return ToolGson.castJsonObjSet(getString(key), type);
    }
}
