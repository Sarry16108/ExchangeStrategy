package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 * 布林线
 * 以日BOLL指标计算为例，其计算方法如下。
 计算公式
 中轨线=N日的移动平均线
 上轨线=中轨线+两倍的标准差
 下轨线=中轨线－两倍的标准差
 计算过程
 （1）计算MA
 MA=N日内的收盘价之和÷N
 （2）计算标准差MD
 MD=平方根N日的（C－MA）的两次方之和除以N
 （3）计算MB、UP、DN线
 MB=（N－1）日的MA
 UP=MB+k×MD
 DN=MB－k×MD
 （K为参数，可根据股票的特性来做相应的调整，一般默认为2）

 1、强势区，在中轨、上轨发生macd金叉，则是看多标识。   买入看多
    强势区，在中轨、上轨间发生macd死叉，方向不明，一般小幅度调整上扬。
 2、弱势区，在中轨、下轨发生macd死叉，则是看空标识。   买入看空。
    弱势区，在中轨、下轨发生macd金叉，方向不明，一般小幅度调整下降。
 */

public enum BollInd {
    INSTANCE;
    protected static final String TAG = "ToolAlgorithm";
    private final int PERIOD = MAInd.M3;

    public static class BollTmp {

        public BollTmp() {
        }

        public BollTmp(BollTmp bollTmp) {
        }

        @Override
        public String toString() {
            return "BollTmp{}";
        }
    }
    /**
     * 计算历史boll值
     */
    private synchronized void computeBoll(List<TradeInfo> values, int start, int end) {
        TradeInfo tradeInfo = null;
        float value = 0, mb = 0, upper = 0, up = 0, dn = 0, dnner = 0, width = 0, b = 0;
        int n = PERIOD;
        for (int i = start; i < end; ++i) {
            tradeInfo = values.get(i);

            if (i < PERIOD) {
                n = i + 1;
            }

            float md = 0;
            for (int j = i - n + 1; j <= i; j++) {
                value = values.get(j).getClose() - tradeInfo.getMa().getMa3();  //todo:PERIOD一同修改
                md += value * value;
            }

            md = (float) Math.sqrt(md / (n - 1));
            mb = tradeInfo.getMa().getMa3();                                    //todo:PERIOD一同修改

            tradeInfo.setBoll(new TradeInfo.Boll(mb, md, tradeInfo.getOpen(), tradeInfo.getClose()));

//            float w1 = (upper - dnner) / dnner;
//            float w2 = (upper - mb) / mb;
//            float w3 = (mb - dnner) / mb;
//            ToolLog.d("time:" + tradeInfo.getXLabel() + " w1:" + w1 + " w2:" + w2 + " w3:" + w3 + " md:" + md + " md*mb:" + md * mb);
            ToolLog.d("BollInd", "computeBoll", tradeInfo.toString() + " " + tradeInfo.getBoll().toString());
        }

        if (null != tradeInfo) {
            checkTrend(tradeInfo.getBoll());
        }
    }

    public void computeBollHistory(BollTmp tmp, List<TradeInfo> values) {
        //i = 0时单独处理，减少不必要循环判断
        TradeInfo tradeInfo = values.get(0);
        tradeInfo.setBoll(new TradeInfo.Boll(tradeInfo.getClose(), 0, tradeInfo.getOpen(), tradeInfo.getClose()));

        computeBoll(values, 1, values.size());
    }

    /**
     * 针对最新一个数据进行计算
     */
    public void computeBollNew(BollTmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 1) {
            return;
        }
        computeBoll(values, len - 1, len);
    }

    public void checkTrend(TradeInfo.Boll boll) {
        if (boll.getWidth() > 0.1) {
            ToolLog.e(TAG, "checkTrend", "boll width( " + boll.getWidth() + " )> 0.1");
        }
    }
}












