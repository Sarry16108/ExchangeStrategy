package com.example.finance.tradestrategy.indicators.calculate;

import android.support.v4.util.ArrayMap;

import com.example.finance.tradestrategy.baseui.BaseActivity;
import com.example.finance.tradestrategy.entity.AnalyzeTmpData;
import com.example.finance.tradestrategy.entity.StockInfo;
import com.example.finance.tradestrategy.entity.StockStrategy;
import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.globaldata.MessageId;
import com.example.finance.tradestrategy.utils.ToolData;
import com.example.finance.tradestrategy.utils.ToolMath;
import com.example.finance.tradestrategy.utils.ToolNotification;
import com.example.finance.tradestrategy.utils.ToolTime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/13.
 */

public enum AnalyzeInd {
    INSTANCE;
    private final String TAG = "AnalyzeInd";

    //为了减少每次获得数据后的遍历计算，保存一些中间值，放在这里是为了减少map.get()及map.containKey()判断
    private Map<String, AnalyzeTmpData> mAnalyTmpData = new ArrayMap<>(10);
    //记录当前分析的结果，但是由于不利于以后的扩展，所以，采用每次调用checkResult遍历的方式
//    private Map<String, AnalyzeResult>  mAnalyzeResult = new ArrayMap<>(10);
    //股票开市的时间区间，对应着相应的策略起作用
    private long mFirstPeriodStart = 0, mFirstPeriodEnd = 0;

    /**
     *
     * @param type          分钟类型
     * @param historyInfo   历史记录数据
     * @param tradeUpdate     最新获取更新数据
     * @param period        当前请求的时间段
     * @param stockStrategy
     * @return
     */
    public synchronized long analyzeStockInfoResult(BaseActivity activity, int type, StockInfo historyInfo, TradeInfo tradeUpdate,
                                                    Long period, StockStrategy stockStrategy) {
        //分种类型
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

        //对结果进行判断
        //添加通知和记录
        if (stockStrategy.isBuy() || stockStrategy.isSell()) {
            ToolNotification.getInstance().showNotification(stockStrategy);
            activity.packMsgAndSend(MessageId.FORECAST_RECORD_ADD, stockStrategy);
        }

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

        if (InitAppConstant.MINUTE_1 == type) {
            List<TradeInfo> tradeInfos = historyInfo.getItems();
            int size = tradeInfos.size();

            TradeInfo tradeInfo = tradeInfos.get(size - 1), preTradeInfo = tradeInfos.get(size - 2), preThirdTradeInfo = tradeInfos.get(size - 3),
                    preFourTradeInfo = tradeInfos.get(size - 3);

//            strategyMa5_10_20Intersect(stockStrategy, tradeInfos, tradeInfo, preTradeInfo);
//            strategyMaMacdOverturn(stockStrategy, tradeInfos, tradeInfo, preTradeInfo);
//            strategyMaIntersectSameIntersect(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo);
//            strategyMa10_20Intersect(stockStrategy, tradeInfos, tradeInfo, preTradeInfo);
//            strategyMa5Overturn_10Intersect(stockStrategy, tradeInfos, tradeInfo, preTradeInfo, preThirdTradeInfo);

            strategyEma1_2Intersect(stockStrategy, tradeInfos, tradeInfo, preTradeInfo);
//            strategyCloseBollGolden(stockStrategy, tradeInfo);
//            strategyMa5BollGoldenIntersect(stockStrategy, tradeInfos, tradeInfo, preTradeInfo);
//            strategyMa5BollGoldenIntersectStart(stockStrategy, tradeInfo, preTradeInfo);
//            strategyMa5_20BollGolden(stockStrategy, tradeInfo, preTradeInfo, preThirdTradeInfo, preFourTradeInfo);
        } else if (InitAppConstant.MINUTE_5 == type) {
            List<TradeInfo> tradeInfos5 = historyInfo.getItems5();
            int sizeM5 = tradeInfos5.size();

            TradeInfo tradeInfoM5 = tradeInfos5.get(sizeM5 - 1), preTradeInfoM5 = tradeInfos5.get(sizeM5 - 2), preThirdTradeInfoM5 = tradeInfos5.get(sizeM5 - 3),
                    preFourTradeInfoM5 = tradeInfos5.get(sizeM5 - 3);
        }
    }

    /***********************************************************
     *                                      算法
     */

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
                    stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_10_20_INTERSECT);
                    break;

                    //多:ma5 > ma10, bot >= ma10
                } else if (isBuyBull == 1 && hisMa.getMa1() > hisMa.getMa2()
                        && hisInfo.getEntityBot() >= hisMa.getMa2()) {
                    stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_10_20_INTERSECT);
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
                    stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_OVERTURN);

                    //看多
                } else if (roundDir > 0 && hisMa.getMa1() < tradeInfo.getMa().getMa1()) {
                    stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_MACD_OVERTURN);
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
                stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);

                //多
            } else if (ma.getMa1() > ma.getMa2() && ma.getMa2() > ma.getMa3()
                    && preMa.getMa1() < preMa.getMa2() && preMa.getMa2() < preMa.getMa3()
                    && preMa.getMa2() < ma.getMa2() && preMa.getMa3() < ma.getMa3()) {
                stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);
            }

            //ma5穿过ma10，ma20，ma的ma20与ma10差值小于preMa的
        } else if (ma.ma5_20 && ma.ma5_10 && Math.abs(ma.getMa2() - ma.getMa3()) < Math.abs(preMa.getMa2() - preMa.getMa3()) - 0.00001) {
            //看多
            if (ma.getMa1() > preMa.getMa1()) {
                stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);
                //看空
            } else if (ma.getMa1() < preMa.getMa1()) {
                stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT);
            }
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
            stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_10_20);
        } else if (2 == buyType) {
            stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_10_20);
        }

    }

    private void strategyCloseBollGolden(StockStrategy stockStrategy,
                                         TradeInfo tradeInfo) {
        TradeInfo.Boll boll = tradeInfo.getBoll();
        //// TODO: 2017/10/18 将top<up1618，bot>down1618改为high<=up1618,low>=down1618
        if (tradeInfo.getEntityTop() >= boll.getGoldenUp0618() && tradeInfo.getEntityTop() <= boll.getGoldenUp1618()
                && tradeInfo.getEntityBot() <= boll.getGoldenDown0618() && tradeInfo.getEntityBot() >= boll.getGoldenDown1618()) {

            //方向和涨跌一致
            //看空
            if (tradeInfo.getOpen() > tradeInfo.getClose()) {
                stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_GOLEN_SPERATOR);

                //看涨
            } else if (tradeInfo.getOpen() < tradeInfo.getClose()){
                stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_BOLL_GOLEN_SPERATOR);
            }
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
                stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT);
            } else if (buyType == InitAppConstant.FORECAST_BEAR_BUY) {
                stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT);
            }
        }

    }

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
                    stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                } else if (intersectType == 3 && tmpMa.getMa1() > tmpBoll.getGoldenDown0382() && tmpPreMa.getMa1() < tmpPreBoll.getGoldenDown0382()
                        && tradeInfo.getEntityBot() > boll.getGoldenUp0618()) {
                    stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                } else if (intersectType == 2 && tmpMa.getMa1() < tmpBoll.getGoldenUp0382() && tmpPreMa.getMa1() > tmpPreBoll.getGoldenUp0382()
                        && tradeInfo.getEntityTop() < boll.getGoldenDown0382()) {
                    stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                } else if (intersectType == 4 && tmpMa.getMa1() < tmpBoll.getGoldenDown0382() && tmpPreMa.getMa1() > tmpPreBoll.getGoldenDown0382()
                        && tradeInfo.getEntityTop() < boll.getGoldenDown0382()){
                    stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT);
                }

                break;
            }
        }
    }
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
                stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START);

                //看多
            } else if (ma.getMa1() >= boll.getGoldenDown0382() && preMa.getMa1() <= preBoll.getGoldenDown0618()
                    && tradeInfo.getEntityBot() >= boll.getMb() && tradeInfo.getEntityTop() >= boll.getGoldenUp0618()
                    && preTradeInfo.getEntityBot() >= preBoll.getGoldenDown0382()
                    && ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenDown0618(), 1, boll.getGoldenDown0618())
                    && ToolMath.isSegmentsIntersection(0, preMa.getMa1(), 1, ma.getMa1(), 0, preBoll.getGoldenDown0382(), 1, boll.getGoldenDown0382())) {
                stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START);
            }
        }
    }

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
            stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN);

            //看多
        } else if (ma.getMa1() > ma.getMa3() && preMa.getMa3() > preMa.getMa1()
                && preTradeInfo.getEntityTop() >= preBoll.getGoldenUp0618()
                && ((preTradeInfo.getEntityBot() >= preBoll.getGoldenDown1618() && preTradeInfo.getEntityBot() <= preBoll.getGoldenDown0618())
                || (preTradeInfo.getOpen() <= preTradeInfo.getClose() && thirdTradeInfo.getOpen() <= thirdTradeInfo.getClose()
                && thirdTradeInfo.getEntityBot() >= thirdBoll.getGoldenDown1618() && thirdTradeInfo.getEntityBot() <= thirdBoll.getGoldenDown0618()))) {
            stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN);
        }
    }


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
            stockStrategy.addBuyBullStrategy(InitAppConstant.STRATEGORY_TYPE_EMA_1_2_INTERSECT);

            //下交看空
        } else if (ema.diff1 < 0 && ema.getEma1() < ema.getEma2()) {
            stockStrategy.addBuyBearStrategy(InitAppConstant.STRATEGORY_TYPE_EMA_1_2_INTERSECT);
        }
    }

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
    }

    /**
     * 对历史数据分析各项指标
     * period  下次要请求的数据
     * @param values
     */
    public synchronized long analyzeHistIndex(int type, long serverTime, String symbol, List<TradeInfo> values) {
        //根据服务器时间计算时间段
        mFirstPeriodStart = ToolTime.getServerStartTime(serverTime, 21, 30);
        mFirstPeriodEnd = mFirstPeriodStart + ToolTime.getTimeDiffMillis(0, 22);

        //保留values的大小在50个，因为更多历史数据没有太大用处，反而耗费存储空间以及各种
        Iterator iterator = values.iterator();
        while (values.size() > 50) {
            iterator.next();
            iterator.remove();
        }

        //下次请求时间是最后一项的时间
        int lastPos = values.size() - 1;
        long period = values.get(lastPos).getTime();

        //开始循环计算各项指标
        AnalyzeTmpData.TmpData tmpData = new AnalyzeTmpData.TmpData();

        MAInd.INSTANCE.computeMAHistory(tmpData.maTmp, values);
        MACDInd.INSTANCE.computeMACDHistory(tmpData.macdTmp, values);
        BollInd.INSTANCE.computeBollHistory(tmpData.bollTmp, values);
        KDJInd.INSTANCE.computeKDJHistory(tmpData.kdjTmp, values);
        RSIInd.INSTANCE.computeRSIHistory(tmpData.rsiTmp, values);

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

}