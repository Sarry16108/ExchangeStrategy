package com.example.finance.tradestrategy.utils;

import android.graphics.PointF;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ToolMath {

    public static final float PRECISION = 0.00001f;

    //判断两个float值是否相等，按小数点后四位进行。
    public static boolean   isFloatEqual(float left, float right) {
        return isFloatEqual(left, right, PRECISION);
    }

    public static boolean   isFloatEqual(float left, float right, final float precision) {
        return (left - right) <= precision && (right - left) <= precision;
    }


    //判断两个float值大小，按小数点后四位进行left > right ,返回true
    public static boolean   isFloatBig(float left, float right) {
        return isFloatBig(left, right, PRECISION);
    }

    public static boolean   isFloatBig(float left, float right, final float precision) {
        return (left - right) > precision;
    }


    //判断两个float值大小，按小数点后四位进行left < right ,返回true
    public static boolean   isFloatSmall(float left, float right) {
        return isFloatSmall(left, right, PRECISION);
    }

    public static boolean   isFloatSmall(float left, float right, final float precision) {
        return (right - left) > precision;
    }


    //判断两个float值大小，按小数点后四位进行left <= right ,返回true
    public static boolean   isFloatEqualSmall(float left, float right) {
        return isFloatEqualSmall(left, right, PRECISION);
    }

    public static boolean   isFloatEqualSmall(float left, float right, final float precision) {
        return (right - left) >= precision;
    }


    //判断两个float值大小，按小数点后四位进行left >= right ,返回true
    public static boolean   isFloatEqualBig(float left, float right) {
        return isFloatEqualBig(left, right, PRECISION);
    }
    public static boolean   isFloatEqualBig(float left, float right, final float precision) {
        return (left - right) >= precision;
    }



    /******************************************************************************
     * native 方法部分
     */

    static {
        System.loadLibrary("TSMath");
    }


    /**
     *  判断两条线段是否相交
     * @param p0x,p0y,p1x,p1y 是第一条线段始终点，p2x,p2y,p3x,p3y是第二条线段始终点
     * @return  if there's a intersect, then return true or false.
     */
    public static native boolean isSegmentsIntersection(float p0x, float p0y, float p1x, float p1y,
                                          float p2x, float p2y, float p3x, float p3y);

    /**
     *  判断两条线段所在直线是否相交
     * @param p0x,p0y,p1x,p1y 是第一条线段始终点，p2x,p2y,p3x,p3y是第二条线段始终点
     * @return  if there's a intersect, then return true or false.
     */
    public static native boolean isLinesIntersect(float p0x, float p0y, float p1x, float p1y,
                                                  float p2x, float p2y, float p3x, float p3y);

    /**
     *  获取线段交点
     * @param p0x,p0y,p1x,p1y 是第一条线段始终点，p2x,p2y,p3x,p3y是第二条线段始终点
     * @return  if there's a intersect, then return the intersect point or null.
     */
    public static native PointF  getSegmentsIntersection(float p0x, float p0y, float p1x, float p1y,
                                                         float p2x, float p2y, float p3x, float p3y);
/*
    //线相交
    private boolean isLinesIntersect(float x1, float y1,
                                     float x2, float y2,
                                     float x3, float y3,
                                     float x4, float y4) {
        if (x1 == x2) {
            return !(x3 == x4 && x1 != x3);
        } else if (x3 == x4) {
            return true;
        } else {
            // Both lines are not parallel to the y-axis
            float m1 = (y1-y2)/(x1-x2);
            float m2 = (y3-y4)/(x3-x4);
            return m1 != m2;
        }
    }*/
/*

    //线段相交
    public static boolean isSegmentsIntersection(float p0x, float p0y, float p1x, float p1y,
                                          float p2x, float p2y, float p3x, float p3y)
    {
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1x - p0x;     s1_y = p1y - p0y;
        s2_x = p3x - p2x;     s2_y = p3y - p2y;

        float s, t;
        s = (-s1_y * (p0x - p2x) + s1_x * (p0y - p2y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0y - p2y) - s2_y * (p0x - p2x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            // Collision detected
//            float intersectX = p0x + (t * s1_x);
//            float intersectY = p0y + (t * s1_y);
            return true;
        }

        return false; // No collision
    }
*/

/*    public static PointF getSegmentsIntersection(float p0x, float p0y, float p1x, float p1y,
                                           float p2x, float p2y, float p3x, float p3y)
    {
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1x - p0x;     s1_y = p1y - p0y;
        s2_x = p3x - p2x;     s2_y = p3y - p2y;

        float s, t;
        s = (-s1_y * (p0x - p2x) + s1_x * (p0y - p2y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0y - p2y) - s2_y * (p0x - p2x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            // Collision detected
            return new PointF(p0x + (t * s1_x), p0y + (t * s1_y));
        }

        return null;
    }*/
}
