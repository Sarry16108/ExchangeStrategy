package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.utils.ToolLog;
import com.example.finance.tradestrategy.utils.ToolMath;
import com.example.finance.tradestrategy.utils.ToolTime;

import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 * kdj：j最灵敏，k次之，d最稳定
 * 适用于交易量大，活跃的股票
 *
 *  均为5分钟图
 * 1、在超买区，当k在d右侧交叉时候（如果在连续超买区，则以最后一次为最佳点），且当前K值已经离开超买区，为看空标识。
 * 2、在超卖区，当k在d右侧交叉时候（如果在连续超卖区，则以最后一次为最佳点），且当前K值已经离开超卖区，为看多标识
 * 3、当k>100时，且满足1条件，为立即看空标识。
 * 4、当k<0时，且满足2条件，为立即看多标识。
 *
 * 看空：
 * 1、5分钟图j>100且15分图j>100时进行关注，当j值超过100，逐渐增大不予理会，当j值减小到j<100情况时，入手。
 *
 * 看多：
 * 1、5分钟图j<0且15分图j<0时进行关注，当j值小于0，且逐渐减小时不予理会，当j值增大到j>0时，入手。
 */

public enum KDJInd{
    INSTANCE;
    protected static final String TAG = "KDJInd";

    private final int CYCLE_LEN = 9  - 1;   //9代表9天，-1是公式使用，如修改，只修改9这个值

    //mK,d如果没有前值，建议用50
    public static class KDJTmp {
        public float mK = 50;
        public float mD = 50;

        public KDJTmp() {
        }

        public KDJTmp(KDJTmp kdjTmp) {
            this.mK = kdjTmp.mK;
            this.mD = kdjTmp.mD;
        }

        @Override
        public String toString() {
            return "KDJTmp{" +
                    "mK=" + mK +
                    ", mD=" + mD +
                    '}';
        }
    }

    /**
     * 计算历史kdj值
     */
    private synchronized void computeKDJ(KDJTmp tmp, List<TradeInfo> values, int start, int end) {
        TradeInfo tradeInfo = null;
        for (int i = start; i < end; i++) {
            tradeInfo = values.get(i);

            int startIndex = i - CYCLE_LEN;
            if (startIndex < 0) {
                startIndex = 0;
            }

            float max9 = 0;
            float min9 = 10000;
            for (int index = startIndex; index <= i; index++) {     //可以优化
                max9 = Math.max(max9, values.get(index).getHigh());
                min9 = Math.min(min9, values.get(index).getLow());
            }

            float rsv = 0;
            if (max9 != min9) {
                rsv = 100 * (tradeInfo.getClose() - min9) / (max9 - min9);
            }
            tmp.mK = (rsv + 2 * tmp.mK) / 3 ;
            tmp.mD = (tmp.mK + 2 * tmp.mD) / 3;

            tradeInfo.setKdj(new TradeInfo.KDJ(tmp.mK, tmp.mD, 3 * tmp.mK - 2 * tmp.mD));
            ToolLog.d("KDJInd", "computeKDJ", ToolTime.getMDHMS(tradeInfo.getTime()) + "---" +tradeInfo.toString() + " " + tradeInfo.getKdj().toString());
        }

        if (null != tradeInfo) {
            checkIntersect(values.get(end - 2), tradeInfo);
        }
    }

    public synchronized void computeKDJHistory(KDJTmp tmp, List<TradeInfo> values) {
        computeKDJ(tmp, values, 0, values.size());
    }

    /**
     * 针对最新一个数据进行计算
     */
    public synchronized void computeKDJNew(KDJTmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 1) {
            return;
        }
        computeKDJ(tmp, values, len - 1, len);
    }

    /**
     * 是否有交点
     */
    private void checkIntersect(TradeInfo prevTradeInfo, TradeInfo tradeInfo) {
        TradeInfo.KDJ prevKdj = prevTradeInfo.getKdj();
        TradeInfo.KDJ kdj = tradeInfo.getKdj();

        kdj.hasIntersect = ToolMath.isSegmentsIntersection(0, prevKdj.getK(), 1, kdj.getK(), 0, prevKdj.getD(), 1, kdj.getD());
    }

    private void checkOverBoughtOrSellZone(TradeInfo tradeInfo) {
        TradeInfo.KDJ kdj = tradeInfo.getKdj();
        if ((kdj.getD() > 80 && kdj.getJ() > 80 && kdj.getK() > 80) || (kdj.getD() < 20 && kdj.getJ() < 20 && kdj.getK() < 20)) {
            kdj.overBoughtOrSellZone = true;
        }
    }
    //在超买超卖区，判断两线的状态
    /*private synchronized void checkIntersect2(TradeInfo prevTradeInfo, TradeInfo tradeInfo) {
        TradeInfo.KDJ prevKdj = prevTradeInfo.getKdj();
        TradeInfo.KDJ kdj = tradeInfo.getKdj();

        //j值在极限区过
        if (prevKdj.getJ() > 100 || prevKdj.getJ() < 0) {
            kdj.hasUltra = true;
        }

        //刚离开超卖区
        if (kdj.getD() >= 25 && kdj.getD() < 40){
            if (kdj.hasIntersect || kdj.continueIntersectTimes > 0) {
                mCanBuy = true;
                if (kdj.hasUltra || kdj.continueUltraTimes > 0) {
                    mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH;
                } else {
                    mLevel = InitAppConstant.FORECAST_LEVEL_BULLISH;
                }
            } else {
                mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL_BULLISH;
            }
        } else if (kdj.getD() < 60){
            mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL;
        } else if (kdj.getD() < 25 && kdj.getJ() < 25) {    //超卖区
            //判断两线是否右侧交叉
            if ((kdj.getK() >= prevKdj.getK())
                    && ToolMath.isSegmentsIntersection(0, prevKdj.getK(), 1, kdj.getK(), 0, prevKdj.getD(), 1, kdj.getD())) {
                kdj.hasIntersect = true;

                ToolLog.d("intersect：" + prevKdj.toString() + "  cur:" + kdj.toString());
            }
        }  else if (kdj.getJ() >= 75 && kdj.getD() >= 75) {     //超买区
            //判断两线是否右侧交叉
            if ((kdj.getK() <= prevKdj.getK()) && ToolMath.isSegmentsIntersection(0, prevKdj.getK(), 1, kdj.getK(), 0, prevKdj.getD(), 1, kdj.getD())) {
                kdj.hasIntersect = true;

                ToolLog.d("intersect：" + prevKdj.toString() + "  cur:" + kdj.toString());
            }
        } else {    //刚离开超买区
            if (kdj.hasIntersect || kdj.continueIntersectTimes > 0) {
                mCanBuy = true;
                if (kdj.hasUltra || kdj.continueUltraTimes > 0) {
                    mLevel = InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH;
                } else {
                    mLevel = InitAppConstant.FORECAST_LEVEL_BEARISH;
                }
            } else {
                mLevel = InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH;
            }

        }

        //更新当前的历史数据
        if (!kdj.isUpdated) {
            kdj.isUpdated = true;
            kdj.continueIntersectTimes = prevKdj.hasIntersect ? (prevKdj.continueIntersectTimes + 1) : 0;
            kdj.continueUltraTimes = prevKdj.hasUltra ? (prevKdj.continueUltraTimes + 1) : 0;
        }

        tradeInfo.setKdj(kdj);
    }*/


}
