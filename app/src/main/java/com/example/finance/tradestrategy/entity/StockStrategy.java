package com.example.finance.tradestrategy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class StockStrategy {
    private int     pos = 0;    //列表中位置
    private String  nameCN;
    private String  symbol;
    private float   close;
    private int  periodType; //周期类型：min1，min5，min10等

    //策略组，只要size不为0，则代表有购买的策略
    private List<Integer> buyBullStrategies;
    private List<Integer> buyBearStrategies;

    //策略组，只要size不为0，则代表有卖出的策略
    private List<Integer> sellBullStrategies;
    private List<Integer> sellBearStrategies;

    //10分钟内避免同一结果提示多次
    private final long TIME_LEN = 240000;

    //上次分析的结果，1买多，2买空，3存在多空双向
    private int         buyStatus = 0;
    private long        buyBullTime = 0;
    private long        buyBearTime = 0;

    //上次分析的结果，1卖多，2卖空，3存在
    private int         sellStatus = 0;
    private long        sellBullTime = 0;
    private long        sellBearTime = 0;

/*
    //单项的预测
    public static class Strategy {
        private boolean buy;        //该方向上的预测，InitAppConstant.FORECAST_LEVEL_NEUTRAL
        private int     strategyType = InitAppConstant.FORECAST_BULL_BEAR;    //

    }*/

    public StockStrategy(String nameCN, String symbol, int pos) {
        this.nameCN = nameCN;
        this.symbol = symbol;
        this.pos = pos;
        buyBullStrategies = new ArrayList<>(2);
        buyBearStrategies = new ArrayList(2);
        sellBullStrategies = new ArrayList<>(2);
        sellBearStrategies = new ArrayList<>(2);
    }

    public int getPos() {
        return pos;
    }

    public String getNameCN() {
        return nameCN;
    }

    public void setNameCN(String nameCN) {
        this.nameCN = nameCN;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getClose() {
        return close;
    }

    public void setPeriodType(int periodType) {
        this.periodType = periodType;
    }

    public int getPeriodType() {
        return periodType;
    }

    public void addBuyBullStrategy(int strategy) {
        buyBullStrategies.add(strategy);
    }

    public void addBuyBearStrategy(int strategy) {
        buyBearStrategies.add(strategy);
    }

    public void addSellBullStrategy(int strategy) {
        sellBullStrategies.add(strategy);
    }

    public void addSellBearStrategy(int strategy) {
        sellBearStrategies.add(strategy);
    }

    public int getBuyStatus() {
        return buyStatus;
    }

    public int getSellStatus() {
        return sellStatus;
    }

    /**
     * 是否购买
     */
    public boolean isBuy() {
        boolean buy = buyBullStrategies.size() > 0 || buyBearStrategies.size() > 0;
        boolean isTip = buy;    //false;

        // TODO: 2017/10/29 关闭频繁提醒限制，因为同一时间可能有多个分钟类型提醒
//        if (buy) {
//            long curTime = System.currentTimeMillis();
//
//            //当本次购买，且不在频繁提醒区
//            if (0 < buyBullStrategies.size() && curTime > buyBullTime) {
//                buyBullTime = curTime + TIME_LEN;
//                isTip = true;
//            }
//
//            if (0 < buyBearStrategies.size() && curTime > buyBearTime) {
//                buyBearTime = curTime + TIME_LEN;
//                isTip = true;
//            }
//        }

        return isTip;
    }

    public void resetBuy() {
        buyBullStrategies.clear();
        buyBearStrategies.clear();
    }

    public List<Integer> getBuyBullStrategies() {
        return buyBullStrategies;
    }

    public List<Integer> getBuyBearStrategies() {
        return buyBearStrategies;
    }

    /**
     * 是否卖出
     */
    public boolean isSell() {
        boolean sell = sellBullStrategies.size() > 0 || sellBearStrategies.size() > 0;
        boolean isTip = sell;   //false;

        // TODO: 2017/10/29 关闭频繁提醒限制，因为同一时间可能有多个分钟类型提醒
//        if (sell) {
//            long curTime = System.currentTimeMillis();
//
//            //当本次购买，且不在频繁提醒区
//            if (0 < sellBullStrategies.size() && curTime > sellBullTime) {
//                sellBullTime = curTime + TIME_LEN;
//                isTip = true;
//            }
//
//            if (0 < sellBearStrategies.size() && curTime > sellBearTime) {
//                sellBearTime = curTime + TIME_LEN;
//                isTip = true;
//            }
//        }

        return isTip;
    }

    public void resetSell() {
        sellBullStrategies.clear();
        sellBearStrategies.clear();
    }

    public List<Integer> getSellBullStrategies() {
        return sellBullStrategies;
    }


    public List<Integer> getSellBearStrategies() {
        return sellBearStrategies;
    }
}
