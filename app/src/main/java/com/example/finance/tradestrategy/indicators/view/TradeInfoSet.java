/*
 * Copyright (C) 2017 WordPlat Open Source Project
 *
 *      https://wordplat.com/InteractiveKLineView/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.finance.tradestrategy.indicators.view;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.indicators.view.index.StockIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>TradeInfoSet</p>
 * <p>Date: 2017/3/1</p>
 *
 * @author afon
 */

public class TradeInfoSet {

    /**
     * Y 轴上 TradeInfo 的最大值
     */
    private float maxY;

    /**
     * Y 轴上 TradeInfo 的最小值
     */
    private float minY;

    /**
     * Y 轴上 TradeInfo 的最大值索引
     */
    private int maxYIndex;

    /**
     * Y 轴上 TradeInfo 的最小值索引
     */
    private int minYIndex;

    /**
     * 高亮的 TradeInfo 索引
     */
    private int highlightIndex;

    /**
     * TradeInfo 列表
     */
    private final List<TradeInfo> entries = new ArrayList<>();

    /**
     * 列表第一个 TradeInfo 的昨日收盘价，用于判断当第一个 TradeInfo 的收盘价等于开盘价时，
     * 不好判断是涨停还是跌停还是不涨不跌，此值默认设置0，即一律视为涨停
     */
    private float preClose = 0;

    /**
     * 是否在加载中状态
     */
    private boolean loadingStatus = true;

    /**
     * 在可显示范围内，显示上边界与下边界的差
     */
    private float   mDeltaY;

    /**
     * 添加一个 TradeInfo 到尾部
     */
    public void addTradeInfo(TradeInfo TradeInfo) {
        entries.add(TradeInfo);
    }

    /**
     * 添加一组 TradeInfo 到尾部
     */
    public void addEntries(List<TradeInfo> entries) {
        this.entries.addAll(entries);
    }

    /**
     * 添加一个 TradeInfo 到头部
     */
    public void insertFirst(TradeInfo TradeInfo) {
        entries.add(0, TradeInfo);
    }

    /**
     * 添加一组 TradeInfo 到头部
     */
    public void insertFirst(List<TradeInfo> entries) {
        this.entries.addAll(0, entries);
    }

    public List<TradeInfo> getTradeInfoList() {
        return entries;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getDeltaY() {
        return maxY - minY; //todo:替换此处，因为从ma换为boll后，maxY和minY表示的是上边缘最大值和下边缘最大值，已经不是价格最大和最小
//        return mDeltaY;
    }

    public int getMinYIndex() {
        return minYIndex;
    }

    public int getMaxYIndex() {
        return maxYIndex;
    }

    public int getHighlightIndex() {
        return highlightIndex;
    }

    public void setHighlightIndex(int highlightIndex) {
        this.highlightIndex = highlightIndex;
    }

    public TradeInfo getHighlightTradeInfo() {
        if (0 < highlightIndex && highlightIndex < entries.size()) {
            return entries.get(highlightIndex);
        }
        return null;
    }

    public float getPreClose() {
        return preClose;
    }

    public void setPreClose(float preClose) {
        this.preClose = preClose;
    }

    public boolean isLoadingStatus() {
        return loadingStatus;
    }

    public void setLoadingStatus(boolean loadingStatus) {
        this.loadingStatus = loadingStatus;
    }

    /**
     * 在给定的范围内，计算分时图 entries 的最大值和最小值
     *
     * @param start
     * @param end
     */
    public void computeTimeLineMinMax(int start, int end) {
        int endValue;
        if (end < 2 || end >= entries.size()) {
            endValue = entries.size() - 1;
        } else {
            endValue = end - 1; // 减去 1 是为了把边缘的 TradeInfo 排除
        }

        minY = Float.MAX_VALUE;
        maxY = -Float.MAX_VALUE;

        for (int i = start; i <= endValue; i++) {
            TradeInfo TradeInfo = entries.get(i);

            if (TradeInfo.getClose() < minY) {
                minY = TradeInfo.getClose();
                minYIndex = i;
            }

            if (TradeInfo.getClose() > maxY) {
                maxY = TradeInfo.getClose();
                maxYIndex = i;
            }
        }
    }


    /**
     * 在给定的范围内，计算 K 线图与MA entries 的最大值和最小值
     */
    public void computeMinMaxWithMa(int start, int end, List<StockIndex> stockIndexList) {
        int endValue;
        if (end < 2 || end >= entries.size()) {
            endValue = entries.size() - 1;
        } else {
            endValue = end - 1; // 减去 1 是为了把边缘的 entry 排除
        }

        minY = Float.MAX_VALUE;
        maxY = -Float.MAX_VALUE;

        if (stockIndexList != null) {
            for (StockIndex stockIndex : stockIndexList) {
                if (stockIndex.isEnable()) {
                    stockIndex.resetMinMax();
                }
            }
        }

        for (int i = start; i <= endValue; i++) {
            TradeInfo entry = entries.get(i);
            TradeInfo.MA ma = entry.getMa();

            if (entry.getLow() < minY) {
                minY = entry.getLow();
                minYIndex = i;
            }
            minY = Math.min(minY, ma.getMa1());
            minY = Math.min(minY, ma.getMa2());
            minY = Math.min(minY, ma.getMa3());

            if (entry.getHigh() > maxY) {
                maxY = entry.getHigh();
                maxYIndex = i;
            }
            maxY = Math.max(maxY, ma.getMa1());
            maxY = Math.max(maxY, ma.getMa2());
            maxY = Math.max(maxY, ma.getMa3());

            if (stockIndexList != null) {
                for (StockIndex stockIndex : stockIndexList) {
                    if (stockIndex.isEnable()) {
                        stockIndex.computeMinMax(i, entry);
                    }
                }
            }
        }
    }


    /**
     * 在给定的范围内，计算 K 线图与Boll entries 的最大值和最小值
     */
    public void computeMinMaxWithBoll(int start, int end, List<StockIndex> stockIndexList) {
        int endValue;
        if (end < 2 || end >= entries.size()) {
            endValue = entries.size() - 1;
        } else {
            endValue = end - 1; // 减去 1 是为了把边缘的 entry 排除
        }

        minY = Float.MAX_VALUE;
        maxY = -Float.MAX_VALUE;

        //TODO:分别是上边缘和下边缘的值
//        float upBorder = 0, dnBorder = 0;

        if (stockIndexList != null) {
            for (StockIndex stockIndex : stockIndexList) {
                if (stockIndex.isEnable()) {
                    stockIndex.resetMinMax();
                }
            }
        }

        for (int i = start; i <= endValue; i++) {
            TradeInfo entry = entries.get(i);
            TradeInfo.Boll boll = entry.getBoll();

            if (entry.getLow() < minY) {
                minY = entry.getLow();
                minYIndex = i;
            }
//            dnBordertmp = Math.min(minY, boll.getDner());
//            dnBorder = Math.min(dnBorder, dnBordertmp);
            minY = Math.min(minY, boll.getDner());

            if (entry.getHigh() > maxY) {
                maxY = entry.getHigh();
                maxYIndex = i;
            }
//            upBordertmp = Math.max(maxY, boll.getUpper());
//            upBorder = Math.max(upBorder, upBordertmp);
            maxY = Math.max(maxY, boll.getUpper());

            if (stockIndexList != null) {
                for (StockIndex stockIndex : stockIndexList) {
                    if (stockIndex.isEnable()) {
                        stockIndex.computeMinMax(i, entry);
                    }
                }
            }
        }

    }
}
