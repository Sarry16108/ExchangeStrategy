package com.example.finance.tradestrategy.indicators.calculate;

import android.support.v4.util.ArrayMap;

import com.example.finance.tradestrategy.baseui.BaseActivity;
import com.example.finance.tradestrategy.entity.AnalyzeTmpData;
import com.example.finance.tradestrategy.entity.StockInfo;
import com.example.finance.tradestrategy.entity.StockStrategy;
import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.utils.ToolData;
import com.example.finance.tradestrategy.utils.ToolLog;
import com.example.finance.tradestrategy.utils.ToolMath;
import com.example.finance.tradestrategy.utils.ToolTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/7/13.
 * 相较于AnalyzeInd类的测试类
 */

public enum AnalyzeIndTest {
    INSTANCE;
    private final String TAG = "AnalyzeInd";

    //为了减少每次获得数据后的遍历计算，保存一些中间值，放在这里是为了减少map.get()及map.containKey()判断
    private Map<String, AnalyzeTmpData> mAnalyTmpData = new ArrayMap<>(10);
    //记录当前分析的结果，但是由于不利于以后的扩展，所以，采用每次调用checkResult遍历的方式
//    private Map<String, AnalyzeResult>  mAnalyzeResult = new ArrayMap<>(10);

    public Set<Integer>     mStrategyTypes = new HashSet<>(6);
    /**
     *
     * @param type          分钟类型
     * @param historyInfo   历史记录数据
     * @param tradeUpdate   最新获取更新数据
     * @param period        当前请求的时间段
     * @param stockStrategy
     * @return
     */
    public synchronized long analyzeStockInfoResult(BaseActivity activity, int type, StockInfo historyInfo, TradeInfo tradeUpdate,
                                                    Long period, StockStrategy stockStrategy) {
        stockStrategy.setPeriodType(type);

        //判断历史记录中最后一条数据和当前最新数据
        List<TradeInfo> historyTrades = null;

        //如果等于最后一条，同一个时间段，则更新，否则添加历史记录末尾。
        //如果是新的时间段，则将tmpData修改，否则，都是在上一时间段基础上进行修改且不保存。
        AnalyzeTmpData analyzeTmpData = mAnalyTmpData.get(tradeUpdate.getSymbol());
        AnalyzeTmpData.TmpData tmpData = null;

        switch (type) {
            case InitAppConstant.MINUTE_1:
                historyTrades = historyInfo.getItems();
                tmpData = analyzeTmpData.getM1();
                break;
            case InitAppConstant.MINUTE_5:
                historyTrades = historyInfo.getItems5();
                tmpData = analyzeTmpData.getM5();
                break;
            case InitAppConstant.MINUTE_15:
                historyTrades = historyInfo.getItems15();
                tmpData = analyzeTmpData.getM15();
                break;
            case InitAppConstant.MINUTE_30:
                historyTrades = historyInfo.getItems30();
                tmpData = analyzeTmpData.getM30();
                break;
            case InitAppConstant.MINUTE_60:
                historyTrades = historyInfo.getItems60();
                tmpData = analyzeTmpData.getM60();
                break;
            default:
                break;
        }
        if (null == historyTrades) {
            return period;
        }

        int lastIndex = historyTrades.size() - 1;
        TradeInfo hisLastInfo = historyTrades.get(lastIndex);

        if (tradeUpdate.getTime() == hisLastInfo.getTime()) {
            tmpData = new AnalyzeTmpData.TmpData(tmpData);
        } else {
            historyTrades.add(tradeUpdate);
        }

        //各种指标计算，因为计算耗时，最好是一次更新执行一次。
        analyzeNewIndex(tradeUpdate.getSymbol(), historyTrades, tmpData);
        stockStrategy.setClose(tradeUpdate.getClose());
        checkResult(type, tradeUpdate.getTime(),  historyInfo, stockStrategy);

        //todo:测试时对结果进行判断
        //添加通知和记录
//        if (stockStrategy.isBuy() || stockStrategy.isSell()) {
//            ToolNotification.getInstance().showNotification(stockStrategy);
//            activity.packMsgAndSend(MessageId.FORECAST_RECORD_ADD, stockStrategy);
//        }

        return period;
    }

    /**
     * 缩减数据，当数据量达到上限值时候，删除到下限值。
     * @param historyInfo
     */
    private void dataCompaction(StockInfo historyInfo) {
        ToolData.dataCompactionFromHead(historyInfo.getItems(), 200, 120);
        ToolData.dataCompactionFromHead(historyInfo.getItems5(), 200, 120);
        ToolData.dataCompactionFromHead(historyInfo.getItems15(), 200, 120);
    }

    /**
     * 所有的指标联合分析，都在这里进行，然后保存入stockStrategy中进行显示。
     * @param type
     * @param stockStrategy
     * @param , prevTradeInfo , tradeInfo
     */
    private void checkResult(int type, long serverTime, StockInfo historyInfo, StockStrategy stockStrategy) {
        //清空策略信息
        stockStrategy.resetBuy();
        stockStrategy.resetSell();


        TradeInfo tradeInfo = null, preTradeInfo = null, preThirdTradeInfo = null, preFourTradeInfo = null;
        List<TradeInfo> tradeInfoList = null;
        switch (type) {
            case InitAppConstant.MINUTE_1:
                List<TradeInfo> tradeInfos = historyInfo.getItems();
                tradeInfoList = tradeInfos;
                int size = tradeInfos.size();
                tradeInfo = tradeInfos.get(size - 1);
                preTradeInfo = tradeInfos.get(size - 2);
                preThirdTradeInfo = tradeInfos.get(size - 3);
                preFourTradeInfo = tradeInfos.get(size - 4);
                break;
            case InitAppConstant.MINUTE_5:
                List<TradeInfo> tradeInfos5 = historyInfo.getItems5();
                tradeInfoList = tradeInfos5;
                int sizeM5 = tradeInfos5.size();
                tradeInfo = tradeInfos5.get(sizeM5 - 1);
                preTradeInfo = tradeInfos5.get(sizeM5 - 2);
                preThirdTradeInfo = tradeInfos5.get(sizeM5 - 3);
                preFourTradeInfo = tradeInfos5.get(sizeM5 - 4);
                break;
            case InitAppConstant.MINUTE_15:
                List<TradeInfo> tradeInfos15 = historyInfo.getItems15();
                tradeInfoList = tradeInfos15;
                int sizeM15 = tradeInfos15.size();
                tradeInfo = tradeInfos15.get(sizeM15 - 1);
                preTradeInfo = tradeInfos15.get(sizeM15 - 2);
                preThirdTradeInfo = tradeInfos15.get(sizeM15 - 3);
                preFourTradeInfo = tradeInfos15.get(sizeM15 - 4);
                break;
            case InitAppConstant.MINUTE_30:
                List<TradeInfo> tradeInfos30 = historyInfo.getItems30();
                tradeInfoList = tradeInfos30;
                int sizeM30 = tradeInfos30.size();
                tradeInfo = tradeInfos30.get(sizeM30 - 1);
                preTradeInfo = tradeInfos30.get(sizeM30 - 2);
                preThirdTradeInfo = tradeInfos30.get(sizeM30 - 3);
                preFourTradeInfo = tradeInfos30.get(sizeM30 - 4);
                break;
            case InitAppConstant.MINUTE_60:
                List<TradeInfo> tradeInfos60 = historyInfo.getItems60();
                tradeInfoList = tradeInfos60;
                int sizeM60 = tradeInfos60.size();
                tradeInfo = tradeInfos60.get(sizeM60 - 1);
                preTradeInfo = tradeInfos60.get(sizeM60 - 2);
                preThirdTradeInfo = tradeInfos60.get(sizeM60 - 3);
                preFourTradeInfo = tradeInfos60.get(sizeM60 - 4);
                break;
        }

        //策略执行
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_UTRAL_MACD)) {
            strategyKdkMacd(stockStrategy, tradeInfo, preTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT)) {
            strategyKdkIntersect(stockStrategy, tradeInfo, preTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION)) {
            strategyKdjIntersectDirection(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION2)) {
            strategyKdjIntersectDirection2(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION3)) {
            strategyKdjIntersectDirection3(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT)) {
            strategyBollMacd(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT2)) {
            strategyBollMacd2(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT3)) {
            strategyBollMacd3(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT4)) {
            strategyBollMacd4(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT5)) {
            strategyBollMacd5(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN)) {
            strategyMacdDiffOverturn(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN2)) {
            strategyMacdDiffOverturn2(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }

        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN3)) {
            strategyMacdDiffOverturn3(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN4)) {
            strategyMacdDiffOverturn4(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN5)) {
            strategyMacdDiffOverturn5(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MACD_DISTANCE_INTERSECT)) {
            strategyMacdDistance(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_5_10_20_INTERSECT)) {
            strategyMa5_10_20Intersect(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_MACD_OVERTURN)) {
            strategyMaMacdOverturn(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_DIRECTION)) {
            strategyMaMacdIntersectSameDirection(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_SIDE)) {
            strategyMaMacdIntersectSameSide(stockStrategy, tradeInfoList, tradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT)) {
            strategyMaIntersectSameIntersect(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_20_40)) {
            strategyMa20_40Intersect(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_10_20)) {
            strategyMa10_20Intersect(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT)) {
            strategyMa5Overturn_10Intersect(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_BOLL_GOLEN_SPERATOR)) {
            strategyCloseBollGolden(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT)) {
            strategyMa5BollGoldenIntersect(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START)) {
            strategyMa5BollGoldenIntersectStart(stockStrategy, tradeInfo, preTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN)) {
            strategyMa5_20BollGolden(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_CANDLE_BOLL_GOLEN)) {
            strategyCandleBollGolden(stockStrategy, tradeInfoList, tradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_BOLL_MD)) {
            strategyBollMd(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo);
        }
        //第七类
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_EMA_1_2_INTERSECT)) {
            strategyEma1_2Intersect(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo);
        }
        if (mStrategyTypes.contains(InitAppConstant.STRATEGORY_TYPE_SELL_MA_10_OVERTURN)) {
            sellPointMa10(stockStrategy, tradeInfoList, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo, 0.00002f);
        }
    }

    /**
     * 策略方式1：InitAppConstant.STRATEGORY_TYPE_UTRAL_MACD
     * 看空：kdj的j>100且macd在强势区，后一时间点的kdj值和macd都小于前一时间点的值。
     * 看多：kdj的j<0且macd在弱势区，后一时间点的kdj值和macd都大于前一时间点的值。
     */
    private void strategyKdkMacd(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo) {
        TradeInfo.KDJ preKdj = preTradeInfo.getKdj();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();

        //判断看空
        if (preKdj.getJ() >= 100 && preMacd.getMacd() > 0) {
            TradeInfo.KDJ kdj = tradeInfo.getKdj();
            TradeInfo.MACD macd = tradeInfo.getMacd();

            if (ToolMath.isFloatEqualSmall(kdj.getK(), preKdj.getK())
//                    && ToolMath.isFloatEqualSmall(kdj.getD(), preKdj.getD())
                    && ToolMath.isFloatEqualSmall(kdj.getJ(), preKdj.getJ())
                    && ToolMath.isFloatEqualSmall(macd.getMacd(), preMacd.getMacd())) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_MACD);
            }

//            ToolLog.d("strategyKdkMacd1:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + " macd:" + macd.toString() + "  preMacd:" + preMacd.toString() + "  bullbear:" + tradeInfo.getBuySell());

        } else if (preKdj.getJ() < 0 && preMacd.getMacd() < 0) {
            TradeInfo.KDJ kdj = tradeInfo.getKdj();
            TradeInfo.MACD macd = tradeInfo.getMacd();

            if (ToolMath.isFloatEqualSmall(preKdj.getK(), kdj.getK())
//                    && ToolMath.isFloatEqualSmall(preKdj.getD(), kdj.getD())
                    && ToolMath.isFloatEqualSmall(preKdj.getJ(), kdj.getJ())
                    && ToolMath.isFloatEqualSmall(preMacd.getMacd(), macd.getMacd())) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_MACD);
            }

//            ToolLog.d("strategyKdkMacd1:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + " macd:" + macd.toString() + "  preMacd:" + preMacd.toString() + "  bullbear:" + tradeInfo.getBuySell());
        }
    }

    /***
     * kdj 差值abs(k-d)差值> 10
     * 每天开始时候，策略1范围扩大，特征类似就提示。
     */
    /**
     * 策略方式2：InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT
     * 看空：kdj在超买区，k>80,d>80,j>80，在离开超买区时有交点，离开即交点在此点前
     * 看多：kdj在超卖区，k<20,d<20,j<20，在离开超卖区时有交点
     */
    private void strategyKdkIntersect(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo) {
        TradeInfo.KDJ preKdj = preTradeInfo.getKdj();
        TradeInfo.KDJ kdj = tradeInfo.getKdj();

        //离开超买区判断
        if (preKdj.getJ() >= 80 && preKdj.getK() >= 80 && kdj.getJ() < 80 && kdj.getK() < 80 && ToolMath.isFloatEqualSmall(kdj.getD(), preKdj.getD())) {

            //前一时间段有交点，当前没有或者当前有，但是d，k值不一样（即不是右侧相交）
            if ((preKdj.hasIntersect && !kdj.hasIntersect && kdj.getD() - kdj.getJ() > 10) || (kdj.hasIntersect && !ToolMath.isFloatEqual(kdj.getD(), kdj.getK()))) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT);
            }

//            ToolLog.d("strategyKdkMacd2:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + "  bullbear:" + tradeInfo.getBuySell());
        } else if (preKdj.getJ() <= 20 && preKdj.getK() <= 20 && kdj.getJ() > 20 && kdj.getK() > 20 && ToolMath.isFloatEqualBig(kdj.getD(), preKdj.getD())
                && kdj.getJ() - kdj.getD() > 10) {

            if ((preKdj.hasIntersect && !kdj.hasIntersect) || (kdj.hasIntersect && !ToolMath.isFloatEqual(kdj.getD(), kdj.getK()))) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT);
            }

//            ToolLog.d("strategyKdkMacd2:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + "  bullbear:" + tradeInfo.getBuySell());
        }
    }


    /**
     * 策略方式3：InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION
     * 看空：kdj在超买区，k>80,j>80，在离开超买区时有交点，离开即交点在此点前
     * 看多：kdj在超卖区，k<20,j<20，在离开超卖区时有交点
     */
    private void strategyKdjIntersectDirection(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo) {
        TradeInfo.KDJ preThirdKdj = preThirdTradeInfo.getKdj();
        TradeInfo.KDJ preKdj = preTradeInfo.getKdj();
        TradeInfo.KDJ kdj = tradeInfo.getKdj();
        String xLabel = tradeInfo.getXLabel();

        if (preKdj.getJ() >= 75 && preKdj.getK() >= 75 && kdj.getJ() < 80 && kdj.getK() < 80 && kdj.getD() < 80) {
            if ((preThirdKdj.hasIntersect || preKdj.hasIntersect) && !kdj.hasIntersect) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION);
            }

//            ToolLog.d("strategyKdkMacd3:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + "  preThirdKdj:" + preThirdKdj.toString() + "  bullbear:" + tradeInfo.getBuySell());
        } else if (preKdj.getJ() <= 25 && preKdj.getK() <= 25 && kdj.getJ() > 20 && kdj.getK() > 20 && kdj.getD() > 20) {
            if ((preThirdKdj.hasIntersect || preKdj.hasIntersect) && !kdj.hasIntersect) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION);
            }

//            ToolLog.d("strategyKdkMacd3:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + "  preThirdKdj:" + preThirdKdj.toString() + "  bullbear:" + tradeInfo.getBuySell());
        }
    }

    /**
     * 策略方式3：InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION
     * 看空：kdj在超买区，k>80,d>80,j>80，在离开超买区时有交点，离开即交点在此点前
     * 看多：kdj在超卖区，k<20,d<20,j<20，在离开超卖区时有交点
     */
    private void strategyKdjIntersectDirection2(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo) {
        TradeInfo.KDJ preThirdKdj = preThirdTradeInfo.getKdj();
        TradeInfo.KDJ preKdj = preTradeInfo.getKdj();
        TradeInfo.KDJ kdj = tradeInfo.getKdj();

        if (preKdj.getJ() >= 75 && preKdj.getK() >= 75 && preKdj.getD() >= 75 && kdj.getJ() < 80 && kdj.getK() < 80 && kdj.getD() < 80
                && kdj.getD() - kdj.getJ() > 10) {
            if ((preThirdKdj.hasIntersect || preKdj.hasIntersect) && !kdj.hasIntersect) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION2);
            }

//            ToolLog.d("strategyKdkMacd3:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + "  preThirdKdj:" + preThirdKdj.toString() + "  bullbear:" + tradeInfo.getBuySell());
        } else if (preKdj.getJ() <= 25 && preKdj.getK() <= 25 && preKdj.getD() <= 25 && kdj.getJ() > 20 && kdj.getK() > 20 && kdj.getD() > 20
                && kdj.getJ() - kdj.getD() > 10) {
            if ((preThirdKdj.hasIntersect || preKdj.hasIntersect) && !kdj.hasIntersect) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION2);
            }

//            ToolLog.d("strategyKdkMacd3:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + "  preThirdKdj:" + preThirdKdj.toString() + "  bullbear:" + tradeInfo.getBuySell());
        }
    }

    /**
     * 条件严格的策略2
     * 策略方式4：InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION
     * 看空：kdj在超买区，k>80,d>80,j>80，在离开超买区时有交点，离开即交点在此点前
     * 看多：kdj在超卖区，k<20,d<20,j<20，在离开超卖区时有交点
     */
    private void strategyKdjIntersectDirection3(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo) {
        TradeInfo.KDJ preThirdKdj = preThirdTradeInfo.getKdj();
        TradeInfo.KDJ preKdj = preTradeInfo.getKdj();
        TradeInfo.KDJ kdj = tradeInfo.getKdj();

        if ((preKdj.hasIntersect || preThirdKdj.hasIntersect) && !kdj.hasIntersect) {

            //看空
            if (ToolMath.isFloatEqualSmall(kdj.getK(), preKdj.getK()) && ToolMath.isFloatEqualSmall(preKdj.getK(), preThirdKdj.getK())
                    && ToolMath.isFloatEqualSmall(kdj.getJ(), preKdj.getJ()) && ToolMath.isFloatEqualSmall(preKdj.getJ(), preThirdKdj.getJ())
                    && kdj.getJ() < 75 && preThirdKdj.getJ() < 90 && preThirdKdj.getJ() > 80
                    && (preKdj.getD() >= 80 || preThirdKdj.getD() >= 80)
                    && kdj.getD() - kdj.getJ() > 10) {

                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION3);

            } else if (ToolMath.isFloatEqualBig(kdj.getK(), preKdj.getK()) && ToolMath.isFloatEqualBig(preKdj.getK(), preThirdKdj.getK())
                && ToolMath.isFloatEqualBig(kdj.getJ(), preKdj.getJ()) && ToolMath.isFloatEqualBig(preKdj.getJ(), preThirdKdj.getJ())
                && preThirdKdj.getJ() > 10  && preThirdKdj.getJ() < 20 && kdj.getJ() > 25
                && (preKdj.getD() <= 20 || preThirdKdj.getD() <= 20)
                && kdj.getJ() - kdj.getD() > 10) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION3);
            }

//            ToolLog.d("strategyKdkMacd3:" + tradeInfo.getXLabel() + " kdj:" + kdj.toString() + "  preKdj:" + preKdj.toString()
//                    + "  preThirdKdj:" + preThirdKdj.toString() + "  bullbear:" + tradeInfo.getBuySell());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第二类boll macd **********************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 策略4：结合boll的强弱区及macd的金死叉来判断买点
     * 超买：在boll的强势区，macd发生了死叉，且交后macd两条线都向下
     * 超卖：在boll的弱势区，macd发生了金叉，且交后macd两条线都向上
     * @param tradeInfo
     * @param preTradeInfo
     */
    private void strategyBollMacd(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo prThirdeTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();

        String date = tradeInfo.getXLabel();
        if (macd.hasIntersect) {
            TradeInfo.Boll boll = tradeInfo.getBoll();
            TradeInfo.Boll preBoll = preTradeInfo.getBoll();
            TradeInfo.Boll preThirdBoll = prThirdeTradeInfo.getBoll();

            //看空
            if (preMacd.getMacd() > 0 && macd.getMacd() < 0
                    && preBoll.getB() > 0.5 && preThirdBoll.getB() > 0.5
                    && (boll.getB() > 0.5 || (tradeInfo.getOpen() > boll.getMb() && tradeInfo.getClose() < boll.getMb() && tradeInfo.getClose() > boll.getDn()))
                            /*&& ((preBoll.getB() >= 0.55 && preThirdBoll.getB() >= 0.55)
                                || (preTradeInfo.getHigh() > preBoll.getMb() && preTradeInfo.getLow() < preBoll.getMb() && preThirdBoll.getB() >= 0.55))*/) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT);

            //看多
            } else if (macd.getMacd() > 0 && preMacd.getMacd() < 0
                    && preBoll.getB() < 0.5 && preThirdBoll.getB() < 0.5
                    && ((boll.getB() < 0.5 || (tradeInfo.getClose() > boll.getMb() && tradeInfo.getOpen() < boll.getMb() && tradeInfo.getClose() < boll.getUp()))
                            /*&& ((preBoll.getB() <= 0.45 && preThirdBoll.getB() <= 0.45)
                                ||(preTradeInfo.getHigh() > preBoll.getMb() && preTradeInfo.getLow() < preBoll.getMb() && preThirdBoll.getB() <= 0.45))*/)) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT);
            }
        }
    }
    /**
     * 策略4.1：结合macd的金死叉来判断买点，加上对应的close价不断下降/上升。（boll的强弱区及）
     * 超买：在boll的强势区，macd发生了死叉，且交后macd两条线都向下
     * 超卖：在boll的弱势区，macd发生了金叉，且交后macd两条线都向上
     * @param tradeInfo
     * @param preTradeInfo
     */
    private void strategyBollMacd2(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();


        if (macd.hasIntersect) {
            TradeInfo.Boll boll = tradeInfo.getBoll();
            TradeInfo.Boll preBoll = preTradeInfo.getBoll();
            TradeInfo.Boll preThirdBoll = preThirdTradeInfo.getBoll();

            float preThirdAverage = (preThirdTradeInfo.getOpen() + preThirdTradeInfo.getClose()) / 2;
            float preAverage = (preTradeInfo.getOpen() + preTradeInfo.getClose()) / 2;
            float curAverage = (tradeInfo.getOpen() + tradeInfo.getClose()) / 2;

            //看空，对应的close价不断下降
            if (preMacd.getMacd() > 0 && macd.getMacd() < 0
                    && ToolMath.isFloatEqualSmall(curAverage, preAverage) && ToolMath.isFloatEqualSmall(preAverage, preThirdAverage)
                  ) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT2);

                //看多，对应的close价不断上升
            } else if (macd.getMacd() > 0 && preMacd.getMacd() < 0
                    && ToolMath.isFloatEqualBig(curAverage, preAverage) && ToolMath.isFloatEqualBig(preAverage, preThirdAverage)
                    ) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT2);
            }
        }
    }

    /**
     * 策略4.3
     * macd交点前后，价格在boll间变化
     */
    private void strategyBollMacd3(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();


        if (macd.hasIntersect) {
            TradeInfo.MACD preMacd = preTradeInfo.getMacd();
            TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();

            TradeInfo.Boll boll = tradeInfo.getBoll();
            TradeInfo.Boll preBoll = preTradeInfo.getBoll();
            TradeInfo.Boll preThirdBoll = preThirdTradeInfo.getBoll();

            //空：（boll强且macd 两线分别>0）或者（boll弱且macd两线<0）下交叉
            //多：（boll强且macd 两线分别>0）或者（boll弱且macd两线<0）上交叉

            if ((ToolMath.isFloatBig(preThirdTradeInfo.getOpen(), preThirdBoll.getMb()) && ToolMath.isFloatBig(preThirdTradeInfo.getClose(), preThirdBoll.getMb())
                    && ToolMath.isFloatBig(preTradeInfo.getOpen(), preBoll.getMb()) && ToolMath.isFloatBig(preTradeInfo.getClose(), preBoll.getMb())
                    && ToolMath.isFloatBig(tradeInfo.getOpen(), boll.getMb()) && ToolMath.isFloatBig(tradeInfo.getClose(), boll.getMb())
                        && preThirdMacd.getDiff() > 0 && preThirdMacd.getDea() > 0 && preMacd.getDiff() > 0 && preMacd.getDea() > 0 && macd.getDiff() > 0 && macd.getDea() > 0)
                || (ToolMath.isFloatSmall(preThirdTradeInfo.getOpen(), preThirdBoll.getMb()) && ToolMath.isFloatSmall(preThirdTradeInfo.getClose(), preThirdBoll.getMb())
                    && ToolMath.isFloatSmall(preTradeInfo.getOpen(), preBoll.getMb()) && ToolMath.isFloatSmall(preTradeInfo.getClose(), preBoll.getMb())
                    && ToolMath.isFloatSmall(tradeInfo.getOpen(), boll.getMb()) && ToolMath.isFloatSmall(tradeInfo.getClose(), boll.getMb())
                    && preThirdMacd.getDiff() < 0 && preThirdMacd.getDea() < 0 && preMacd.getDiff() < 0 && preMacd.getDea() < 0 && macd.getDiff() < 0 && macd.getDea() < 0)) {

                //下交叉
                if (preMacd.getMacd() > 0 && macd.getMacd() < 0 && ToolMath.isFloatSmall(macd.getDiff(), macd.getDea())) {
                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT3);
                } else if (macd.getMacd() > 0 && preMacd.getMacd() < 0 && ToolMath.isFloatBig(macd.getDiff(), macd.getDea())) {
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT3);
                }
            }
        }
    }

    /**
     *  4.4加入了相邻几个时间段均价沿方向上的变动
     */
    private void strategyBollMacd4(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();

        if (macd.hasIntersect) {
            TradeInfo.MACD preMacd = preTradeInfo.getMacd();
            TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();

            TradeInfo.Boll boll = tradeInfo.getBoll();
            TradeInfo.Boll preBoll = preTradeInfo.getBoll();
            TradeInfo.Boll preThirdBoll = preThirdTradeInfo.getBoll();


            float preThirdAver = (preThirdTradeInfo.getOpen() + preThirdTradeInfo.getClose()) / 2;
            float preAver = (preTradeInfo.getOpen() + preTradeInfo.getClose()) / 2;
            float aver = (tradeInfo.getOpen() + tradeInfo.getClose()) / 2;

            //空：（boll强且macd 两线分别>0）或者（boll弱且macd两线<0）下交叉
            //多：（boll强且macd 两线分别>0）或者（boll弱且macd两线<0）上交叉

            if ((ToolMath.isFloatBig(preThirdTradeInfo.getOpen(), preThirdBoll.getMb()) && ToolMath.isFloatBig(preThirdTradeInfo.getClose(), preThirdBoll.getMb())
                    && ToolMath.isFloatBig(preTradeInfo.getOpen(), preBoll.getMb()) && ToolMath.isFloatBig(preTradeInfo.getClose(), preBoll.getMb())
                    && ToolMath.isFloatBig(tradeInfo.getOpen(), boll.getMb()) && ToolMath.isFloatBig(tradeInfo.getClose(), boll.getMb())
                    && preThirdMacd.getDiff() > 0 && preThirdMacd.getDea() > 0 && preMacd.getDiff() > 0 && preMacd.getDea() > 0 && macd.getDiff() > 0 && macd.getDea() > 0)
                    || (ToolMath.isFloatSmall(preThirdTradeInfo.getOpen(), preThirdBoll.getMb()) && ToolMath.isFloatSmall(preThirdTradeInfo.getClose(), preThirdBoll.getMb())
                    && ToolMath.isFloatSmall(preTradeInfo.getOpen(), preBoll.getMb()) && ToolMath.isFloatSmall(preTradeInfo.getClose(), preBoll.getMb())
                    && ToolMath.isFloatSmall(tradeInfo.getOpen(), boll.getMb()) && ToolMath.isFloatSmall(tradeInfo.getClose(), boll.getMb())
                    && preThirdMacd.getDiff() < 0 && preThirdMacd.getDea() < 0 && preMacd.getDiff() < 0 && preMacd.getDea() < 0 && macd.getDiff() < 0 && macd.getDea() < 0)) {

                //下交叉
                if (preMacd.getMacd() > 0 && macd.getMacd() < 0
                        && ToolMath.isFloatSmall(macd.getDiff(), macd.getDea())
                        && ToolMath.isFloatEqualBig(preThirdAver, preAver) && ToolMath.isFloatEqualBig(preAver, aver)) {
                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT3);
                } else if (macd.getMacd() > 0 && preMacd.getMacd() < 0
                        && ToolMath.isFloatBig(macd.getDiff(), macd.getDea())
                        && ToolMath.isFloatEqualSmall(preThirdAver, preAver) && ToolMath.isFloatEqualSmall(preAver, aver)) {
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT3);

                }
            }
        }
    }

    private void strategyBollMacd5(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();

        if (macd.hasIntersect) {
            TradeInfo.MACD preMacd = preTradeInfo.getMacd();
            TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();

            TradeInfo.Boll boll = tradeInfo.getBoll();
            TradeInfo.Boll preBoll = preTradeInfo.getBoll();
            TradeInfo.Boll preThirdBoll = preThirdTradeInfo.getBoll();
            TradeInfo.Boll preFourBoll = preFourTradeInfo.getBoll();

            //空：（boll前两个open>upper,当前close<dnner）下交叉
            //多：（boll前两个open<dnner,当前close>upper）上交叉

            if ((ToolMath.isFloatEqualBig(preThirdTradeInfo.getOpen(), preThirdBoll.getUp()) || ToolMath.isFloatEqualBig(preTradeInfo.getOpen(), preBoll.getUp())
                    || ToolMath.isFloatEqualBig(preFourTradeInfo.getOpen(), preFourBoll.getUp())) && boll.getB() <= 0.1
                    && preMacd.getMacd() > 0 && macd.getMacd() < 0) {

                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT5);

                //上交叉
            } else if ((preFourBoll.getB() <= 0.1 || preThirdBoll.getB() <= 0.1 || preBoll.getB() <= 0.1) && ToolMath.isFloatEqualBig(tradeInfo.getClose(), boll.getUp())
                    && macd.getMacd() > 0 && preMacd.getMacd() < 0) {

                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT5);
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第三类   macd diff overturn***********************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////


    //历史数据回顾：发现对3和5对uvxy准确率比较高
    /**
     * macd出现拐点，且拐点符合特定要求
     */
    private void strategyMacdDiffOverturn(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();
        TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();
        TradeInfo.MACD preFourMacd = preFourTradeInfo.getMacd();

        //有拐点出现
        if (preMacd.isKeyPoint && !macd.isKeyPoint) {

            //空方向，开口向下
            if (preFourMacd.angle > 0 && preThirdMacd.angle > 0 && macd.angle < 0 && preMacd.angle < 0) {
                //diff在dea线以上且每次angle差多少
                if (((macd.getDiff() - preMacd.getDea() > 0.01)
                        || (macd.getDiff() - preThirdMacd.getDiff() <= -0.01))
                        && ToolMath.isFloatEqualBig(macd.getDiff(), macd.getDea())
                        && ToolMath.isFloatEqualBig(preFourMacd.getDiff(), preFourMacd.getDea())) {
                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN);
                }

            //多方向，开口向上
            } else if (preFourMacd.angle < 0 && preThirdMacd.angle < 0 && preMacd.angle > 0 && macd.angle > 0){
                if (macd.getDiff() - preThirdMacd.getDiff() >= 0.01
                        && ToolMath.isFloatEqualSmall(macd.getDiff(), macd.getDea())
                        && ToolMath.isFloatEqualSmall(preFourMacd.getDiff(), preFourMacd.getDea())) {
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN);
                }
            }
        }

    }


    /**
     * macd出现拐点，且拐点出现在boll的强势区域（b>0.7或者弱势b<0.3）
     */
    private void strategyMacdDiffOverturn2(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();
        TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();
        TradeInfo.MACD preFourMacd = preFourTradeInfo.getMacd();

        //有拐点出现
        if (preMacd.isKeyPoint && !macd.isKeyPoint) {

//            //空方向，开口向下
//            if (preFourMacd.angle > 0 && preThirdMacd.angle > 0 && macd.angle < 0 && preMacd.angle < 0) {
//                //diff在dea线以上且每次angle差多少
//                if (((macd.getDiff() - preMacd.getDea() > 0.01)
//                        || (macd.getDiff() - preThirdMacd.getDiff() <= -0.01))
//                        && ToolMath.isFloatEqualBig(macd.getDiff(), macd.getDea())
//                        && ToolMath.isFloatEqualBig(preFourMacd.getDiff(), preFourMacd.getDea())) {
//                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN2);
//                }
//
//                //多方向，开口向上
//            } else if (preFourMacd.angle < 0 && preThirdMacd.angle < 0 && preMacd.angle > 0 && macd.angle > 0){
//                if (macd.getDiff() - preThirdMacd.getDiff() >= 0.01
//                        && ToolMath.isFloatEqualSmall(macd.getDiff(), macd.getDea())
//                        && ToolMath.isFloatEqualSmall(preFourMacd.getDiff(), preFourMacd.getDea())) {
//                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN2);
//                }
//            }
        }

    }

    /**
     * macd出现拐点
     * 空：开口向下，preMacd>0.06 && preThirdMacd.getMacd() > 0.1，diff都在dea上
     * 多：开口向上，preMacd < -0.11 && preThirdMacd < -0.15，diff都在dea下
     */
    private void strategyMacdDiffOverturn3(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();
        TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();
        TradeInfo.MACD preFourMacd = preFourTradeInfo.getMacd();

        //有拐点出现
        if (preMacd.isKeyPoint && !macd.isKeyPoint) {

            //空方向，开口向下
            if (preFourMacd.angle > 0 && preThirdMacd.angle > 0 && macd.angle < 0 && preMacd.angle < 0
                    && (preMacd.getMacd() > 0.06 && preThirdMacd.getMacd() > 0.1)
                    && (ToolMath.isFloatBig(macd.getDiff(), macd.getDea()) && ToolMath.isFloatBig(preMacd.getDiff(), preMacd.getDea()) && ToolMath.isFloatBig(preThirdMacd.getDiff(), preThirdMacd.getDea()) && ToolMath.isFloatBig(preFourMacd.getDiff(), preFourMacd.getDea()))) {
                //diff在dea线以上且每次angle差多少
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN3);

                //多方向，开口向上
            } else if (preFourMacd.angle < 0 && preThirdMacd.angle < 0 && preMacd.angle > 0 && macd.angle > 0
                    && (preMacd.getMacd() < -0.11 && preThirdMacd.getMacd() < -0.15)
                    && (ToolMath.isFloatSmall(macd.getDiff(), macd.getDea()) && ToolMath.isFloatSmall(preMacd.getDiff(), preMacd.getDea()) && ToolMath.isFloatSmall(preThirdMacd.getDiff(), preThirdMacd.getDea()) && ToolMath.isFloatSmall(preFourMacd.getDiff(), preFourMacd.getDea()))){
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN3);
            }
        }
    }

    /**
     * macd出现拐点，且拐点距离前一个macd交点有一定距离
     */
    private void strategyMacdDiffOverturn4(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();
        TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();
        TradeInfo.MACD preFourMacd = preFourTradeInfo.getMacd();

        //有拐点出现
        if (preMacd.isKeyPoint && !macd.isKeyPoint && preMacd.distance > 15) {

            //空方向，开口向下
            if (preFourMacd.angle > 0 && preThirdMacd.angle > 0 && macd.angle < 0 && preMacd.angle < 0) {
                //diff在dea线以上且每次angle差多少
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN4);

                //多方向，开口向上
            } else if (preFourMacd.angle < 0 && preThirdMacd.angle < 0 && preMacd.angle > 0 && macd.angle > 0){
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN4);
            }
        }
    }

    /**
     * macd出现拐点，且交易量会有相应的变化
     */
    private void strategyMacdDiffOverturn5(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();
        TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();
        TradeInfo.MACD preFourMacd = preFourTradeInfo.getMacd();

        //有拐点出现
        if (preMacd.isKeyPoint && !macd.isKeyPoint ) {

            //空方向，开口向下：交易量变少
            if (preFourMacd.angle > 0 && preThirdMacd.angle > 0 && macd.angle < 0 && preMacd.angle < 0
                    && tradeInfo.getVolume() < preTradeInfo.getVolume() && tradeInfo.getVolume() < preThirdTradeInfo.getVolume()
                    && (ToolMath.isFloatBig(macd.getDiff(), macd.getDea()) && ToolMath.isFloatBig(preMacd.getDiff(), preMacd.getDea()) && ToolMath.isFloatBig(preThirdMacd.getDiff(), preThirdMacd.getDea()) && ToolMath.isFloatBig(preFourMacd.getDiff(), preFourMacd.getDea()))) {
                //diff在dea线以上且每次angle差多少
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN5);

                //多方向，开口向上：交易量变多
            } else if (preFourMacd.angle < 0 && preThirdMacd.angle < 0 && preMacd.angle > 0 && macd.angle > 0
                    && tradeInfo.getVolume() > preTradeInfo.getVolume() && tradeInfo.getVolume() > preThirdTradeInfo.getVolume()
                    && (ToolMath.isFloatSmall(macd.getDiff(), macd.getDea()) && ToolMath.isFloatSmall(preMacd.getDiff(), preMacd.getDea()) && ToolMath.isFloatSmall(preThirdMacd.getDiff(), preThirdMacd.getDea()) && ToolMath.isFloatSmall(preFourMacd.getDiff(), preFourMacd.getDea()))){
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN5);
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第四类macd distance*******************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * macd交点距离判断，两点距离太近就忽略
     */
    private void strategyMacdDistance(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo) {
        TradeInfo.MACD macd = tradeInfo.getMacd();
        TradeInfo.MACD preMacd = preTradeInfo.getMacd();
        TradeInfo.MACD preThirdMacd = preThirdTradeInfo.getMacd();
        TradeInfo.MACD preFourMacd = preFourTradeInfo.getMacd();


        if (macd.hasIntersect) {

            if ((macd.preDistance < preMacd.preDistance && macd.preDistance < 10)
                    || (macd.distance < macd.preDistance)) {
                return;
            }

            //看空
            if (preMacd.getMacd() > 0 && macd.getMacd() < 0) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DISTANCE_INTERSECT);

                //看多
            } else if (macd.getMacd() > 0 && preMacd.getMacd() < 0) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DISTANCE_INTERSECT);
            }
        } else {
//            if (macd.preDistance < preMacd.preDistance && macd.distance > macd.preDistance) {
//                //看空
//                if (preMacd.getMacd() > 0 && macd.getMacd() < 0) {
//                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DISTANCE_INTERSECT);
//
//                    //看多
//                } else if (macd.getMacd() > 0 && preMacd.getMacd() < 0) {
//                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MACD_DISTANCE_INTERSECT);
//                }
//            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第五类macd distance*******************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 5
     * 交点有先后顺序，从后向前，先有5_20，再有5_10。
     * 空：向下相交5-》10-》20，
     * 多：
     * @param stockStrategy
     * @param tradeInfoList
     */
    private void strategyMa5_10_20Intersect(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList, TradeInfo tradeInfo, TradeInfo preTradeInfo) {

        if (!(tradeInfo.getMa().ma5_20 || preTradeInfo.getMa().ma5_20)
                || (tradeInfo.getMa().ma5_20 && !preTradeInfo.getMa().ma5_20)) {
            return;
        }

        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();

        int length = tradeInfoList.size();
        int isBuyBull = 0;  //1：买多，2：买空

        //空
        if (ma.getMa1() < ma.getMa3() && ma.getMa3() < ma.getMa2() && ma.getMa1() < preMa.getMa1()
                && tradeInfo.getEntityTop() < ma.getMa1()) {
            isBuyBull = 2;

            //多
        } else if (ma.getMa1() > ma.getMa3() && ma.getMa3() > ma.getMa2() && ma.getMa1() > preMa.getMa1()
                && tradeInfo.getEntityBot() > ma.getMa1()) {
            isBuyBull = 1;
        }

        //此时就返回吧，方向不明确
        if (0 == isBuyBull) {
            return;
        }

        //往前遍历，验证5_10与5_20是不是一致
        TradeInfo.MA maAfter = tradeInfo.getMa();
        for (int i = length - 1; i >= length - 8; i--) {
            TradeInfo hisInfo = tradeInfoList.get(i);
            if (hisInfo == tradeInfo) {
                continue;
            }
            if (preMa.ma5_20 && hisInfo == preTradeInfo) {
                maAfter = preTradeInfo.getMa();
                continue;
            }

            TradeInfo.MA hisMa = hisInfo.getMa();

            //如果ma5_20前最近一次是ma5_20或者ma10_20交点，那么忽略该次信号
            if ((hisMa.ma5_20 || hisMa.ma10_20) && !hisMa.ma5_10) {
                return;
            }

            if (hisMa.ma5_10) {
                //空:ma5 < ma10，top <= ma10
                if (isBuyBull == 2 && hisMa.getMa1() < hisMa.getMa2()
                        && hisInfo.getEntityTop() <= hisMa.getMa2()) {
                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_10_20_INTERSECT);
                    break;

                    //多:ma5 > ma10, bot >= ma10
                } else if (isBuyBull == 1 && hisMa.getMa1() > hisMa.getMa2()
                        && hisInfo.getEntityBot() >= hisMa.getMa2()) {
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_10_20_INTERSECT);
                    break;
                }

                //交点之间的ma5，ma10必须符合要求，否则忽略
            } else {
//空：ma5，ma10往前不断增大，close < ma5否则就停止
                if (isBuyBull == 2 && !(maAfter.getMa1() < hisMa.getMa1() && maAfter.getMa2() < hisMa.getMa2() && hisInfo.getEntityBot() <= hisMa.getMa3())) {
                    break;

                    //多：ma5，ma10往前不断减小
                } else if (isBuyBull == 1 && !(maAfter.getMa1() > hisMa.getMa1() && maAfter.getMa2() > hisMa.getMa2() && hisInfo.getEntityBot() >= hisMa.getMa3())) {
                    break;
                }

                //把当前的ma保留，以和前一个进行比较
                maAfter = hisMa;
            }
        }
    }

    /**5.1
     *  在ma5_10与ma5_20两个交点之间，出现了ma10的拐点
     *  看多：ma10向上拐
     *  看空：ma10向下拐
     */
    private void strategyMaMacdOverturn(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList, TradeInfo tradeInfo, TradeInfo preTradeInfo) {

        if (!tradeInfo.getMa().ma5_20) {
            return;
        }

        //拐弯方向：roundDir < 0：向下拐  0 < roundDir：向上拐
        float roundDir = tradeInfo.getMa().getMa2() - preTradeInfo.getMa().getMa2();
        if (roundDir <= 0.0000003 && roundDir >= -0.0000003) {      //在这个区间相当于重合，不处理
            return;
        }

        int length = tradeInfoList.size();


        float tmpValue = roundDir;
        for (int i = length - 2; i >= length - 10; i--) {
            TradeInfo hisInfo = tradeInfoList.get(i);
            TradeInfo preHisInfo = tradeInfoList.get(i - 1);
            TradeInfo.MA hisMa = hisInfo.getMa();
            TradeInfo.MA preHisMa = preHisInfo.getMa();

            if (hisMa.ma5_20 || hisMa.ma10_20) {
                return;
            }

            //将此判断放在下部分拐点判断前，发现提示点更多（更多的也没有错误）
            if (hisMa.ma5_10) {
                //看空，还要满足两个交点处，ma5的位置比较
                if (roundDir < 0 && hisMa.getMa1() > tradeInfo.getMa().getMa1()) {
                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_OVERTURN);

                //看多
                } else if (roundDir > 0 && hisMa.getMa1() < tradeInfo.getMa().getMa1()) {
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_OVERTURN);
                }

                break;
            }


            float curValue = hisMa.getMa2() - preHisMa.getMa2();
            //向下拐，curValue总是小于tmpValue
            if (roundDir < 0) {
                if (tmpValue > curValue/* + 0.00001*/) {
                    break;
                }

                //向上拐，curValue总是大于tmpValue
            } else if (roundDir > 0) {
                if (tmpValue < curValue/* - 0.00001*/) {
                    break;
                }
            }

            tmpValue = curValue;
        }

    }

//    暂停使用
    /**5.2
     * 1分钟图，5_10分钟图两个相隔不远的交点在20线一侧，且最新的交点方向与20线相同，距离10_20距离不远。
     * 看空：5_10交点都在20线以下，且最新交点后方向与20 同样向下，close向下
     * 看多：5_10交点都在20线以上，且最新交点后方向与20 同样向上，close向上
     */
    private void strategyMaMacdIntersectSameDirection(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
                                                      TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo thirdTradeInfo) {
        if (!tradeInfo.getMa().ma5_10) {
            return;
        }

        TradeInfo.MA ma = tradeInfo.getMa();
        //交点在20上还是下
        boolean isDown20 = ToolMath.isFloatSmall(ma.getMa1(), ma.getMa3(), 0) && ToolMath.isFloatSmall(ma.getMa2(), ma.getMa3(), 0);
        boolean isUp20 = ToolMath.isFloatBig(ma.getMa1(), ma.getMa3(), 0) && ToolMath.isFloatBig(ma.getMa2(), ma.getMa3(), 0);

        //如果交点不能在20线上
        if (!isDown20 && !isUp20) {
            return;
        }
        final int range = 7;
        int end = tradeInfoList.size() - 1;

        for (int i = end; i > end - range; --i) {
            TradeInfo tmpTrade = tradeInfoList.get(i);
            if (tradeInfo == tmpTrade) {
                continue;
            }

            //中间的所有点都要和ma的一致，都在20线上或下
            TradeInfo.MA tmpMa = tmpTrade.getMa();
            boolean isTmpDown20 = ToolMath.isFloatSmall(tmpMa.getMa1(), tmpMa.getMa3(), 0) && ToolMath.isFloatSmall(tmpMa.getMa2(), tmpMa.getMa3(), 0);
            if (isDown20 && !isTmpDown20) {
                return;
            }
            boolean isTmpUp20 = ToolMath.isFloatBig(tmpMa.getMa1(), tmpMa.getMa3(), 0) && ToolMath.isFloatBig(tmpMa.getMa2(), tmpMa.getMa3(), 0);
            if (isUp20 && !isTmpUp20) {
                return;
            }

            //找到前面的一个5_10交点
            if (tmpMa.ma5_10) {

                TradeInfo.MA preMa = preTradeInfo.getMa();
                TradeInfo.MA thirdMa = thirdTradeInfo.getMa();

                //看空，方向向下
                //5_10交点在20线下
                //close递减
                if (isTmpDown20
                        && ToolMath.isFloatSmall( ma.getMa3(), preMa.getMa3(), 0) && ToolMath.isFloatSmall(preMa.getMa3(), thirdMa.getMa3(), 0)
                        && ToolMath.isFloatEqualSmall( ma.getMa1(), preMa.getMa1(), 0) && ToolMath.isFloatEqualSmall(preMa.getMa1(), thirdMa.getMa1(), 0)
                        && ToolMath.isFloatEqualSmall( ma.getMa2(), preMa.getMa2(), 0) && ToolMath.isFloatEqualSmall(preMa.getMa2(), thirdMa.getMa2(), 0)
                        && ToolMath.isFloatEqualSmall(tradeInfo.getClose(), preTradeInfo.getClose()) && ToolMath.isFloatEqualSmall(preTradeInfo.getClose(), thirdTradeInfo.getClose(), 0)) {

                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_DIRECTION);
                    break;

                    //看空，方向向上
                    //5_10交点在20线上
                    //close递升
                } else if (isTmpUp20
                        && ToolMath.isFloatBig( ma.getMa3(), preMa.getMa3(), 0) && ToolMath.isFloatBig(preMa.getMa3(), thirdMa.getMa3(), 0)
                        && ToolMath.isFloatEqualBig( ma.getMa1(), preMa.getMa1(), 0) && ToolMath.isFloatEqualBig(preMa.getMa1(), thirdMa.getMa1(), 0)
                        && ToolMath.isFloatEqualBig( ma.getMa2(), preMa.getMa2(), 0) && ToolMath.isFloatEqualBig(preMa.getMa2(), thirdMa.getMa2(), 0)
                        && ToolMath.isFloatEqualBig(tradeInfo.getClose(), preTradeInfo.getClose()) && ToolMath.isFloatEqualBig(preTradeInfo.getClose(), thirdTradeInfo.getClose(), 0)) {

                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_DIRECTION);
                    break;
                }
            }
        }
    }

    /**5.3
     * 1分钟图，5_10分钟图交点在macd交点相邻（4个内）
     * 看空: 5_10在20线上面，5_10交点处的open/close值在20线上，macd向下交。
     * 看多：5_10在20线下面，5_10交点处的open/close值在20线上，macd向上交。
     */
    private void strategyMaMacdIntersectSameSide(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList, TradeInfo tradeInfo) {
        if (!(tradeInfo.getMa().ma5_10 || tradeInfo.getMacd().hasIntersect)) {
            return;
        }

        int end = tradeInfoList.size() - 1;
        //macd和ma5_10交点对应的tradeInfo
        TradeInfo macdTradeInfo = null;
        TradeInfo maTradeInfo = null;

        //ma5_10交点前两个个点
        TradeInfo maPreTradeInfo = null;
        TradeInfo maThirdTradeInfo = null;

        //macd交点是否在ma5_10交点前，是则true
        boolean isMacdIntersectFirst = false;
        final int range = 3;

        for (int i = end; i > end - range; --i) {
            TradeInfo tmpTrade = tradeInfoList.get(i);

            //设置ma5_10交点前的tradeInfo
            if (null == maThirdTradeInfo && null != maTradeInfo && null != maPreTradeInfo) {
                maThirdTradeInfo = tmpTrade;
            }
            if (null == maPreTradeInfo && null != maTradeInfo) {
                maPreTradeInfo = tmpTrade;
            }

            if (null == maTradeInfo && tmpTrade.getMa().ma5_10) {
                maTradeInfo = tmpTrade;
            }

            if (null == macdTradeInfo && tmpTrade.getMacd().hasIntersect) {
                macdTradeInfo = tmpTrade;
                isMacdIntersectFirst = null == maTradeInfo;
            }

            if (null != macdTradeInfo && null != maTradeInfo) {
                break;
            }
        }

        if (null == macdTradeInfo || null == maTradeInfo) {
            return;
        }

        if (null == maPreTradeInfo) {
            maPreTradeInfo = tradeInfoList.get(end - range);
        }
        if (null == maThirdTradeInfo) {
            maThirdTradeInfo = tradeInfoList.get(end - range - 1);
        }

        TradeInfo.MA ma = maTradeInfo.getMa();
        TradeInfo.MACD macd = macdTradeInfo.getMacd();
        //ma处的open/close必须大于ma20值
        if ((ToolMath.isFloatEqualBig(maTradeInfo.getClose(), ma.getMa3()) || ToolMath.isFloatEqualBig(maTradeInfo.getOpen(), ma.getMa3()))) {
            //看空
            //此处的ma之前open/close必须也大于20日值
            if (maPreTradeInfo.getEntityBot() >= maPreTradeInfo.getMa().getMa3()
                    && maThirdTradeInfo.getEntityBot() >= maThirdTradeInfo.getMa().getMa3()
                    && ToolMath.isFloatBig(ma.getMa1(), ma.getMa3()) && ToolMath.isFloatBig(ma.getMa2(), ma.getMa3())
                    && (macd.hasIntersect && macd.getMacd() < -0.00001)) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_SIDE);

                //看多
            } else if (ToolMath.isFloatSmall(ma.getMa1(), ma.getMa3()) && ToolMath.isFloatSmall(ma.getMa2(), ma.getMa3())
                    && (macd.hasIntersect && macd.getMacd() > 0.00001)) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_SIDE);
            }
        }
    }

    /**5.4——STRATEGORY_TYPE_MA_SAME_INTERSECT
     * 单位时间多点相交
     *  1、5_10_20交于一点
     *  2、5_10与5_20
     *  3、5_10与10_20
     *  4、5_20与10_20
     *  至少同时两个交点
     * 看空：相交后向下，5<10<20，相交前，5>10>20
     * 看多：相交后向上，5>10>20，相交前，5<10<20
     */
    private void strategyMaIntersectSameIntersect(StockStrategy stockStrategy, TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo thirdTradeInfo) {
        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();
        TradeInfo.MA thirdMa = thirdTradeInfo.getMa();

//        if (ma.ma5_20 && ma.ma5_10 && ma.ma10_20) {
//            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_MACD);   //1
//        } else if (ma.ma5_20 && ma.ma5_10) {
//            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT);  //1.1       出现次数比较多
//        } else if (ma.ma5_10 && ma.ma10_20) {
//            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION);    //1.2
//        } else if (ma.ma5_20 && ma.ma10_20) {
//            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION2);   // 1.3
//        }

        //看空：相交后向下，5<10<20，相交前，5>10>20
        //看多：相交后向上，5>10>20，相交前，5<10<20
        if (ma.ma5_20 && ma.ma5_10 && ma.ma10_20) {
            //看空
            if (ma.getMa1() < ma.getMa2() && ma.getMa2() < ma.getMa3()
                    && preMa.getMa1() > preMa.getMa2() && preMa.getMa2() > preMa.getMa3()
                    && preMa.getMa2() > ma.getMa2() && preMa.getMa3() > ma.getMa3()) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);

                //多
            } else if (ma.getMa1() > ma.getMa2() && ma.getMa2() > ma.getMa3()
                    && preMa.getMa1() < preMa.getMa2() && preMa.getMa2() < preMa.getMa3()
                    && preMa.getMa2() < ma.getMa2() && preMa.getMa3() < ma.getMa3()) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);
            }

            //ma5穿过ma10，ma20，ma的ma20与ma10差值小于preMa的
        } else if (ma.ma5_20 && ma.ma5_10 && Math.abs(ma.getMa2() - ma.getMa3()) < Math.abs(preMa.getMa2() - preMa.getMa3()) - 0.00001) {
            //看多
            if (ma.getMa1() > preMa.getMa1()) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);
                //看空
            } else if (ma.getMa1() < preMa.getMa1()) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);
            }
        }
    }

    /**5.5
     * 1分钟gbp为分析对象
     *  ma20和40的相交后，根据线的方向来决定多空。
     *  空：20下交40，且交点处close&open低于20，往前3个点open&close逐渐降低的，至少不一次往前增高。
     *  多：20上交40，且交点处close&open高于20，往前2个点open&close > 40，再往前则open/close > 40。
     */
    private void strategyMa20_40Intersect(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
                                          TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo thirdTradeInfo, TradeInfo fourTradeInfo) {
        //没有交点返回
        if (!(tradeInfo.getMa().ma20_40 || preTradeInfo.getMa().ma20_40)) {
            return;
        }

        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();
        TradeInfo.MA thirdMa = thirdTradeInfo.getMa();
        TradeInfo.MA fourMa = fourTradeInfo.getMa();


        //看多：交后20 > 40，交前，20 < 40
        //    :之前几个open/close > ma40
        if (ma.getMa3() > ma.getMa4() && preMa.getMa3() < preMa.getMa4()
                && tradeInfo.getEntityBot() > ma.getMa3()
                && preTradeInfo.getEntityBot() > preMa.getMa4()
                && thirdTradeInfo.getEntityBot() > thirdMa.getMa4()
                && fourTradeInfo.getEntityTop() > fourMa.getMa4()) {
            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_20_40);

            //看空：交后20 < 40，交前，20 > 40
            //    :之前几个open/close < ma40
        } else if (ma.getMa3() < ma.getMa4() && preMa.getMa3() > preMa.getMa4()
                && tradeInfo.getEntityTop() < ma.getMa3()
                && tradeInfo.getEntityTop() <= preTradeInfo.getEntityBot()
                && ((preTradeInfo.getEntityTop() <= thirdTradeInfo.getEntityBot() && thirdTradeInfo.getEntityTop() <= fourTradeInfo.getEntityBot())
                    || (preTradeInfo.getEntityTop() <= thirdTradeInfo.getEntityTop() && thirdTradeInfo.getEntityTop() <= fourTradeInfo.getEntityTop()))) {
            tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_20_40);
        }
    }

    /**
     * 5.6   STRATEGORY_TYPE_MA_INTERSECT_10_20
     * 10与20相交时，
     * 看多：相交后方向都向上，且ma5线(交点前至少4点)一直在往上，交后ma5>ma10>ma20，交前ma5>ma20>ma10
     * 看空：相交后方向都向下，且ma5线（交点前至少4点）一直在往下，交后ma5<ma10<ma20，交前ma5<ma20<ma10
     *
     */
    private void strategyMa10_20Intersect(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
                                          TradeInfo tradeInfo, TradeInfo preTradeInfo) {
        //没有交点返回
        if (!tradeInfo.getMa().ma10_20) {
            return;
        }

        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();

        //1：看多，  2：看空
        int buyType = 0;
        //看多
        if (ma.getMa3() > preMa.getMa3() && ma.getMa2() > preMa.getMa2()
                && ma.getMa1() > ma.getMa2() && ma.getMa2() > ma.getMa3()) {
                buyType = 1;

            //看空
        } else if (ma.getMa3() < preMa.getMa3() && ma.getMa2() < preMa.getMa2()
                && ma.getMa1() < ma.getMa2() && ma.getMa2() < ma.getMa3()) {
            buyType = 2;
        }

        int end = tradeInfoList.size() - 2;
        float latterMa5 = ma.getMa1();
        TradeInfo tmpTradeInfo = null;
        int ma10_20_times = 0;
        for (int i = end; i >= end - 4; i--) {
            tmpTradeInfo = tradeInfoList.get(i);
            TradeInfo.MA tmpMa = tmpTradeInfo.getMa();

            //忽略情况：中间出现了ma10_20交点
            if (tmpMa.ma10_20) {
                ma10_20_times++;
                if (ma10_20_times > 1) {        //已经排除了最开始的end-1点
                    return;
                }
            }


            //多，ma5往前应该逐渐变小
            if (1 == buyType) {
                if (latterMa5 < tmpMa.getMa1()) {
                    return;
                }

                if (tmpMa.ma5_20 && tmpMa.getMa1() > ma.getMa1()) {
                    return;
                }
                //空，ma5往前应该逐渐变大
            } else if (2 == buyType) {
                if (latterMa5 > tmpMa.getMa1()) {
                    return;
                }

                if (tmpMa.ma5_20 && tmpMa.getMa1() < ma.getMa1()) {
                    return;
                }
            }

            latterMa5 = tmpMa.getMa1();
        }

        if (1 == buyType) {
            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_10_20);
        } else if (2 == buyType) {
            tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_10_20);
        }

    }

    /**
     * 5.7 ——STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT
     * ma5与ma10相交前，ma5已经有了一个大拐，交点处存在ma10<0.000005或者交点前ma10已经发生拐点
     * 看空：ma5与ma10相交，ma5在相交前拐点向下
     * 看多：ma5与ma10相交，ma5在相交前拐点向上
     */
    private void strategyMa5Overturn_10Intersect(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
                                                 TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo thirdTradeInfo) {
        //ma5、ma10相交了
        if (!tradeInfo.getMa().ma5_10) {
            return;
        }

        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();
        TradeInfo.MA thirdMa = thirdTradeInfo.getMa();

        //是交叉相交
        int buyType = InitAppConstant.FORECAST_BULL_BEAR;
        //上交了
        if (ma.getMa1() > ma.getMa2() && (preMa.getMa1() < preMa.getMa2() || thirdMa.getMa1() < thirdMa.getMa2())
                && ma.dif1 > 0) {
            buyType = InitAppConstant.FORECAST_BULL_BUY;

            //下交了
        } else if (ma.getMa1() < ma.getMa2() && (preMa.getMa1() > preMa.getMa2()  || thirdMa.getMa1() > thirdMa.getMa2())
                && ma.dif1 < 0) {
            buyType = InitAppConstant.FORECAST_BEAR_BUY;
        } else {
            return;
        }


        //ma10条件
        int end = tradeInfoList.size() - 1;
        TradeInfo afterTrade = tradeInfo;
        TradeInfo.MA afterMa = afterTrade.getMa();

        boolean isMa10 = false;
        if (Math.abs(tradeInfo.getMa().dif2) <= 0.000005 || Math.abs(preTradeInfo.getMa().dif2) <= 0.000005) {
            isMa10 = true;
        } else {
            for (int i = 1; i < 6; i++) {
                TradeInfo tmpTrade = tradeInfoList.get(end - i);
                TradeInfo.MA tmpMa = tmpTrade.getMa();
                if (tmpMa.ma5_10 || tmpMa.ma5_20) {
                    return;
                }

                //上拐
                if (buyType == InitAppConstant.FORECAST_BULL_BUY
                        && ((afterMa.dif1 > 0 && tmpMa.dif1 < 0)
                                || (afterMa.dif1 >= 0 && tmpMa.dif1 < 0)
                                || (afterMa.dif1 > 0 && tmpMa.dif1 <= 0))) {
                    isMa10 = true;
                    break;
                    //下拐
                } else if (buyType == InitAppConstant.FORECAST_BEAR_BUY
                        && ((afterMa.dif1 < 0 && tmpMa.dif1 > 0)
                                || (afterMa.dif1 <= 0 && tmpMa.dif1 > 0)
                                || (afterMa.dif1 < 0 && tmpMa.dif1 >= 0))) {
                    isMa10 = true;
                    break;
                }

                afterMa = tmpMa;
            }
        }

        afterMa = afterTrade.getMa();
        //ma5拐点处的时间点
        int turnIndex = 0;
        for (int i = 1; i < 6; i++) {
            TradeInfo tmpTrade = tradeInfoList.get(end - i);
            TradeInfo.MA tmpMa = tmpTrade.getMa();
            if (tmpMa.ma5_10 || tmpMa.ma5_20) {
                return;
            }

            //上拐
            if (buyType == InitAppConstant.FORECAST_BULL_BUY
                    && ((afterMa.dif1 > 0 && tmpMa.dif1 < 0)
                        || (afterMa.dif1 >= 0 && tmpMa.dif1 < 0)
                        || (afterMa.dif1 > 0 && tmpMa.dif1 <= 0))) {
                turnIndex = i;
                break;
                //下拐
            } else if (buyType == InitAppConstant.FORECAST_BEAR_BUY
                    && ((afterMa.dif1 < 0 && tmpMa.dif1 > 0)
                        || (afterMa.dif1 <= 0 && tmpMa.dif1 > 0)
                        || (afterMa.dif1 < 0 && tmpMa.dif1 >= 0))) {
                turnIndex = i;
                break;
            }

            afterMa = tmpMa;
        }

        //没有拐点
        if (0 == turnIndex) {
            return;
        }

        //拐点以前10个时间没有交点
        for (int i = turnIndex + 1; i < turnIndex + 5; ++i) {
            TradeInfo tmpTrade = tradeInfoList.get(end - i);
            TradeInfo.MA tmpMa = tmpTrade.getMa();
            if (tmpMa.ma5_10 || tmpMa.ma5_20) {
                return;
            }

            //顶点外方向必须一致
            if ((buyType == InitAppConstant.FORECAST_BULL_BUY && tmpMa.dif1 > 0)
                    || (buyType == InitAppConstant.FORECAST_BEAR_BUY && tmpMa.dif1 < 0)) {
                return;
            }
        }

        //满足ma10条件且ma5拐点处距离ma10有一定距离
        TradeInfo.MA tangencyMa = tradeInfoList.get(end - turnIndex).getMa();
        if (isMa10 && Math.abs(tangencyMa.getMa1() - tangencyMa.getMa2()) > 0.00005) {
            if (buyType == InitAppConstant.FORECAST_BULL_BUY) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT);
            } else if (buyType == InitAppConstant.FORECAST_BEAR_BUY) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT);
            }
        }

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第六类ma boll*******************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**有三个分析点：
     * 1、1min:   ma5上穿过0.382和0.618线时，tradeInfo的top或bot都>上0.618线，下穿同。
     * 2、min:  tradeInfo的上1.618>top>=上0.618，下1.618<bot<=下0.618，且high<上1.618，low>下1.618，看其是涨跌定方向
     * 3、ma5穿过0.618和0.382线时，tradeInfo的open/close特征。
     * 4、ma5连续穿过上下0.618和0.382线，
     * 5、top/bot在0.382之间，属于徘徊期。
     * 6、在实体超过1.618后，反向行情（比如出是涨，入是跌）进入1.618后，会持续该态势一段时间
     */
//    audusd:三连涨/三连跌，三点的top-bot的值>=中间点的上下0618差（两个连续点也可以）
//
//    该添加2点、3点穿越上下0618，方向由点的涨跌定，当前面的1点或两点满足，就不考虑当前点
//    考虑是否把boll的md值级别考虑进去
    /**
     * 6——STRATEGORY_TYPE_MA_BOLL_GOLEN_SPERATOR
     * 1min:  tradeInfo的上1.618>top>=上0.618，下1.618<bot<=下0.618，且high<上1.618，low>下1.618，看其是涨跌定方向
     *  preTrade.open != preTrade.Close，因为很容易导致trade超过up/down0618
     * 看多：close > open
     * 看空：close < open
     */
    private void strategyCloseBollGolden(StockStrategy stockStrategy,
                                         TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo thirdTradeInfo) {
        TradeInfo.Boll boll = tradeInfo.getBoll();
        //// TODO: 2017/10/18 将top<up1618，bot>down1618改为high<=up1618,low>=down1618
        if (!(preTradeInfo.getClose() == preTradeInfo.getOpen() && thirdTradeInfo.getClose() == thirdTradeInfo.getOpen())
                && tradeInfo.getEntityTop() >= boll.getGoldenUp0618() && tradeInfo.getEntityTop() <= boll.getGoldenUp1618()
                && tradeInfo.getEntityBot() <= boll.getGoldenDown0618() && tradeInfo.getEntityBot() >= boll.getGoldenDown1618()) {

            //方向和涨跌一致
            //看空
            if (tradeInfo.getOpen() > tradeInfo.getClose()) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_GOLEN_SPERATOR);

                //看涨
            } else if (tradeInfo.getOpen() < tradeInfo.getClose()){
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_GOLEN_SPERATOR);
            }
        }
    }

    /**
     * 6.1—— STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT
     * 1min:   ma5上穿过0.382和0.618线时，tradeInfo的top或bot都>上0.618线，且pre.open与pre.close比较判断下穿同。
     * 看多：ma5先后上穿0.382和0.618线时，tradeInfo的top或bot都>up0.618线，且pre.open < pre.close
     * 看空：ma5先后上穿0.382和0.618线时，tradeInfo的top或bot都 < down0.618线，且pre.open > pre.close
     */
    private void strategyMa5BollGoldenIntersect(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
                                                TradeInfo tradeInfo, TradeInfo preTradeInfo) {
        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();
        TradeInfo.Boll boll = tradeInfo.getBoll();
        TradeInfo.Boll preBoll = preTradeInfo.getBoll();

        //ma5与0.618相交类型，0没有相交，1：上穿up0618,  2：下穿up0618,  3：上穿down0618,  4：下穿down0618
        int intersectType = 0;

        //ma5与上0.618相交
        boolean hasIntersect = ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenUp0618(), 1, boll.getGoldenUp0618());
        if (hasIntersect) {
            if (ma.getMa1() > boll.getGoldenUp0618() && preMa.getMa1() < preBoll.getGoldenUp0618() && preTradeInfo.getOpen() < preTradeInfo.getClose()) {
                intersectType = 1;
            } else if (ma.getMa1() < boll.getGoldenUp0618() && preMa.getMa1() > preBoll.getGoldenUp0618() && preTradeInfo.getOpen() > preTradeInfo.getClose()) {
                intersectType = 2;
            }

            //ma5与下0.618相交
        } else if (ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenDown0618(), 1, boll.getGoldenDown0618())) {
            if (ma.getMa1() > boll.getGoldenDown0618() && preMa.getMa1() < preBoll.getGoldenDown0618() && preTradeInfo.getOpen() < preTradeInfo.getClose()) {
                intersectType = 3;
            } else if (ma.getMa1() < boll.getGoldenDown0618() && preMa.getMa1() > preBoll.getGoldenDown0618() && preTradeInfo.getOpen() > preTradeInfo.getClose()) {
                intersectType = 4;
            }
        }

        //没有符合条件的，不处理
        if (0 == intersectType) {
            return;
        }

        int end = tradeInfoList.size() - 1;
        TradeInfo tmpTrade, tmpPreTrade;

        //往前查找范围为5
        for (int i = end; i > end - 5; i--) {
            tmpTrade = tradeInfoList.get(i);
            tmpPreTrade = tradeInfoList.get(i - 1);
            TradeInfo.MA tmpMa = tmpTrade.getMa();
            TradeInfo.MA tmpPreMa = tmpPreTrade.getMa();
            TradeInfo.Boll tmpBoll = tmpTrade.getBoll();
            TradeInfo.Boll tmpPreBoll = tmpPreTrade.getBoll();

            if (ToolMath.isSegmentsIntersection(0, tmpPreMa.getMa1(), 1, tmpMa.getMa1(), 0, tmpPreBoll.getGoldenUp0382(), 1, tmpBoll.getGoldenUp0382())) {

                //ma5交0618和0382方向都一致
                if (intersectType == 1 && tmpMa.getMa1() > tmpBoll.getGoldenUp0382() && tmpPreMa.getMa1() < tmpPreBoll.getGoldenUp0382()
                        && tradeInfo.getEntityBot() > boll.getGoldenUp0618()) {
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                } else if (intersectType == 3 && tmpMa.getMa1() > tmpBoll.getGoldenDown0382() && tmpPreMa.getMa1() < tmpPreBoll.getGoldenDown0382()
                        && tradeInfo.getEntityBot() > boll.getGoldenUp0618()) {
                    tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                } else if (intersectType == 2 && tmpMa.getMa1() < tmpBoll.getGoldenUp0382() && tmpPreMa.getMa1() > tmpPreBoll.getGoldenUp0382()
                        && tradeInfo.getEntityTop() < boll.getGoldenDown0382()) {
                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                } else if (intersectType == 4 && tmpMa.getMa1() < tmpBoll.getGoldenDown0382() && tmpPreMa.getMa1() > tmpPreBoll.getGoldenDown0382()
                        && tradeInfo.getEntityTop() < boll.getGoldenDown0382()){
                    tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                }

                break;
            }
        }
    }

    /**
     * 6.2——STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START
     * 1min:  ma5在单位时间同时穿过0382和0618线，根据前一点和当前点相对分割线位置判断最初趋向
     * 看多：ma5上穿down0618和down0382，bot>中线，top > up0618, preBot>down0382线，high、low在up/down1618之间
     * 看空：ma5下穿down0618和down0382，top<中线，bot < down0618, preBot<up0382线，high、low在up/down1618之间
     */
    private void strategyMa5BollGoldenIntersectStart(StockStrategy stockStrategy,
                                         TradeInfo tradeInfo, TradeInfo preTradeInfo) {
        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();
        TradeInfo.Boll boll = tradeInfo.getBoll();
        TradeInfo.Boll preBoll = preTradeInfo.getBoll();

        //单位时间穿过两条线
        //值在up/down1618之间
        if (tradeInfo.getHigh() <= boll.getGoldenUp1618() && tradeInfo.getLow() >= boll.getGoldenDown1618()
                && preTradeInfo.getHigh() <= preBoll.getGoldenUp1618() && preTradeInfo.getLow() >= preBoll.getGoldenDown1618()) {
            //看空
            if (ma.getMa1() <= boll.getGoldenUp0382() && preMa.getMa1() >= preBoll.getGoldenUp0618()
                    && tradeInfo.getEntityTop() <= boll.getMb() && tradeInfo.getEntityBot() <= boll.getGoldenDown0618()
                    && preTradeInfo.getEntityTop() <= preBoll.getGoldenUp0382()
                    && ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenUp0618(), 1, boll.getGoldenUp0618())
                    && ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenUp0382(), 1, boll.getGoldenUp0382())) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START);

                //看多
            } else if (ma.getMa1() >= boll.getGoldenDown0382() && preMa.getMa1() <= preBoll.getGoldenDown0618()
                    && tradeInfo.getEntityBot() >= boll.getMb() && tradeInfo.getEntityTop() >= boll.getGoldenUp0618()
                    && preTradeInfo.getEntityBot() >= preBoll.getGoldenDown0382()
                    && ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenDown0618(), 1, boll.getGoldenDown0618())
                    && ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenDown0382(), 1, boll.getGoldenDown0382())) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START);
            }
        }
    }

    /**
     * 6.3——STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN
     * ma5_20相交方向以及，前一时间点的bot/top在boll0618点位的存在来判定买入点
     * 看空：ma5下交ma20，up1618 >= pre.top>= up0618且pre.bot<=down0618
     *      或者 ma5下交ma20，前两点均open>=close，且up1618>=third.top>=up0618且down0618>=pre.bot
     *      （或者ma5下交ma20，前三点均open>=close，且up1618>=fourth.top>=up0618且down0618>=pre.bot三点的发现失误率大）
     * 看多：ma5上交ma20，down1618 <= pre.bot<= down0618且pre.top>=up0618
     *      或者 ma5上交ma20，前两点均open<=close，且down1618<=third.bot<=down0618且up0618<=pre.top
     *      (或者 ma5上交ma20，前三点均open<=close，且down1618<=forth.bot<=down0618且up0618<=pre.top三点的发现失误率大)
     * @param stockStrategy
     * @param tradeInfo
     * @param preTradeInfo
     */
    private void strategyMa5_20BollGolden(StockStrategy stockStrategy,
                                                     TradeInfo tradeInfo, TradeInfo preTradeInfo, TradeInfo thirdTradeInfo, TradeInfo fourthTradeInfo) {
        if (!tradeInfo.getMa().ma5_20) {
            return;
        }

        TradeInfo.MA ma = tradeInfo.getMa();
        TradeInfo.MA preMa = preTradeInfo.getMa();
        TradeInfo.Boll preBoll = preTradeInfo.getBoll();
        TradeInfo.Boll thirdBoll = thirdTradeInfo.getBoll();
        TradeInfo.Boll fourthBoll = fourthTradeInfo.getBoll();

        //看空
        if (ma.getMa1() < ma.getMa3() && preMa.getMa3() < preMa.getMa1()                //上交
                && preTradeInfo.getEntityBot() <= preBoll.getGoldenDown0618()           //共同情况
                && ((preTradeInfo.getEntityTop() <= preBoll.getGoldenUp1618() && preTradeInfo.getEntityTop() >= preBoll.getGoldenUp0618())  //一点情况
                    || (preTradeInfo.getOpen() >= preTradeInfo.getClose() && thirdTradeInfo.getOpen() >= thirdTradeInfo.getClose()       //open>=close
                            && thirdTradeInfo.getEntityTop() <= thirdBoll.getGoldenUp1618() && thirdTradeInfo.getEntityTop() >= thirdBoll.getGoldenUp0618()))   ) {    //两点情况
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN);

            //看多
        } else if (ma.getMa1() > ma.getMa3() && preMa.getMa3() > preMa.getMa1()
                && preTradeInfo.getEntityTop() >= preBoll.getGoldenUp0618()
                && ((preTradeInfo.getEntityBot() >= preBoll.getGoldenDown1618() && preTradeInfo.getEntityBot() <= preBoll.getGoldenDown0618())
                    || (preTradeInfo.getOpen() <= preTradeInfo.getClose() && thirdTradeInfo.getOpen() <= thirdTradeInfo.getClose()
                            && thirdTradeInfo.getEntityBot() >= thirdBoll.getGoldenDown1618() && thirdTradeInfo.getEntityBot() <= thirdBoll.getGoldenDown0618()))) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN);
        }
    }

    /**
     * 6.4——STRATEGORY_TYPE_CANDLE_BOLL_GOLEN
     * 连续两个（或者守卫两个，条件是中间最多允许存在三个都是open==close,否则条件不满足）时间段，涨跌情况相同，up1.382>=top>=up0.618， down1.382<=bot<=down0.618
     * 看多：连续两个(或守卫两个) down1.382 <= preOpen <= down0.618, down0.382 <= preClose <= open <= up0.382, up0.618 <= close <= up1.382
     * 看空：连续两个(或守卫两个) up1.382 >= preOpen >= up0.618, up0.382 >= preClose >= open >= down0.382,  down0.618 >= close >= down1.382
     */
    private void strategyCandleBollGolden(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
                                          TradeInfo tradeInfo) {

        TradeInfo.Boll boll = tradeInfo.getBoll();
        int end = tradeInfoList.size() - 2;
        TradeInfo tmpTrade;
        //0，没有符合条件，1：看空  2：看多
        int buyType = 0;

        //看空
        if (tradeInfo.getOpen() >= boll.getGoldenDown0382()
                && tradeInfo.getClose() >= boll.getGoldenDown1382() && tradeInfo.getClose() <= boll.getGoldenDown0618()) {
                buyType = 1;

            //看多
        } else if (tradeInfo.getOpen() <= boll.getGoldenUp0382()
                && tradeInfo.getClose() <= boll.getGoldenUp1382() && tradeInfo.getClose() >= boll.getGoldenUp0618()){
            buyType = 2;

        }

        //往前查找范围为5
        for (int i = end; i >= end - 4; i--) {
            tmpTrade = tradeInfoList.get(i);

            //中间的open == close不用管。
            if (tmpTrade.getOpen() == tradeInfo.getClose()) {
                continue;
            }

            TradeInfo.Boll tmpBoll = tmpTrade.getBoll();


            //空判断
            if (1 == buyType
                    && tmpTrade.getOpen() <= tmpBoll.getGoldenUp1382() && tmpTrade.getOpen() >= tmpBoll.getGoldenUp0618()
                    && tmpTrade.getClose() <= tmpBoll.getGoldenUp0382() && tmpTrade.getClose() >= tradeInfo.getOpen()) {
                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_CANDLE_BOLL_GOLEN);
                break;

                //多判断
            } else if (2 == buyType
                    && tmpTrade.getOpen() >= tmpBoll.getGoldenDown1382() && tmpTrade.getOpen() <= tmpBoll.getGoldenDown0618()
                    && tmpTrade.getClose() >= tmpBoll.getGoldenDown0382() && tmpTrade.getClose() <= tradeInfo.getOpen()) {
                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_CANDLE_BOLL_GOLEN);
                break;
            }
        }
    }
//    private void strategyCandleBollGolden(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
//                                          TradeInfo tradeInfo) {
//
//        TradeInfo.Boll boll = tradeInfo.getBoll();
//        int end = tradeInfoList.size() - 2;
//        TradeInfo tmpTrade;
//        //0，没有符合条件，1：看空  2：看多
//        int buyType = 0;
//
//        //看空
//        if (tradeInfo.getOpen() > tradeInfo.getClose() && tradeInfo.getOpen() >= boll.getGoldenDown0618()
//                && tradeInfo.getClose() >= boll.getGoldenDown1382() && tradeInfo.getClose() <= boll.getGoldenDown0618()) {
//            buyType = 1;
//
//            //看多
//        } else if (tradeInfo.getOpen() <= boll.getGoldenUp0618() && tradeInfo.getOpen() < tradeInfo.getClose()
//                && tradeInfo.getClose() <= boll.getGoldenUp1382() && tradeInfo.getClose() >= boll.getGoldenUp0618()){
//            buyType = 2;
//
//        }
//
//        //往前查找范围为5
//        for (int i = end; i >= end - 4; i--) {
//            tmpTrade = tradeInfoList.get(i);
//
//            //中间的open == close不用管。
//            if (tmpTrade.getOpen() == tradeInfo.getClose()) {
//                continue;
//            }
//
//            TradeInfo.Boll tmpBoll = tmpTrade.getBoll();
//
//
//            //空判断
//            if (1 == buyType
//                    && tmpTrade.getOpen() <= tmpBoll.getGoldenUp1382() && tmpTrade.getOpen() >= tmpBoll.getGoldenUp0618()
//                    && tmpTrade.getClose() <= tmpBoll.getGoldenUp0618() && tmpTrade.getClose() >= tradeInfo.getOpen()) {
//                tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_CANDLE_BOLL_GOLEN);
//                break;
//
//                //多判断
//            } else if (2 == buyType
//                    && tmpTrade.getOpen() >= tmpBoll.getGoldenDown1382() && tmpTrade.getOpen() <= tmpBoll.getGoldenDown0618()
//                    && tmpTrade.getClose() >= tmpBoll.getGoldenDown0618() && tmpTrade.getClose() <= tradeInfo.getOpen()) {
//                tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_CANDLE_BOLL_GOLEN);
//                break;
//            }
//        }
//    }

    //分析点：当前点相对于前一点的位置判断

    /**
     * 6.5——STRATEGORY_TYPE_BOLL_MD
     * 徘徊期的md特点是md小数至少有5位，逃离该期md不在该级别，且candle连续变化
     *
     */
    private void strategyBollMd(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList,
                                          TradeInfo tradeInfo, TradeInfo preTradeInfo) {
//        if (preTradeInfo.getBoll().getMd() * 10000 < 1) {
//            return;
//        }

        if (preTradeInfo.getBoll().getMd() * 10000 < 1
                && tradeInfo.getBoll().getMd() * 10000 >= 1) {
            return;
        }

        int end = tradeInfoList.size() - 3;
        int buyType = 0;

        for (int i = end; i >= end - 3; --i) {

            if (tradeInfoList.get(i).getBoll().getMd() * 10000 >= 1) {
                return;
            }
        }

        if (tradeInfo.getOpen() < tradeInfo.getClose()) {
            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_MD);
        } else {
            tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_MD);
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第七类  ema的应用   *******************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 7——STRATEGORY_TYPE_EMA_1_2_INTERSECT
     *  gbpusd，
     * 看多：ema1上交ema2
     * 看空：ema1下交ema2
     */
    private void strategyEma1_2Intersect(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList, TradeInfo tradeInfo, TradeInfo preTradeInfo) {
        //交点处ema与preema的ema1之差进行分析的临界值
        final float diff = 0.00001f;
        final float samePoint = 0.000001f;  //相交于该点，方向无法判断，需要下一个时间点来判断

        //有交点
        TradeInfo.EMA preEma = preTradeInfo.getEma();
        TradeInfo.EMA ema = tradeInfo.getEma();

        //连着两点都没有交点，或者连着两点都是交点
        if (!((ema.ema1_2 && !preEma.ema1_2)
                || (!ema.ema1_2 && preEma.ema1_2 && (Math.abs(preEma.diff1) < diff || Math.abs(preEma.getEma1() - preEma.getEma2()) < samePoint)))) {
            return;
        }

        if (Math.abs(ema.diff1) < diff   //相邻两点的ema1的高度差小于临界值的
                || Math.abs(ema.getEma1() - ema.getEma2()) < samePoint) {
            return;
        }

        if (!ema.ema1_2) {
            int end = tradeInfoList.size() - 1;
            int len = 4, i = 1;
            for (; i < len; ++i) {
                TradeInfo tmpTrade = tradeInfoList.get(end - i);
                TradeInfo.EMA tmpEma = tmpTrade.getEma();

                //求相交点前面一点ema1和ema2方向，需要ema1和ema2的大小与ema相反，排除同向拐点
                if (!tmpEma.ema1_2) {
                    if ((ema.getEma1() > ema.getEma2() && tmpEma.getEma1() < tmpEma.getEma2())
                            || (ema.getEma1() < ema.getEma2() && tmpEma.getEma1() > tmpEma.getEma2())) {
                        break;
                    }
                    return;
                }
            }

            //没找到符合要求的，就忽略掉
            if (i >= len) {
                return;
            }
        }

        //上交，看多
        if (ema.diff1 > 0 && ema.getEma1() > ema.getEma2()) {
            tradeInfo.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_EMA_1_2_INTERSECT);

            //下交看空
        } else if (ema.diff1 < 0 && ema.getEma1() < ema.getEma2()) {
            tradeInfo.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_EMA_1_2_INTERSECT);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第八类  构建卖点   *******************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 1—— STRATEGORY_TYPE_SELL_MA_10_OVERTURN
     * ma5已经出现拐点，向ma10靠近，且ma10进入出现拐点前的角度减小现象
     * 卖多：ma5出现上拐点，ma5向下ma10靠近，ma10的差值逐渐减小
     * 卖空：ma5出现下拐点，ma5向上ma10靠近，ma10的差值逐渐减小
     *
     */
    private void sellPointMa10(StockStrategy stockStrategy, List<TradeInfo> tradeInfoList, TradeInfo tradeInfo, TradeInfo preTradeInfo,
                               TradeInfo preThirdTradeInfo, TradeInfo preFourTradeInfo, float deltaHeight) {
//        if (!((preThirdTradeInfo.getMa().dif2 > 0 && preFourTradeInfo.getMa().dif2 < 0)
//                || (preThirdTradeInfo.getMa().dif2 < 0 && preFourTradeInfo.getMa().dif2 > 0)
//                || preThirdTradeInfo.getMa().dif2 == 0)
//                ) {
//            return;
//        }

        //当前多空类型
        int buyType = InitAppConstant.FORECAST_BULL_BEAR;
        //上拐
        if ((preThirdTradeInfo.getMa().dif2 > 0 && preFourTradeInfo.getMa().dif2 < 0) || (preThirdTradeInfo.getMa().dif2 == 0 && preFourTradeInfo.getMa().dif2 < 0)) {
            buyType = InitAppConstant.FORECAST_BULL_SELL;

            //下拐
        } else if ((preThirdTradeInfo.getMa().dif2 < 0 && preFourTradeInfo.getMa().dif2 > 0) || (preThirdTradeInfo.getMa().dif2 == 0 && preFourTradeInfo.getMa().dif2 > 0)) {
            buyType = InitAppConstant.FORECAST_BEAR_SELL;
        } else {
            return;
        }


        TradeInfo preFiveTradeInfo = tradeInfoList.get(tradeInfoList.size() - 5);
        TradeInfo preSixTradeInfo = tradeInfoList.get(tradeInfoList.size() - 6);
        if (InitAppConstant.FORECAST_BULL_SELL == buyType
                && tradeInfo.getMa().dif2 > 0 && preTradeInfo.getMa().dif2 > 0 && preThirdTradeInfo.getMa().dif2 >= 0
                && preSixTradeInfo.getMa().dif2 < 0 && preFiveTradeInfo.getMa().dif2 < 0 && preFourTradeInfo.getMa().dif2 <= 0 ) {

            tradeInfo.addSellBearStrategy(InitAppConstant.STRATEGORY_TYPE_SELL_MA_10_OVERTURN);

        } else if (InitAppConstant.FORECAST_BEAR_SELL == buyType
                && tradeInfo.getMa().dif2 < 0 && preTradeInfo.getMa().dif2 < 0  && preThirdTradeInfo.getMa().dif2 <= 0
                && preSixTradeInfo.getMa().dif2 > 0 && preFiveTradeInfo.getMa().dif2 > 0 && preFourTradeInfo.getMa().dif2 >= 0) {

            tradeInfo.addSellBullStrategy(InitAppConstant.STRATEGORY_TYPE_SELL_MA_10_OVERTURN);
        }

//
//        //从倒数第二项往前推
//        int end = tradeInfoList.size() - 4;
//        TradeInfo.MA    afterMa = preFourTradeInfo.getMa();
//        int times = 1;
//        for (int i = end; i >= end - 3; --i) {
//            TradeInfo tmpTrade = tradeInfoList.get(i);
//            TradeInfo.MA tmpMa = tmpTrade.getMa();
//
//            //将下拐
//            if (InitAppConstant.FORECAST_BULL_BUY == buyType && afterMa.dif2 <= tmpMa.dif2) {
//                times++;
//                //将上拐
//            } else if (InitAppConstant.FORECAST_BEAR_BUY == buyType && afterMa.dif1 >= tmpMa.dif2) {
//                times++;
//            }
//
//            afterMa = tmpMa;
//        }
//
//        if (times >= 4) {
//            //将下拐
//            if (InitAppConstant.FORECAST_BULL_BUY == buyType) {
//                tradeInfo.addSellBullStrategy(InitAppConstant.STRATEGORY_TYPE_SELL_MA_10_OVERTURN);
//                //将上拐
//            } else if (InitAppConstant.FORECAST_BEAR_BUY == buyType) {
//                tradeInfo.addSellBearStrategy(InitAppConstant.STRATEGORY_TYPE_SELL_MA_10_OVERTURN);
//            }
//        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*******************************第七类macd distance*******************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////


    /**
         * 对最新数据（列表末尾项）分析各项指标
         * @param values  针对的是某一支的某一分钟
         * @param tmpData 针对的是某一支的某一分钟
         */
    private synchronized void analyzeNewIndex(String symbol, List<TradeInfo> values, AnalyzeTmpData.TmpData tmpData) {
        MAInd.INSTANCE.computeMANew(tmpData.maTmp, values);
        MACDInd.INSTANCE.computeMACDNew(tmpData.macdTmp, values);
        BollInd.INSTANCE.computeBollNew(tmpData.bollTmp, values);
        KDJInd.INSTANCE.computeKDJNew(tmpData.kdjTmp, values);
        RSIInd.INSTANCE.computeRSINew(tmpData.rsiTmp, values);
//        OBVInd.INSTANCE.computeOBVNew(tmpData.obvTmp, values);
//        TBRegionInd.INSTANCE.computeTBRegionNew(tmpData.tbRegionTmp, values);
//        ShockInd.INSTANCE.computeShockNew(tmpData.shockTmp, values);
        EMAInd.INSTANCE.computeEMANew(tmpData.emaTmp, values);
    }

    /**
     * 对历史数据分析各项指标
     * period  下次要请求的数据
     * @param values
     */
    public synchronized long analyzeHistIndex(int type, String symbol, List<TradeInfo> values) {

        //下次请求时间是最后一项的时间，获取后移除
        int lastPos = values.size() - 1;
        long period = values.get(lastPos).getTime();
        values.remove(lastPos);


        //开始循环计算各项指标
        AnalyzeTmpData.TmpData tmpData = new AnalyzeTmpData.TmpData();

        MAInd.INSTANCE.computeMAHistory(tmpData.maTmp, values);
        MACDInd.INSTANCE.computeMACDHistory(tmpData.macdTmp, values);
        BollInd.INSTANCE.computeBollHistory(tmpData.bollTmp, values);
        KDJInd.INSTANCE.computeKDJHistory(tmpData.kdjTmp, values);
        RSIInd.INSTANCE.computeRSIHistory(tmpData.rsiTmp, values);
        OBVInd.INSTANCE.computeOBVHistory(tmpData.obvTmp, values);
        TBRegionInd.INSTANCE.computeTBRegionHistory(tmpData.tbRegionTmp, values);
        ShockInd.INSTANCE.computeShockHistory(tmpData.shockTmp, values);
        EMAInd.INSTANCE.computeEMAHistory(tmpData.emaTmp, values);

        //将计算的中间值保存到对应的symbol的相应时间周期上
        AnalyzeTmpData newAnalyzeTmpData = null;
        if (mAnalyTmpData.containsKey(symbol)) {
            newAnalyzeTmpData = mAnalyTmpData.get(symbol);
        } else {
            newAnalyzeTmpData = new AnalyzeTmpData();
            mAnalyTmpData.put(symbol, newAnalyzeTmpData);
        }
        switch (type) {
            case InitAppConstant.MINUTE_1:
                newAnalyzeTmpData.setM1(tmpData);
                break;
            case InitAppConstant.MINUTE_5:
                newAnalyzeTmpData.setM5(tmpData);
                break;
            case InitAppConstant.MINUTE_15:
                newAnalyzeTmpData.setM15(tmpData);
                break;
            case InitAppConstant.MINUTE_30:
                newAnalyzeTmpData.setM30(tmpData);
                break;
            case InitAppConstant.MINUTE_60:
                newAnalyzeTmpData.setM60(tmpData);
                break;
            default:
                break;
        }

        return period;
    }

    //test
    public long analyzeHistByAdd(BaseActivity activity, int type, long serverTime, String symbol, List<TradeInfo> values) {
        //根据服务器时间计算时间段
        long start = ToolTime.getServerStartTime(serverTime, 21, 30);
        long end = start + ToolTime.getTimeDiffMillis(0, 22);
        ToolLog.i("first period - start:" + start + "  end:" + end);

        //截取40条数据，之后的
        final int IGNORE_LEN = 20;
        List<TradeInfo> subValues = new ArrayList<>(300);
        for (int i = 0; i < IGNORE_LEN; ++i) {
            subValues.add(values.get(i));
        }

        AnalyzeTmpData.TmpData tmpData = new AnalyzeTmpData.TmpData();

        MAInd.INSTANCE.computeMAHistory(tmpData.maTmp, subValues);
        MACDInd.INSTANCE.computeMACDHistory(tmpData.macdTmp, subValues);
        BollInd.INSTANCE.computeBollHistory(tmpData.bollTmp, subValues);
        KDJInd.INSTANCE.computeKDJHistory(tmpData.kdjTmp, subValues);
        RSIInd.INSTANCE.computeRSIHistory(tmpData.rsiTmp, subValues);
//        OBVInd.INSTANCE.computeOBVHistory(tmpData.obvTmp, subValues);
//        TBRegionInd.INSTANCE.computeTBRegionHistory(tmpData.tbRegionTmp, subValues);
//        ShockInd.INSTANCE.computeShockHistory(tmpData.shockTmp, subValues);
        EMAInd.INSTANCE.computeEMAHistory(tmpData.emaTmp, subValues);

//将计算的中间值保存到对应的symbol的相应时间周期上
        AnalyzeTmpData newAnalyzeTmpData = null;
        if (mAnalyTmpData.containsKey(symbol)) {
            newAnalyzeTmpData = mAnalyTmpData.get(symbol);
        } else {
            newAnalyzeTmpData = new AnalyzeTmpData();
            mAnalyTmpData.put(symbol, newAnalyzeTmpData);
        }

        StockInfo stockInfo = new StockInfo();

        switch (type) {
            case InitAppConstant.MINUTE_1:
                newAnalyzeTmpData.setM1(tmpData);
                stockInfo.setItems(subValues);
                break;
            case InitAppConstant.MINUTE_5:
                newAnalyzeTmpData.setM5(tmpData);
                stockInfo.setItems5(subValues);
                break;
            case InitAppConstant.MINUTE_15:
                newAnalyzeTmpData.setM15(tmpData);
                stockInfo.setItems15(subValues);
                break;
            case InitAppConstant.MINUTE_30:
                newAnalyzeTmpData.setM30(tmpData);
                stockInfo.setItems30(subValues);
                break;
            case InitAppConstant.MINUTE_60:
                newAnalyzeTmpData.setM60(tmpData);
                stockInfo.setItems60(subValues);
                break;
            default:
                break;
        }

        ToolLog.i("size:" + values.size());
        StockStrategy stockStrategy = new StockStrategy(symbol, symbol, 0);

        for (int i = IGNORE_LEN; i < values.size(); ++i) {
            TradeInfo tradeInfo = values.get(i);
            stockStrategy.resetBuy();
            stockStrategy.resetSell();
            tradeInfo.resetBuy();
            tradeInfo.resetSell();
            tradeInfo.setSymbol(symbol);

            //测试非测试环境的执行
            analyzeStockInfoResult(activity, type, stockInfo, tradeInfo, tradeInfo.getTime(), stockStrategy);

        }

        return -1;
    }

}
