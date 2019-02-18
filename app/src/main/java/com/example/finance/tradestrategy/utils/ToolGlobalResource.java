package com.example.finance.tradestrategy.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.example.finance.tradestrategy.base.BaseApplication;

/**
 * 资源获取工具类
 * Created by Yanghj on 2016/11/21.
 */

public class ToolGlobalResource {
    private final String TAG = "GlobalResources";

    public static String getString(int idRes) {
        return BaseApplication.mContext.getResources().getString(idRes);
    }


    public static String[] getStringArray(int idRes) {
        return BaseApplication.mContext.getResources().getStringArray(idRes);
    }

    public static String getString(int idRes, Object... objects) {
        String value = "";
        try {
            value = BaseApplication.mContext.getResources().getString(idRes, objects);
        } catch (Resources.NotFoundException ex) {
            ToolLog.e(ex.getMessage());
        }

        return value;
    }

    public static Drawable getDrawable(int idRes) {
        return BaseApplication.mContext.getResources().getDrawable(idRes);
    }

    public static int getDimen(int idRes) {
        int dimen = 0;
        try {
            dimen = BaseApplication.mContext.getResources().getDimensionPixelOffset(idRes);
        } catch (Resources.NotFoundException ex) {
            ToolLog.e(ex.getMessage());
        }

        return dimen;
    }

    public static int getColor(int idRes) {
        int dimen = 0;
        try {
            dimen = BaseApplication.mContext.getResources().getColor(idRes);
        } catch (Resources.NotFoundException ex) {
            ToolLog.e(ex.getMessage());
        }

        return dimen;
    }

}
