package com.example.finance.tradestrategy.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by yanghj on 2017/6/3.
 */

public class ToolDigitFormat {

    //默认两位
    public static String floatToStr(float value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static float decimalPlace(float value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static String formatFloat(double value) {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        return format.format(value).toString();
    }


    public static String formatFloatStr(float value) {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        return format.format(value).toString();
    }

    /**
     * 四舍五入到哪一位
     * @param value
     * @param number    小数点保留位数
     * @return
     */
    public static float DigitalRound (float value, int number) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(number, BigDecimal.ROUND_HALF_UP).floatValue();
    }



}
