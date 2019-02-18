package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.utils.ToolLog;
import com.example.finance.tradestrategy.utils.ToolMath;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 * 指数平滑移动平均线
 * 快速线为DIF,慢速线为DEA,
 *
 */

public enum MACDInd{
    INSTANCE;

    protected static final String TAG = "MACDInd";

    //macd选择的三个时间周期
    private final int   EMA1 = 12;  //快速移动平均值
    private final int   EMA2 = 26;  //慢速移动
    private final int   DEA = 9;    //差离值

    //公式中使用的前后周期日
    private final int   EMA1_PRE = EMA1 - 1;
    private final int   EMA1_AFT = EMA1 + 1;
    private final int   EMA2_PRE = EMA2 - 1;
    private final int   EMA2_AFT = EMA2 + 1;
    private final int   DEA_PRE = DEA - 1;
    private final int   DEA_AFT = DEA + 1;


    //计算数据
    public static class MACDTmp {
        //默认mEma1和mEma2的值是第一条数据的close值
        public float mEma1 = 0;
        public float mEma2 = 0;
        public float mDea = 0;

        public MACDTmp() {
        }

        public MACDTmp(MACDTmp macdTmp) {
            this.mEma1 = macdTmp.mEma1;
            this.mEma2 = macdTmp.mEma2;
            this.mDea = macdTmp.mDea;
        }

        @Override
        public String toString() {
            return "MACDTmp{" +
                    "mEma1=" + mEma1 +
                    ", mEma2=" + mEma2 +
                    ", mDea=" + mDea +
                    '}';
        }
    }

    private synchronized void computeMACD(MACDTmp tmp, List<TradeInfo> values, int start, int end) {
        float diff = 0, macd = 0;
        TradeInfo tradeInfo = null;
        for (int i = start; i < end; ++i) {
            tradeInfo = values.get(i);

            tmp.mEma1 = (tmp.mEma1 * EMA1_PRE + tradeInfo.getClose() * 2) / EMA1_AFT;
            tmp.mEma2 = (tmp.mEma2 * EMA2_PRE + tradeInfo.getClose() * 2) / EMA2_AFT;

            diff = tmp.mEma1 - tmp.mEma2;
            tmp.mDea = (tmp.mDea * DEA_PRE + diff * 2 ) / DEA_AFT;
            macd = (diff - tmp.mDea) * 2;
            tradeInfo.setMacd(new TradeInfo.MACD(tmp.mDea, diff, macd));
//
//            ToolLog.d("MACDInd", "computeMACD", tradeInfo.toString() + " " + tradeInfo.getMacd().toString());
        }

        if (null != tradeInfo) {
            checkIntersect(values.get(end - 2), tradeInfo);

            //计算是否出现拐点
            checkDiffOverTurn(values.get(end - 2), tradeInfo);

            ToolLog.d("MACDInd", "computeMACD", tradeInfo.toString() + " " + tradeInfo.getMacd().toString());
        }
    }

    public void computeMACDHistory(MACDTmp tmp, List<TradeInfo> values) {
        //默认mEma1和mEma2的值是第一条数据的close值
        if (0 < values.size()) {
            TradeInfo tradeInfo = values.get(0);
            tmp.mEma1 = tmp.mEma2 = tradeInfo.getClose();
        }
        computeMACD(tmp, values, 0, values.size());
    }

    public void computeMACDNew(MACDTmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 1) {
            return;
        }
        computeMACD(tmp, values, len - 1, len);
    }

    private void checkDiffOverTurn(TradeInfo preTradeInfo, TradeInfo tradeInfo) {
        TradeInfo.MACD preMacd = preTradeInfo.getMacd(), macd = tradeInfo.getMacd();

        float precision = 0;
        macd.angle = macd.getDiff() - preMacd.getDiff();
        //前一个点和当前点的angle正负相反（或者值为0），表示到达了顶点
        macd.isKeyPoint = (preMacd.angle >= precision && macd.angle <= precision)
                            || (preMacd.angle <= precision && macd.angle >= precision);
    }

    private void checkIntersect(TradeInfo preTradeInfo, TradeInfo tradeInfo) {
        TradeInfo.MACD preMacd = preTradeInfo.getMacd(), macd = tradeInfo.getMacd();
        //交点判断
        macd.hasIntersect = ToolMath.isSegmentsIntersection(0, preMacd.getDiff(), 1, macd.getDiff(), 0, preMacd.getDea(), 1, macd.getDea());

        //距离交点+1
        macd.distance = preMacd.distance + 1;
        //当前没有交点，当前点保存preMacd前两交点距离
        // 当前有交点，则保存该点与前一个交点距离，并且，恢复distance距离为0
        if (!macd.hasIntersect) {
            macd.preDistance = preMacd.preDistance;
        } else {
            macd.preDistance = macd.distance;
            macd.distance = 0;
        }
    }

    private void getText(int feature) {
        switch (feature) {
            case InitAppConstant.INTERSECT_COINCIDE_UP:
                ToolLog.e(TAG, "indicator", "重合向上");
                break;
            case InitAppConstant.INTERSECT_COINCIDE_DOWN:
                ToolLog.e(TAG, "indicator", "重合向下");
                break;
            case InitAppConstant.INTERSECT_COINCIDE_HORI:
                ToolLog.e(TAG, "indicator", "重合水平");
                break;
            case InitAppConstant.INTERSECT_LEFT_UP_UP:
                ToolLog.e(TAG, "indicator", "相交后都向上");
                break;
            case InitAppConstant.INTERSECT_LEFT_UP_DOWN:
                ToolLog.e(TAG, "indicator", "相交后快上慢下");
                break;
            case InitAppConstant.INTERSECT_LEFT_DOWN_UP:
                ToolLog.e(TAG, "indicator", "相交后快下慢上");
                break;
            case InitAppConstant.INTERSECT_LEFT_DOWN_DOWN:
                ToolLog.e(TAG, "indicator", "相交后都下");
                break;
            case InitAppConstant.INTERSECT_RIGHT_UP_UP:
                ToolLog.e(TAG, "indicator", "相交前都上");
                break;
            case InitAppConstant.INTERSECT_RIGHT_UP_DOWN:
                ToolLog.e(TAG, "indicator", "相交前快上慢下");
                break;
            case InitAppConstant.INTERSECT_RIGHT_DOWN_UP:
                ToolLog.e(TAG, "indicator", "相交前快下慢上");
                break;
            case InitAppConstant.INTERSECT_RIGHT_DOWN_DOWN:
                ToolLog.e(TAG, "indicator", "相交前都下");
                break;
            case InitAppConstant.INTERSECT_NO:
                ToolLog.e(TAG, "indicator", "无相交水平");
                break;
            case InitAppConstant.INTERSECT_NO_UP_UP:
                ToolLog.e(TAG, "indicator", "无相交都上");
                break;
            case InitAppConstant.INTERSECT_NO_UP_DOWN:
                ToolLog.e(TAG, "indicator", "无相交快上慢下");
                break;
            case InitAppConstant.INTERSECT_NO_DOWN_UP:
                ToolLog.e(TAG, "indicator", "无相交快下慢上");
                break;
            case InitAppConstant.INTERSECT_NO_DOWN_DOWN:
                ToolLog.e(TAG, "indicator", "无相交都下");
                break;
        }
    }

//    http://investexcel.net/make-winning-trades-macd/
//
//    https://zhuanlan.zhihu.com/p/27966892
//    http://www.investopedia.com/articles/trading/08/macd-stochastic-double-cross.asp

}
