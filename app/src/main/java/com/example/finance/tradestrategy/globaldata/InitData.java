package com.example.finance.tradestrategy.globaldata;

/**
 * Created by Administrator on 2017/6/1.
 */

public interface InitData {

    //排行榜类型
    String RankTypeRevenue  = "REVENUE";     //收益
    String RankTypeWinRatio = "WINRATIO";   //胜率

    //时间范围
    String TimeTypeDate    = "DATE";     //按日
    String TimeTypeWeek    = "WEEK";     //按周
    String TimeTypeMonth   = "MONTH";   //按月
    String TimeTypeWhole   = "WHOLE";    //30天

    //信息值：containFut
    String ContainAll = "ALL";   //所有


    String RecordStatusDraft  = "DRAFT";    //持有
    String RecordStatusFinish  = "FINISH";    //卖出

    //多空
    String BuyTypeBull = "BULL";    //多
    String BuyTypeBear = "BEAR";    //空



    //sharedPreferences name
    String  TigerStockCodes     = "tigerStockCodes";    //监控的股票代码
    String  SpKeyUserLoginInfo = "userLoginInfo";       //用户账号信息



    //页面跳转标记
    String  ACT_MARK_MAIN_ACT   =   "act_main";



    //汇通周期类型
    String PERIOD_LINE_1_MINUTE  = "min1";         //一分
    String PERIOD_LINE_5_MINUTE  = "min5";         //五分
    String PERIOD_LINE_15_MINUTE = "min15";        //十五分
    String PERIOD_LINE_30_MINUTE = "min30";        //三十分
    String PERIOD_LINE_60_MINUTE = "min60";        //六十分
    String PERIOD_LINE_2_HOUR    = "hr2";           //两小时
    String PERIOD_LINE_4_HOUR    = "hr4";           //四小时
    String PERIOD_LINE_DAY        = "day";          //天
    String PERIOD_LINE_WEEK       = "week";         //周
    String PERIOD_LINE_MONTH      = "month";        //月


    //股票代码
    public String Ex_USD = "USD";           //美元指数
    public String Ex_XAU = "XAU";           //现货黄金
    public String Ex_XAG = "XAG";           //现货白银
    public String Ex_CONC = "CONC";         //美原油连续
    public String Ex_AUDUSD = "AUDUSD";     //澳元
    public String Ex_EURUSD = "EURUSD";     //欧元
    public String Ex_GBPUSD = "GBPUSD";     //英镑



}
