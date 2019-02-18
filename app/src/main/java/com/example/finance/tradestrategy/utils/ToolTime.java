package com.example.finance.tradestrategy.utils;


import android.support.v4.util.ArrayMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/2.
 */

public class ToolTime {
    private static Date mDate = new Date();
    private static SimpleDateFormat sd = new SimpleDateFormat(
            "yyyy-MM-dd kk:mm:ss");
    private static SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.US);
    private static SimpleDateFormat formatMDHMS = new SimpleDateFormat("MM-dd HH:mm:ss");
    private static SimpleDateFormat formatMDHM = new SimpleDateFormat("M-dd HH:mm");

    //一天的毫秒数
    public static int DAY_LEN = 24 * 60 * 60 * 1000;
    public static int DAY_MARKET_LEN = (7 * 60 + 30) * 60 * 1000;


    public static long mMarketStart = 0;
    public static long mMarketEnd = 0;

    public static void initTime() {
        mMarketStart = ToolTime.getDayStartTime(21, 30);
        mMarketEnd = mMarketStart + ToolTime.DAY_MARKET_LEN;
    }

    /**
     *  这部分是通用的判断周期性判断时间是否到达的模块，在使用结束时，必须调用removeTime移除
     *  遗留的标签。
     */
    public static Map<String, Long> mReachTimes = new ArrayMap<>();

    //毫秒为单位，到达时间点，则返回true，没有到达更新时间点则返回false
    public static boolean isReachTimeMS(String tag, long delay) {
        boolean isReachTime = false;
        long time = System.currentTimeMillis();
        if (mReachTimes.containsKey(tag)) {
            isReachTime = mReachTimes.get(tag) <= time;
            if (isReachTime) {
                mReachTimes.put(tag, time + delay);
            }
        } else {
            isReachTime = true;
            mReachTimes.put(tag, time + delay);
        }

        ToolLog.d(tag + "delay：" + delay + " cur: " + time +  "  new Time: " + mReachTimes.get(tag));

        return isReachTime;
    }

    public static long removeTimeTag(String tag) {
        if (mReachTimes.containsKey(tag)) {
            return mReachTimes.remove(tag);
        }

        return 0;
    }


    public static long getFromYMDHMS(String str) {
        try {
            Date date = sd.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis();
    }

    //转化成gmt标准时间
    public static long getFromGmt(String str) {
        try {
            return gmtFormat.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param time  unit is milli second
     * @return
     */
    public static String getMDHMS(long time) {
        mDate.setTime(time);
        return formatMDHMS.format(mDate);
    }

    /**
     * @param time  unit is second
     * @return
     */
    public static String getMDHM(long time) {
        mDate.setTime(time * 1000);
        return formatMDHM.format(mDate);
    }
    /**
     * 分钟起始点
     * @return
     */
    public static long getMinuteStartInMilli() {
        return System.currentTimeMillis() / 60000 * 60000;
    }

    /**
     * 每天几点开始的time
     * @param hour  24小时格式，例如晚上9:30为21:30
     * @return
     */
    public static long getDayStartTime(int hour, int minute) {
        return System.currentTimeMillis() / DAY_LEN * DAY_LEN + ((hour - 8) * 60 + minute) * 60 * 1000;
    }

    /**
     * 返回以服务器为准的当天的时分秒
     * @param serverTime   服务器当前时间
     * @param hour          服务器对应天的小时
     * @param minute        服务器对应小时的分钟
     * @return
     */
    public static long getServerStartTime(long serverTime, int hour, int minute) {
        return serverTime / DAY_LEN * DAY_LEN + ((hour - 8) * 60 + minute) * 60 * 1000;
    }

    /**
     * 返回hour和minute所对应的时间差，单位是ms
     * @param hour
     * @param minute
     * @return
     */
    public static long getTimeDiffMillis(int hour, int minute) {
        return (hour * 60 + minute) * 60 * 1000;
    }
}
