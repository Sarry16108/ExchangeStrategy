package com.example.finance.tradestrategy.globaldata;

/**
 * Created by Administrator on 2017/6/1.
 */

public interface InitNetInfo {

    //post or get
    String  MODE_POST   = "POST";
    String  MODE_GET   = "GET";

    //验证域名
    String  SERVER_AUTHENTICATION = "oauth1.tigerbrokers.com";

    String  AUTHEN_API_V4 = "api/v4/auth/guest";


    //老虎域名
    String  SERVER_HOST = "hq.tigerbrokers.com";

    //功能类型
    String STOCK_INFO = "";       //详细信息
    String FUNDAMENTAL = "fundamental";     //公司相关信息

    //查找code
    String SEARCH_SUGGEST_V4 = "/suggest/v4";               //查找相关

    //更新图形数据获取所需要的凭证
    String STOCK_INFO_COOKIE = "/stock_info/brief/all";


    //趋势图
    String TIME_TREND_DAY     = "/stock_info/time_trend/day";            //天


    //蜡烛图周期类型
    String PERIOD_CANDLE_PREFIX_MINUTE  = "/stock_info/candle_stick/";
    String PERIOD_CANDLE_1_MINUTE  = "/stock_info/candle_stick/1min";         //一分
    String PERIOD_CANDLE_5_MINUTE  = "/stock_info/candle_stick/5min";         //五分
    String PERIOD_CANDLE_15_MINUTE = "/stock_info/candle_stick/15min";        //十五分
    String PERIOD_CANDLE_30_MINUTE = "/stock_info/candle_stick/30min";        //三十分
    String PERIOD_CANDLE_60_MINUTE = "/stock_info/candle_stick/60min";        //六十分
    String PERIOD_CANDLE_DAY        = "/stock_info/candle_stick/day";          //天
    String PERIOD_CANDLE_WEEK       = "/stock_info/candle_stick/week";         //周
    String PERIOD_CANDLE_MONTH      = "/stock_info/candle_stick/month";        //月
    String PERIOD_CANDLE_YEAR       = "/stock_info/candle_stick/year";         //年

}
