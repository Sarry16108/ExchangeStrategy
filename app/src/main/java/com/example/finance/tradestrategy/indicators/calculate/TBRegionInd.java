package com.example.finance.tradestrategy.indicators.calculate;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.util.List;

/**
 * Created by Administrator on 2017/9/5.
 * 顶底区间：top-bottom-region
 *  用于计算变动上下边沿
 * 目标是查出脱离稳定区域的方向
 * 计算方式：股票有效值为open和close，处于蜡烛实体上部的相加求平均就是top，同理，下部相加求平均就是bot。
 */

public enum TBRegionInd {
    INSTANCE;

    protected static final String TAG = "TBRegionInd";

    //计算平均值得周期
    public static final int M1 = 5;
    public static final int M2 = 10;
    public static final int M3 = 20;
    public static final int M4 = 30;

    private final int M = M2;
    private final float EXP = 0.0002f;
    private final float TOP_EXP = 1 + EXP;
    private final float BOT_EXP = 1 - EXP;

    //中间值存储区域
    public static class TBRegionTmp {
        public float top = 0;    //上部
        public float bot = 0;    //下部

        public TBRegionTmp() {
        }

        public TBRegionTmp(TBRegionTmp tbRegionTmp) {
            this.top = tbRegionTmp.top;
            this.bot = tbRegionTmp.bot;
        }

        @Override
        public String toString() {
            return "TBRegionTmp{" +
                    "bot=" + bot +
                    ", top=" + top +
                    '}';
        }
    }


    //算法计算区域
    private synchronized void computeTBRegion(TBRegionTmp tmp, List<TradeInfo> values, int start, int end) {
        TradeInfo tradeInfo = null;
        for (int i = start; i < end; ++i) {
            tradeInfo = values.get(i);
            TradeInfo.TBRegion tbRegion = tradeInfo.getTbRegion();

            // TODO: 2017/9/5 是否包含open==close再定
            if (tradeInfo.getOpen() >= tradeInfo.getClose()) {
                tmp.top += tradeInfo.getOpen();
                tmp.bot += tradeInfo.getClose();
            } else {
                tmp.top += tradeInfo.getClose();
                tmp.bot += tradeInfo.getOpen();
            }

            if (i >= M) {
                TradeInfo firstInfo = values.get(i - M);
                if (firstInfo.getOpen() >= firstInfo.getClose()) {
                    tmp.top -= firstInfo.getOpen();
                    tmp.bot -= firstInfo.getClose();
                } else {
                    tmp.top -= firstInfo.getClose();
                    tmp.bot -= firstInfo.getOpen();
                }

                tbRegion.setTop(tmp.top / M * TOP_EXP);
                tbRegion.setBot(tmp.bot / M * BOT_EXP);
            } else {
                tbRegion.setTop(tmp.top / (i + 1) * TOP_EXP);
                tbRegion.setBot(tmp.bot / (i + 1) * BOT_EXP);
            }

            tradeInfo.setTbRegion(tbRegion);

            ToolLog.d(TAG, "computeTBRegion", tradeInfo.toString() + " " + tbRegion.toString());
        }

    }


    public void computeTBRegionHistory(TBRegionTmp tmp, List<TradeInfo> values) {
        computeTBRegion(tmp, values, 0, values.size());
    }

    public void computeTBRegionNew(TBRegionTmp tmp, List<TradeInfo> values) {
        int len = values.size();
        if (len < 1) {
            return;
        }
        computeTBRegion(tmp, values, len - 1, len);
    }



}
