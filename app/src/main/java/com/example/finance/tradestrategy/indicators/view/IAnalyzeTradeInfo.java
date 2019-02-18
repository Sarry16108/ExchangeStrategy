package com.example.finance.tradestrategy.indicators.view;

import com.example.finance.tradestrategy.entity.StockStrategy;
import com.example.finance.tradestrategy.entity.TradeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface IAnalyzeTradeInfo {
    StockStrategy TrendAndCharacter(List<TradeInfo> values);
}
