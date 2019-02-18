package com.example.finance.tradestrategy.collections;


import com.example.finance.tradestrategy.entity.StockInfo;

import java.util.Comparator;

/**
 * Created by yanghj on 2017/6/3.
 */

public class SortComparator implements Comparator<StockInfo> {
    @Override
    public int compare(StockInfo left, StockInfo right) {
        return 0;//todo:(int)(left.getHotDegree() - right.getHotDegree());
    }
}
