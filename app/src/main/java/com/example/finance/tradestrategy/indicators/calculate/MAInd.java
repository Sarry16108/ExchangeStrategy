package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.utils.ToolDigitFormat;
import com.example.finance.tradestrategy.utils.ToolLog;
import com.example.finance.tradestrategy.utils.ToolMath;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 * 移动平均线：求N个数据的平均值
 */

public enum MAInd{
    INSTANCE;

    protected static final String TAG = "MAInd";
    public static final int M1 = 5;
    public static final int M2 = 10;
    public static final int M3 = 20;
    public static final int M4 = 40;

    //计算中使用的数据

    public static class MATmp {
        public float mMa1 = 0;
        public float mMa2 = 0;
        public float mMa3 = 0;
        public float mMa4 = 0;

        public MATmp() {
        }

        public MATmp(MATmp maTmp) {
            this.mMa1 = maTmp.mMa1;
            this.mMa2 = maTmp.mMa2;
            this.mMa3 = maTmp.mMa3;
            this.mMa4 = maTmp.mMa4;
        }

        @Override
        public String toString() {
            return "MATmp{" +
                    "mMa1=" + mMa1 +
                    ", mMa2=" + mMa2 +
                    ", mMa3=" + mMa3 +
                    ", mMa4=" + mMa4 +
                    '}';
        }
    }

    /**
     *
     * @param tmp
     * @param values
     * @param start
     * @param end       即values的长度
     */
    private synchronized void computeMA(MATmp tmp, List<TradeInfo> values, int start, int end) {
        TradeInfo tradeInfo = null;
        //tradeInfo前一个点
        TradeInfo.MA preMa = null;
        for (int i = start; i < end; ++i) {
            tradeInfo = values.get(i);

            //设置每一个tradeInfo的open/close的最大最小值
            //因为此指标在所有策略分析前，所以entityTop和entityBot初始化在此。
            tradeInfo.setEntityTop(Math.max(tradeInfo.getOpen(), tradeInfo.getClose()));
            tradeInfo.setEntityBot(Math.min(tradeInfo.getOpen(), tradeInfo.getClose()));

            TradeInfo.MA ma = tradeInfo.getMa();

            tmp.mMa1 += tradeInfo.getClose();
            tmp.mMa2 += tradeInfo.getClose();
            tmp.mMa3 += tradeInfo.getClose();
            tmp.mMa4 += tradeInfo.getClose();

            if (i >= M1) {
                tmp.mMa1 -= values.get(i - M1).getClose();
                ma.setMa1(tmp.mMa1 / M1);
            } else {
                ma.setMa1(tmp.mMa1 / (i + 1));
            }

            if (i >= M2) {
                tmp.mMa2 -= values.get(i - M2).getClose();
                ma.setMa2(tmp.mMa2 / M2);
            } else {
                ma.setMa2(tmp.mMa2 / (i + 1));
            }

            if (i >= M3) {
                tmp.mMa3 -= values.get(i - M3).getClose();
                ma.setMa3(tmp.mMa3 / M3);
            } else {
                ma.setMa3(tmp.mMa3 / (i + 1));
            }

            if (i >= M4) {
                tmp.mMa4 -= values.get(i - M4).getClose();
                ma.setMa4(tmp.mMa4 / M4);
            } else {
                ma.setMa4(tmp.mMa4 / (i + 1));
            }

            if (i >= 1) {
                preMa = values.get(i - 1).getMa();
                ma.dif1 = ma.getMa1() - preMa.getMa1();
                ma.dif2 = ma.getMa2() - preMa.getMa2();
                ma.dif3 = ma.getMa3() - preMa.getMa3();
                ma.dif4 = ma.getMa4() - preMa.getMa4();
            }

            tradeInfo.setMa(ma);

            ToolLog.d("MAInd", "computeMA", tradeInfo.toString() + " " + ma.toString());
        }


        if (null != tradeInfo) {
            checkIntersect(values.get(end - 2), tradeInfo);
        }
    }

    public void computeMAHistory(MATmp tmp, List<TradeInfo> values) {
        computeMA(tmp, values, 0, values.size());
    }

    public void computeMANew(MATmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 1) {
            return;
        }
        computeMA(tmp, values, len - 1, len);
    }

    private void checkIntersect(TradeInfo prevTradeInfo, TradeInfo tradeInfo) {
        TradeInfo.MA preMa = prevTradeInfo.getMa();
        TradeInfo.MA ma = tradeInfo.getMa();

//        float ma1 = ToolDigitFormat.DigitalRound(ma.getMa1(), 4);
//        float ma2 = ToolDigitFormat.DigitalRound(ma.getMa2(), 4);
//        float ma3 = ToolDigitFormat.DigitalRound(ma.getMa3(), 4);
//        float preMa1 = ToolDigitFormat.DigitalRound(preMa.getMa1(), 4);
//        float preMa2 = ToolDigitFormat.DigitalRound(preMa.getMa2(), 4);
//        float preMa3 = ToolDigitFormat.DigitalRound(preMa.getMa3(), 4);

        ma.ma5_10 = ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preMa.getMa2(), 1, ma.getMa2());
        ma.ma5_20 = ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preMa.getMa3(), 1, ma.getMa3());
        ma.ma10_20 = ToolMath.isSegmentsIntersection(0, preMa.getMa2(), 1, ma.getMa2(), 0, preMa.getMa3(), 1, ma.getMa3());
        ma.ma20_40 = ToolMath.isSegmentsIntersection(0, preMa.getMa3(), 1, ma.getMa3(), 0, preMa.getMa4(), 1, ma.getMa4());
    }
}
