package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.util.List;

/**
 * 震荡指标：
 * （close-open）的累加。
 * Created by Administrator on 2017/10/11.
 */

public enum ShockInd {
    INSTANCE;

    protected static final String TAG = "ShockInd";
    //周期是5天
    private final int   PERIOD = 30;

    public static class ShockTmp {
        public float sh = 0;

        public ShockTmp() {
        }


        public ShockTmp(ShockTmp shockTmp) {
            this.sh = shockTmp.sh;
        }

        @Override
        public String toString() {
            return "ShockTmp{" +
                    "sho=" + sh +
                    '}';
        }
    }


    private synchronized void computeShock(ShockInd.ShockTmp tmp, List<TradeInfo> values, int start, int end) {
        TradeInfo tradeInfo = null;
        for (int i = start; i < end; ++i) {
            tradeInfo = values.get(i);
            tmp.sh += tradeInfo.getClose() - tradeInfo.getOpen();

            if (i > PERIOD) {
                TradeInfo tmpTradeInfo = values.get(i - PERIOD);
                tmp.sh -= (tmpTradeInfo.getClose() - tmpTradeInfo.getOpen());
            }

            tradeInfo.setShock(new TradeInfo.Shock(tmp.sh));
        }

        if (null != tradeInfo) {
            ToolLog.d("ShockInd", "computeShock", tradeInfo.toString() + " " + tradeInfo.getShock().toString());
        }
    }

    public void computeShockHistory(ShockInd.ShockTmp tmp, List<TradeInfo> values) {
        computeShock(tmp, values, 0, values.size());
    }

    public void computeShockNew(ShockInd.ShockTmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 1) {
            return;
        }
        computeShock(tmp, values, len - 1, len);
    }

}
