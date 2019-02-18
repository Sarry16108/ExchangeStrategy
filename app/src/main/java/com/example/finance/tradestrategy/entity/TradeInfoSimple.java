package com.example.finance.tradestrategy.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/7/10.
 *
 * 汇通网外汇数据：{"e":"WH","d":"USD","o":"92.4531","h":"92.6579","l":"92.4313","c":"92.5270","p":"92.4531","t":"1505964709"}
 */

public class TradeInfoSimple extends BaseResponse {

    //价格相关
    @SerializedName("c")
    private float close;        //收价
    @SerializedName("t")
    private long  time;

    public float getClose() {
        return close;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TradeInfoSimple{" +
                "symbol=" + getSymbol() +
                "close=" + close +
                ", time=" + time +
                '}';
    }
}
