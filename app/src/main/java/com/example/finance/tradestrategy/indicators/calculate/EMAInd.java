package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.utils.ToolLog;
import com.example.finance.tradestrategy.utils.ToolMath;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 * EMA表示的是指数平滑移动平均：其函数的定义为，Y = [2 * X + (N - 1) * LY] / (N + 1)，LY 上一个周期的Y值
 *      EMAtoday=α * Pricetoday + ( 1 - α ) * EMAyesterday;
 *              =
        其中，α为平滑指数，一般取作2/(N+1)。在计算MACD指标时，EMA计算中的N一般选取12和26天，因此α相应为2/13和2/27。
 */

public enum EMAInd {
    INSTANCE;

    protected static final String TAG = "MAInd";
    //三个不同长度周期
    public static final int M1 = 6;
    public static final int M2 = 12;
    public static final int M3 = 26;

    //平滑指数取2/(N+1)，A为2,
    public static final int A               = 2;
    public static final float A_1           = (float) A / (M1 + 1);
    public static final float A_DIFF_1     = 1 - A_1;
    public static final float A_2           = (float) A / (M2 + 1);
    public static final float A_DIFF_2     = 1 - A_2;
    public static final float A_3           = (float) A / (M3 + 1);
    public static final float A_DIFF_3     = 1 - A_3;

    //计算中使用的数据

    public static class EMATmp {
        public float mMa1 = 0;
        public float mMa2 = 0;
        public float mMa3 = 0;

        public EMATmp() {
        }

        public EMATmp(EMATmp emaTmp) {
            this.mMa1 = emaTmp.mMa1;
            this.mMa2 = emaTmp.mMa2;
            this.mMa3 = emaTmp.mMa3;
        }

        @Override
        public String toString() {
            return "MATmp{" +
                    "mMa1=" + mMa1 +
                    ", mMa2=" + mMa2 +
                    ", mMa3=" + mMa3 +
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
    private synchronized void computeEMA(EMATmp tmp, List<TradeInfo> values, int start, int end) {
        TradeInfo tradeInfo = null;
        TradeInfo preTradeInfo  = null;

        for (int i = start; i < end; ++i) {
            tradeInfo = values.get(i);
            preTradeInfo = values.get(i - 1);
            TradeInfo.EMA ema = tradeInfo.getEma();
            TradeInfo.EMA preEma = preTradeInfo.getEma();

            float valueType = /*tradeInfo.getMa().getMa2(); //*/tradeInfo.getClose();

            if (i >= M1) {
                ema.setEma1(valueType * A_1 + preEma.getEma1() * A_DIFF_1);
            } else {
                ema.setEma1((A * valueType + i * preEma.getEma1()) / (i + 2));   //N = i + 1
            }

            if (i >= M2) {
                ema.setEma2(valueType * A_2 + preEma.getEma2() * A_DIFF_2);
            } else {
                ema.setEma2((A * valueType + i * preEma.getEma2()) / (i + 2));   //N = i + 1
            }

            if (i >= M3) {
                ema.setEma3(valueType * A_3 + preEma.getEma3() * A_DIFF_3);
            } else {
                ema.setEma3((A * valueType + i * preEma.getEma3()) / (i + 2));   //N = i + 1
            }

            checkIntersect(ema, preEma);

            ToolLog.d("EMAInd", "computeEMA", tradeInfo.toString() + " " + ema.toString());
        }
    }

    public void computeEMAHistory(EMATmp tmp, List<TradeInfo> values) {
        TradeInfo tradeInfo = values.get(0);
        tradeInfo.setEma(new TradeInfo.EMA(tradeInfo.getClose(), tradeInfo.getClose(), tradeInfo.getClose()));

        computeEMA(tmp, values, 1, values.size());
    }

    public void computeEMANew(EMATmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 2) {
            return;
        }
        computeEMA(tmp, values, len - 1, len);
    }

    /**
     * 交点的判断
     */
    private void checkIntersect(TradeInfo.EMA ema, TradeInfo.EMA preEma) {
        //先求四舍五入，保留4位小数，不精确求交点
//        float ema1 = ToolDigitFormat.DigitalRound(ema.getEma1(), 4);
//        float ema2 = ToolDigitFormat.DigitalRound(ema.getEma2(), 4);
//        float ema3 = ToolDigitFormat.DigitalRound(ema.getEma3(), 4);
//        float preEma1 = ToolDigitFormat.DigitalRound(preEma.getEma1(), 4);
//        float preEma2 = ToolDigitFormat.DigitalRound(preEma.getEma2(), 4);
//        float preEma3 = ToolDigitFormat.DigitalRound(preEma.getEma3(), 4);
        ema.diff1 = ema.getEma1() - preEma.getEma1();
        ema.ema1_2 = ToolMath.isSegmentsIntersection(0, preEma.getEma1(), 1, ema.getEma1(), 0, preEma.getEma2(), 1, ema.getEma2());
        ema.ema2_3 = ToolMath.isSegmentsIntersection(0, preEma.getEma2(), 1, ema.getEma2(), 0, preEma.getEma3(), 1, ema.getEma3());
        ema.ema1_3 = ToolMath.isSegmentsIntersection(0, preEma.getEma1(), 1, ema.getEma1(), 0, preEma.getEma3(), 1, ema.getEma3());
    }
}
