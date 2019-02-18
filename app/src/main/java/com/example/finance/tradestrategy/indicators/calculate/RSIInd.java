package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 */

public enum RSIInd{
    INSTANCE;
    protected static final String TAG = "RSIInd";

    //RSI选择的三个时间周期
    private final int RSI1 = 6;
    private final int RSI2 = 12;
    private final int RSI3 = 24;

    //rsi在公式中使用的前一日周期
    private final int RSI1_PRE = RSI1 - 1;
    private final int RSI2_PRE = RSI2 - 1;
    private final int RSI3_PRE = RSI3 - 1;

    public static class RSITmp {
        public float mRsi1ABSEma = 0;  //1;
        public float mRsi2ABSEma = 0;  //1;
        public float mRsi3ABSEma = 0;  //1;
        public float mRsi1MaxEma = 0;  //1;
        public float mRsi2MaxEma = 0;  //1;
        public float mRsi3MaxEma = 0;  //1;

        public RSITmp() {
        }

        public RSITmp(RSITmp rsiTmp) {
            this.mRsi1ABSEma = rsiTmp.mRsi1ABSEma;
            this.mRsi2ABSEma = rsiTmp.mRsi2ABSEma;
            this.mRsi3ABSEma = rsiTmp.mRsi3ABSEma;
            this.mRsi1MaxEma = rsiTmp.mRsi1MaxEma;
            this.mRsi2MaxEma = rsiTmp.mRsi2MaxEma;
            this.mRsi3MaxEma = rsiTmp.mRsi3MaxEma;
        }

        @Override
        public String toString() {
            return "RSITmp{" +
                    "mRsi1ABSEma=" + mRsi1ABSEma +
                    ", mRsi2ABSEma=" + mRsi2ABSEma +
                    ", mRsi3ABSEma=" + mRsi3ABSEma +
                    ", mRsi1MaxEma=" + mRsi1MaxEma +
                    ", mRsi2MaxEma=" + mRsi2MaxEma +
                    ", mRsi3MaxEma=" + mRsi3MaxEma +
                    '}';
        }
    }


    //多空级别
    private int mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL;
    //用于表示是否开始转向，true代表转向，可以买入
    private boolean        mIsReverse = false;
    
    private synchronized void computeRSI(RSITmp tmp, List<TradeInfo> values, int start, int end) {
        float rsi1 = 0;
        float rsi2 = 0;
        float rsi3 = 0;
        TradeInfo tradeInfo = null;

        for (int i = start; i < end; i++) {
            tradeInfo = values.get(i);

            float Rmax = Math.max(0, tradeInfo.getClose() - values.get(i - 1).getClose());
            float RAbs = Math.abs(tradeInfo.getClose() - values.get(i - 1).getClose());

            tmp.mRsi1MaxEma = (Rmax + RSI1_PRE * tmp.mRsi1MaxEma) / RSI1;
            tmp.mRsi1ABSEma = (RAbs + RSI1_PRE * tmp.mRsi1ABSEma) / RSI1;

            tmp.mRsi2MaxEma = (Rmax + RSI2_PRE * tmp.mRsi2MaxEma) / RSI2;
            tmp.mRsi2ABSEma = (RAbs + RSI2_PRE * tmp.mRsi2ABSEma) / RSI2;

            tmp.mRsi3MaxEma = (Rmax + RSI3_PRE * tmp.mRsi3MaxEma) / RSI3;
            tmp.mRsi3ABSEma = (RAbs + RSI3_PRE * tmp.mRsi3ABSEma) / RSI3;

            rsi1 = (tmp.mRsi1MaxEma / tmp.mRsi1ABSEma) * 100;
            rsi2 = (tmp.mRsi2MaxEma / tmp.mRsi2ABSEma) * 100;
            rsi3 = (tmp.mRsi3MaxEma / tmp.mRsi3ABSEma) * 100;
            tradeInfo.setRsi(new TradeInfo.RSI(rsi1, rsi2, rsi3));
            ToolLog.d("RSIInd", "computeRSI", tradeInfo.toString() + " " + tradeInfo.getRsi().toString());
        }

//        if (null != tradeInfo && 0 < values.size()) {
//            checkReverse(values.get(end - 1).getRsi(), tradeInfo.getRsi());
//        }
    }

    public void computeRSIHistory(RSITmp tmp, List<TradeInfo> values) {
        //i == 0时，默认设置值
        TradeInfo tradeInfo = values.get(0);
        tradeInfo.setRsi(new TradeInfo.RSI(50, 50, 50));
        computeRSI(tmp, values, 1, values.size());
    }


    public void computeRSINew(RSITmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 1) {
            return;
        }
        computeRSI(tmp, values, len - 1, len);
    }

    public void checkReverse(TradeInfo.RSI prevRsi, TradeInfo.RSI rsi) {
        mIsReverse = false;

        if (rsi.getRsi1() <= 20 && rsi.getRsi2() <= 20 && rsi.getRsi3() <= 20) {
            //反转开始，当前均值开始大于前值
            mIsReverse = true;

            if (rsi.getRsi2() <= 10 && rsi.getRsi3() <= 10) {
                mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH;
            } else {
                mLevel = InitAppConstant.FORECAST_LEVEL_BULLISH;
            }
        } else if (rsi.getRsi3() < 40) {
            mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL_BULLISH;
        } else if (rsi.getRsi3() < 60) {
            mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL;
        } else if (rsi.getRsi1() >= 80 && rsi.getRsi2() >= 80 && rsi.getRsi3() >= 80) {
            //反转开始，当前均值开始小于前值
            mIsReverse = true;
            if (rsi.getRsi2() >= 90 && rsi.getRsi3() >= 90) {
                mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH;
            } else {
                mLevel = InitAppConstant.FORECAST_LEVEL_BEARISH;
            }
        } else {
            mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH;
        }
    }

    //和前值比较，判断更优的超买超卖点，但是这个点一般时间很短，且和kdj共同判断，所以，不再和前次判断
    public void checkReverse2(TradeInfo.RSI prevRsi, TradeInfo.RSI rsi) {
        mIsReverse = false;

        if (rsi.getRsi1() <= 20 && rsi.getRsi2() <= 20 && rsi.getRsi3() <= 20) {
            rsi.setReverse(true);

            //反转开始，当前均值开始大于前值
            if (rsi.getRsi1() > prevRsi.getRsi1() && prevRsi.isReverse()) {
                mIsReverse = true;

                if (rsi.getRsi2() <= 10 && rsi.getRsi3() <= 10) {
                    mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH;
                } else {
                    mLevel = InitAppConstant.FORECAST_LEVEL_BULLISH;
                }
            } else {
                if (rsi.getRsi2() <= 10 && rsi.getRsi3() <= 10) {
                    mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH;
                } else {
                    mLevel = InitAppConstant.FORECAST_LEVEL_BULLISH;
                }
            }
        } else if (rsi.getRsi3() < 40){
            mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL_BULLISH;
        } else if (rsi.getRsi3() < 60){
            mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL;
        } else if (rsi.getRsi1() >= 80 && rsi.getRsi2() >= 80 && rsi.getRsi3() >= 80) {
            rsi.setReverse(true);

            //反转开始，当前均值开始小于前值
            if (rsi.getRsi1() < prevRsi.getRsi1() && prevRsi.isReverse()) {
                mIsReverse = true;
                if (rsi.getRsi2() >= 90 && rsi.getRsi3() >= 90){
                    mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH;
                }  else{
                    mLevel = InitAppConstant.FORECAST_LEVEL_BEARISH;
                }
            } else {
                if (rsi.getRsi2() >= 90 && rsi.getRsi3() >= 90){
                    mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH;
                }  else{
                    mLevel = InitAppConstant.FORECAST_LEVEL_BEARISH;
                }
            }
        } else {
            mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH;
        }
    }

    public boolean isIsReverse() {
//        return mIsReverse;
        return true;    //todo;
    }

    public int getLevel() {
        return mLevel;
    }
}
