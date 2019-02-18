package com.example.finance.tradestrategy.utils;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */

public class ToolData {

    /**
     *
     * @param datas     数据集合
     * @param maxLen    最大包含数据量
     * @param minLen    最小包含据量
     */
    public static <T> void dataCompactionFromHead(List<T> datas, int maxLen, int minLen) {
        if (null != datas && minLen < maxLen && maxLen < datas.size()) {
            datas.removeAll(datas.subList(0, datas.size() - minLen));
        }
    }
}
