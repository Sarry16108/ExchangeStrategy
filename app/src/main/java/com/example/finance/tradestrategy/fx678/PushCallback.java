package com.example.finance.tradestrategy.fx678;

import com.example.finance.tradestrategy.entity.TradeInfoSimple;

/**
 * Created by Administrator on 2017/9/6.
 */

public interface PushCallback {
    public abstract void onUDP_push(TradeInfoSimple tradeInfo);
}
