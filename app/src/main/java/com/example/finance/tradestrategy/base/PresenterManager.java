package com.example.finance.tradestrategy.base;

import android.content.Intent;
import android.support.v4.util.ArrayMap;

import com.example.finance.tradestrategy.activities.SettingAct;
import com.example.finance.tradestrategy.activities.StockInfoAct;
import com.example.finance.tradestrategy.baseui.BaseActivity;
import com.example.finance.tradestrategy.globaldata.InitData;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/26.
 * 页面跳转管理
 */

public class PresenterManager {
    private static final String TAG = "PresenterManager";

    //页面关闭处理模块
    private static class Singleton {
        static List<BaseActivity> ACTIVITIES = new ArrayList<>(5);
        static Map<String, Integer> MARKS = new ArrayMap<>(2);     //页面标记数，用以记录打开的数量
    }

    //新activity
    public static int add(BaseActivity activity) {
        if (null == activity) {
            ToolLog.e(TAG, "add", "activity is not exist");
            return Singleton.ACTIVITIES.size();
        }

        Singleton.ACTIVITIES.add(activity);
        ToolLog.d(TAG, "add", "activity is added:" + Singleton.ACTIVITIES.size());
        return Singleton.ACTIVITIES.size();
    }

    //移除activity
    public static int remove(BaseActivity activity) {
        int index = Singleton.ACTIVITIES.indexOf(activity);
        if (null == activity || 0 > index) {
            ToolLog.e(TAG, "remove", "activity is not exist before:" + index + "  size:" + Singleton.ACTIVITIES.size());
            return Singleton.ACTIVITIES.size();
        }

        activity.finish();
        Singleton.ACTIVITIES.remove(activity);

        //如果删除的是当前页面之前的值，那么需要重置一下删除页面之后的所有位置
        if (index < Singleton.ACTIVITIES.size()) {
            int pos = index;
            for (Map.Entry<String, Integer> item : Singleton.MARKS.entrySet()) {
                pos = item.getValue();
                if (pos > index) {
                    item.setValue(--pos);
                }
            }
        }

        ToolLog.d(TAG, "remove", "activity is removed:" + Singleton.ACTIVITIES.size());
        return Singleton.ACTIVITIES.size();
    }

    //反向清理activity
    public static int removeReverse(int length) {
        int size = Singleton.ACTIVITIES.size();
        if (length > size || 1 > length) {
            ToolLog.e(TAG, "removeReverse", "remove length " + length + " and size " + size);
            return size;
        }

        for (int i = size - 1; i >= size - length; --i) {
            remove(Singleton.ACTIVITIES.get(i));
        }

        return Singleton.ACTIVITIES.size();
    }

    //反向清理marked计数的activity
    public static int removeReverseToMarked(String key) {
        int item = 0;
        if (!Singleton.MARKS.containsKey(key)) {
            Integer pos = Singleton.MARKS.get(InitData.ACT_MARK_MAIN_ACT);   //查找不到的，就跳转到首页
            item = null != pos ? pos : 0;
        } else {
            item = Singleton.MARKS.get(key);
        }
        int size = Singleton.ACTIVITIES.size();
        int marked = size - item;
        if (marked > size || 1 > marked) {
            ToolLog.e(TAG, "removeReverseToMarked", "remove length " + marked + " and size " + size);
            return size;
        }

        for (int i = size - 1; i >= size - marked; --i) {
            remove(Singleton.ACTIVITIES.get(i));
        }

        return Singleton.ACTIVITIES.size();
    }

    //重置标志
    public static void resetActivityMarked(String key) {
        Singleton.MARKS.put(key, Singleton.ACTIVITIES.size());
    }

    //获取标志
    public static int getActivityMarked(String key) {
        return Singleton.MARKS.get(key);
    }

    //移除标志
    public static int removeActivityMarked(String key) {
        if (null == Singleton.MARKS || !Singleton.MARKS.containsKey(key)) {
            return -1;
        }
        return Singleton.MARKS.remove(key);
    }

    public static void restartApp(BaseActivity activity) {
        Intent intent2 = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent2);
    }
    //////////////////////////////页面跳转//////////////////////////////////

    //用户详情页
    public static void toStockInfo(BaseActivity activity) {
        Intent intent = new Intent(activity,StockInfoAct.class);
        activity.startActivity(intent);
    }

    public static void toSetting(BaseActivity activity, int reqCode) {
        Intent intent = new Intent(activity,SettingAct.class);
        activity.startActivityForResult(intent, reqCode);
    }
}
