package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 * 1、当股价上升而OBV线下降，表示买盘无力，股价可能会回跌。
 2、股价下降时而OBV线上升，表示买盘旺盛，逢低接手强股，股价可能会止跌回升。
 3、OBV线缓慢上升，表示买气逐渐加强，为买进信号。
 4、OBV线急速上升时，表示力量将用尽为卖出信号。
 5、OBV线从正的累积数转为负数时，为下跌趋势，应该卖出持有股票。反之，OBV线从负的累积数转为正数时，应该买进股票。
 6、OBV线最大的用处，在于观察股市盘局整理后，何时会脱离盘局以及突破后的未来走势，OBV线变动方向是重要参考指数，其具体的数值并无实际意义。
 7、OBV线对双重顶第二个高峰的确定有较为标准的显示，当股价自双重顶第一个高峰下跌又再次回升时，如果OBV线能够随股价趋势同步上升且价量配合，则可持续多头市场并出现更高峰。
 相反，当股价再次回升时OBV线未能同步配合，却见下降，则可能形成第二个顶峰，完成双重顶的形态，导致股价反转下跌。
 */

public enum OBVInd {
    INSTANCE;

    public static class OBVTmp {
        public OBVTmp() {
        }

        public OBVTmp(OBVTmp obvTmp) {
        }

        @Override
        public String toString() {
            return "OBVTmp{}";
        }
    }

    //是否穿过0线
    private boolean mIsCrossZero;
    private int     mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL;

    /**
     *
     * @param values
     * @param start     至少从1开始
     * @param end
     */
    private synchronized void computeOBV(List<TradeInfo> values, int start, int end) {

        TradeInfo tradeInfo = null, lastTradeInfo = null;
        float obv = 0, ratio = 0;
        for (int i = start; i < end; ++i) {
            tradeInfo = values.get(i);
            lastTradeInfo = values.get(i - 1);

            if (tradeInfo.getClose() >= lastTradeInfo.getClose()) {
                obv = lastTradeInfo.getObv().getV() + tradeInfo.getVolume();
            } else {
                obv = lastTradeInfo.getObv().getV() - tradeInfo.getVolume();
            }

            //多空比率净额= [（收盘价－最低价）－（最高价-收盘价）] ÷（ 最高价－最低价）×V
            if (tradeInfo.getHigh() != tradeInfo.getLow()) {
                ratio = (tradeInfo.getClose() * 2 - tradeInfo.getLow() - tradeInfo.getHigh()) / (tradeInfo.getHigh() - tradeInfo.getLow()) * obv;
            } else {
                ratio = obv;
            }

            tradeInfo.setObv(new TradeInfo.OBV(obv, ratio));
            ToolLog.d("OBVInd", "computeOBV", tradeInfo.toString() + " " + tradeInfo.getObv().toString());
        }

//        if (null != tradeInfo && 0 < values.size()) {
//            checkTrend(values.get(end - 1).getObv(), tradeInfo.getObv());
//        }
    }

    public void computeOBVHistory(OBVTmp tmp, List<TradeInfo> values) {
        values.get(0).setObv(new TradeInfo.OBV(0, 0));
        computeOBV(values, 1, values.size());
    }

    public void computeOBVNew(OBVTmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 2) {
            return;
        }
        computeOBV(values, len - 1, len);
    }

    private void checkTrend(TradeInfo.OBV prevObv, TradeInfo.OBV obv) {
        //下穿0线，卖出
        if (prevObv.getV() > 0 && obv.getV() < 0) {
            mIsCrossZero = true;
            mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH;
        } else if (prevObv.getV() < 0 && obv.getV() >0) {   //上穿，买入
            mIsCrossZero = true;
            mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH;
        }
    }


    public boolean isCrossZero() {
        return mIsCrossZero;
    }

    public int getLevel() {
        return mLevel;
    }

}
