package com.example.finance.tradestrategy.databindings;

import com.example.finance.tradestrategy.entity.StockForecast;
import com.example.finance.tradestrategy.entity.StockStrategy;
import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.globaldata.InitData;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 */

public class DatabindingUtls {
    private final static String TAG = "DatabindingUtls";

    private static void FormatNewlineItem(StringBuilder builder, String tip, String value) {
        if (null == value) {
            value = "";
        }
        builder.append(tip + " :  " + value + '\n');
    }

    private static void FormatNewlineItem(StringBuilder builder, String tip, float value) {
        builder.append(tip + " :  " + value + '\n');
    }

    private static void FormatNewlineItem(StringBuilder builder, String tip, int value) {
        builder.append(tip + " :  " + value + '\n');
    }

    private static void FormatNewlineItem(StringBuilder builder, Object ...values) {
        if (0 == values.length || 0 != (values.length % 2)) {
            ToolLog.e(TAG, "FormatNewlineItem", "values is error :" + values.toString());
            return;
        }

        for (int i = 0; i < values.length; i+=2) {
            if (null == values[i + 1]) {
                values[i + 1] = "";
            }
            builder.append(values[i] + " :  " + values[i + 1] + "  ");
        }
        builder.append('\n');
    }

    public static String getClosePrice(float close) {
        return "价格：" + close;
    }

    public static String stratogySymbol(String symbol) {
        return "（" + symbol + "）";
    }

    public static String stratogyTitle(StockForecast stockForecast) {
        return stockForecast.getSymbol() + "—" + stockForecast.getNameCN();
    }

//    public static String stratogyResult(StockStrategy stockStrategy) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("方向：").append(getDirection(stockStrategy.getForecastLevel()));
//        builder.append("\n强度：").append(getImportance(stockStrategy.getForecastLevel()));
//        return builder.toString();
//    }

    public static String getDirection(int level) {
        switch (level) {
            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH:
            case InitAppConstant.FORECAST_LEVEL_BEARISH:
            case InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH:
                return "跌";
            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BULLISH:
            case InitAppConstant.FORECAST_LEVEL_BULLISH:
            case InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH:
                return "涨";
            case InitAppConstant.FORECAST_LEVEL_NEUTRAL:
                default:
                return "观望";
        }
    }

    //预测强度：InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH
    public static int getImportance(int level) {
        return level % 4;
    }

    public static String getImportanceStr(int level) {
        return getImportance(level) + " 级";
    }

    public static String getStartStopText(boolean isRunning) {
        return isRunning ? "结束" : "开始";
    }


    public static String getMemberItemCountPrice(String count, String price) {
        return "手数：" + count + "\r\r\r价格：" + price;
    }


    public static String getTip(StockStrategy strategyInfo) {
        StringBuilder builder = new StringBuilder();
        int buyBullSize = strategyInfo.getBuyBullStrategies().size(), buyBearSize = strategyInfo.getBuyBearStrategies().size(),
            sellBullSize = strategyInfo.getSellBullStrategies().size(), sellBearSize = strategyInfo.getSellBearStrategies().size();

        if (0 < buyBullSize) {
            builder.append("买入看多策略组：");
            for (Integer strategy : strategyInfo.getBuyBullStrategies()) {
                builder.append(getStrategoryType(strategy));
                builder.append(" ; ");
            }
        }

        if (0 < buyBearSize) {
            builder.append("买入看空策略组：");
            for (Integer strategy : strategyInfo.getBuyBearStrategies()) {
                builder.append(getStrategoryType(strategy));
                builder.append(" ; ");
            }
        }

        if (0 < sellBullSize) {
            builder.append("卖出看多策略组：");
            for (Integer strategy : strategyInfo.getSellBullStrategies()) {
                builder.append(getStrategoryType(strategy));
                builder.append(" ; ");
            }
        }

        if (0 < sellBearSize) {
            builder.append("卖出看空策略组：");
            for (Integer strategy : strategyInfo.getSellBearStrategies()) {
                builder.append(getStrategoryType(strategy));
                builder.append(" ; ");
            }
        }

        return builder.toString();
//
//        switch (forecastLevel) {
//            case InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH:
//                return "将大跌，卖出<看多>，买入<看空>";
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH:
//            case InitAppConstant.FORECAST_LEVEL_BEARISH:
//                return "将跌，卖出<看多>，买入<看空>";
//            case InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH:
//                return "将大涨，卖出<看空>，买入<看多>";
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BULLISH:
//            case InitAppConstant.FORECAST_LEVEL_BULLISH:
//                return "将涨，卖出<看空>，买入<看多>";
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL:
//            default:
//                return "观望";
//        }
    }

//    public static String getTipNew(StockStrategy strategy) {
//        StringBuilder builder = new StringBuilder();
//        if (strategy.getBoll().isReverse()) {
//            builder.append("boll-").append(InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH == strategy.getBoll().getForecastLevel() ? "跌" : "涨");
//        }
//        if (strategy.getKdj().isReverse()) {
//            builder.append("  kdj-").append(InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH == strategy.getKdj().getForecastLevel() ? "跌" : "涨");
//        }
//        if (strategy.getRsi().isReverse()) {
//            builder.append("  rsi-").append(InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH == strategy.getRsi().getForecastLevel() ? "跌" : "涨");
//        }
//        if (strategy.getMa().isReverse()) {
//            builder.append("  ma-").append(InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH == strategy.getMa().getForecastLevel() ? "跌" : "涨");
//        }
//        if (strategy.getMacd().isReverse()) {
//            builder.append("  macd-").append(InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH == strategy.getMacd().getForecastLevel() ? "跌" : "涨");
//        }
//
//        return builder.toString();
//    }

    /**
     * 根据多空买卖状态，返回对应的字符串
     */
    public static String getBuySell(int status) {
        switch (status) {
            case InitAppConstant.FORECAST_BULL_BUY:
                return "买多";
            case InitAppConstant.FORECAST_BULL_SELL:
                return "卖多";
            case InitAppConstant.FORECAST_BEAR_BUY:
                return "买空";
            case InitAppConstant.FORECAST_BEAR_SELL:
                return "卖空";
            default:
                return "";
        }
    }

    public static String getStrategoryType(int type) {
        switch (type) {
            //买入
            case InitAppConstant.STRATEGORY_TYPE_UTRAL_MACD:
                return "1";
            case InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT:
                return "1.1";
            case InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION:
                return "1.2";
            case InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION2:
                return "1.3";
            case InitAppConstant.STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION3:
                return "2";
            case InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT:
                return "2.1";
            case InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT2:
                return "2.2";
            case InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT3:
                return "2.3";
            case InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT4:
                return "2.4";
            case InitAppConstant.STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT5:
                return "2.5";
            case InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN:
                return "3";
            case InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN2:
                return "3.1";
            case InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN3:
                return "3.2";
            case InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN4:
                return "3.3";
            case InitAppConstant.STRATEGORY_TYPE_MACD_DIFF_OVERTURN5:
                return "3.4";
            case InitAppConstant.STRATEGORY_TYPE_MACD_DISTANCE_INTERSECT:
                return "4";
            case InitAppConstant.STRATEGORY_TYPE_MA_5_10_20_INTERSECT:
                return "5";
            case InitAppConstant.STRATEGORY_TYPE_MA_MACD_OVERTURN:
                return "5.1";
            case InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_DIRECTION:
                return "5.2";
            case InitAppConstant.STRATEGORY_TYPE_MA_MACD_SAME_SIDE:
                return "5.3";
            case InitAppConstant.STRATEGORY_TYPE_MA_SAME_INTERSECT:
                return "5.4";
            case InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_20_40:
                return "5.5";
            case InitAppConstant.STRATEGORY_TYPE_MA_INTERSECT_10_20:
                return "5.6";
            case InitAppConstant.STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT:
                return "5.7";
            case InitAppConstant.STRATEGORY_TYPE_BOLL_GOLEN_SPERATOR:
                return "6";
            case InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT:
                return "6.1";
            case InitAppConstant.STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START:
                return "6.2";
            case InitAppConstant.STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN:
                return "6.3";
            case InitAppConstant.STRATEGORY_TYPE_CANDLE_BOLL_GOLEN:
                return "6.4";
            case InitAppConstant.STRATEGORY_TYPE_BOLL_MD:
                return "6.5";
            case InitAppConstant.STRATEGORY_TYPE_EMA_1_2_INTERSECT:
                return "7";


            //卖出
            case InitAppConstant.STRATEGORY_TYPE_SELL_MA_10_OVERTURN:
                return "s1";
            default:
                return "";
        }
    }

    /**
     * 获取策略组合，不同策略用/分割，
     * @param prefix    策略显示前缀
     * @param types     策略组合
     * @return
     */
    public static String getStrategyTypes(String prefix, List<Integer> types) {
        StringBuilder builder = new StringBuilder(prefix);
        for (Integer type : types) {
            builder.append(getStrategoryType(type)).append('/');
        }

        //删除最后一个符号
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String getSymbolName(String symbol) {
        switch (symbol) {
            case InitData.Ex_USD:
                return "美元指数";
            case InitData.Ex_XAU:
                return "现货黄金";
            case InitData.Ex_XAG:
                return "现货白银";
            case InitData.Ex_CONC:
                return "美原油连续";
            case InitData.Ex_AUDUSD:
                return "澳元兑美元";
            case InitData.Ex_EURUSD:
                return "欧元兑美元";
            case InitData.Ex_GBPUSD:
                return "英镑兑美元";
        }

        return "未记录名称";
    }

    public static String getSymbolExcode(String symbol) {
        switch (symbol) {
            case InitData.Ex_XAU:
            case InitData.Ex_XAG:
                return "WGJS";
            case InitData.Ex_CONC:
                return "NYMEX";
            case InitData.Ex_USD:
            case InitData.Ex_AUDUSD:
            case InitData.Ex_EURUSD:
            case InitData.Ex_GBPUSD:
                default:
                return "WH";
        }
    }
}
