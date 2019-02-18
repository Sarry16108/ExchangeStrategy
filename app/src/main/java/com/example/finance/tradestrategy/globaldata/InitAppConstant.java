package com.example.finance.tradestrategy.globaldata;

/**
 * Created by Administrator on 2017/6/9.
 */

public interface InitAppConstant {

//提醒级别
    int     FORECAST_LEVEL_NEUTRAL_BULLISH = 1;   //微涨
    int     FORECAST_LEVEL_BULLISH = 2;            //看涨
    int     FORECAST_LEVEL_ULTRA_BULLISH = 3;      //暴涨        看多点
    int     FORECAST_LEVEL_NEUTRAL = 4;            //观望
    int     FORECAST_LEVEL_NEUTRAL_BEARISH = 5;    //微跌
    int     FORECAST_LEVEL_BEARISH = 6;            //看跌
    int     FORECAST_LEVEL_ULTRA_BEARISH = 7;      //暴跌         看空点
    int     FORECAST_LEVEL_COUNT = 8;

    //多空交易类型
    int     FORECAST_BULL_BEAR  = 0;        //保持当前状态：买入状态，空闲状态
    int     FORECAST_BULL_BUY   = 1;        //看多买入
    int     FORECAST_BEAR_BUY   = 2;        //看空买入
    int     FORECAST_BULL_SELL  = 3;        //看多卖出
    int     FORECAST_BEAR_SELL  = 4;        //看空卖出

    //策略类型
    int     STRATEGORY_TYPE_START                            = 0;     //策略开始
    int     STRATEGORY_TYPE_UTRAL_MACD                      = 1;     //超买超卖类型
    int     STRATEGORY_TYPE_UTRAL_INTERSECT                 = 2;     //离开超买超卖区交点类型
    int     STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION     = 3;     //超买超卖区有交点，离开后的线方向
    int     STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION2     = 4;     //超买超卖区有交点，离开后的线方向
    int     STRATEGORY_TYPE_UTRAL_INTERSECT_DIRECTION3     = 5;     //超买超卖区有交点，离开后的线方向

    //第二类
    int     STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT        = 10;     //BOLL强弱区和macd金死叉
    int     STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT2       = 11;     //macd金死叉和股价趋势
    int     STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT3       = 12;     //强弱区macd金sicha
    int     STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT4       = 13;     //macd交点前后，价格在boll间变化
    int     STRATEGORY_TYPE_BOLL_ZONE_MACD_INTERSECT5       = 14;     //macd交点前后，价格在boll间变化

    //第三类
    int     STRATEGORY_TYPE_MACD_DIFF_OVERTURN              = 20;     //macd的diff拐点出现，代表转势
    int     STRATEGORY_TYPE_MACD_DIFF_OVERTURN2             = 21;     //macd的diff拐点出现，且处于boll极强势或极弱势附近
    int     STRATEGORY_TYPE_MACD_DIFF_OVERTURN3             = 22;     //macd的diff拐点出现，且mcd值>0.1
    int     STRATEGORY_TYPE_MACD_DIFF_OVERTURN4             = 23;     //macd的diff拐点出现，且距离上个macd交点有一定距离
    int     STRATEGORY_TYPE_MACD_DIFF_OVERTURN5             = 24;     //macd的diff拐点出现，且看多交易量上涨，看空交易量减少

    //第四类
    int     STRATEGORY_TYPE_MACD_DISTANCE_INTERSECT         = 30;     //macd两个交点的距离，越来越短则很大程度上趋势不变，
                                                                          //当交点距离比上一个点大，且大过最小间距，那么认为改变趋势
    //第五类：ma
    int STRATEGORY_TYPE_MA_5_10_20_INTERSECT                = 40;      //看空：ma依次下交叉10,20，且方向向下，此时macd < 0，且值依次减小。
                                                                            //多则反之。
    int STRATEGORY_TYPE_MA_MACD_OVERTURN                     = 41;      //ma的10拐点在ma5_10和ma5_20之间和macd同趋势
    int STRATEGORY_TYPE_MA_MACD_SAME_DIRECTION              = 42;       //ma5_10交点后走向和20相同
    int STRATEGORY_TYPE_MA_MACD_SAME_SIDE                    = 43;      //ma5_10交点和macd同时（或者相邻）发生，与macd的趋势相同。
    int STRATEGORY_TYPE_MA_SAME_INTERSECT                    = 44;      //ma5_10_20同时发生，与macd的趋势相同。
    int STRATEGORY_TYPE_MA_INTERSECT_20_40                   = 45;      //ma20_40相交
    int STRATEGORY_TYPE_MA_INTERSECT_10_20                   = 46;      //ma10_20相交
    int STRATEGORY_TYPE_MA_5_OVERTURN_10_INTERSECT = 47;      //ma5_10相交

    //第六类：ma & boll
    int STRATEGORY_TYPE_BOLL_GOLEN_SPERATOR                  = 50;      //价格区间在boll的黄金分割交叉及方向判断
    int STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT           = 51;      //ma5与boll的黄金分割线交叉及方向判断
    int STRATEGORY_TYPE_MA_5_BOLL_GOLEN_INTERSECT_START    = 52;      //ma5与boll的黄金分割线交叉及方向判断最初动向
    int STRATEGORY_TYPE_MA_5_20_BOLL_GOLEN                   = 53;      //ma5_20交点前一个或者前两个点的open/close在boll的黄金分割线中位置判定
    int STRATEGORY_TYPE_CANDLE_BOLL_GOLEN                    = 54;      //open/close在boll中的分布
    int STRATEGORY_TYPE_BOLL_MD                               = 55;      //boll md的分析

    //第七类：ema的使用
    int STRATEGORY_TYPE_EMA_1_2_INTERSECT                    = 60;      //ema的快中线交点及方向

    int STRATEGORY_TYPE_SELL_MA_10_OVERTURN                 = 100;      //当ma10拐弯弧度达到多少时候开始考虑


    int     STRATEGORY_TYPE_END                                = 101;     //策略结束


//请求时间类型
    int     MINUTE_1 = 1;
    int     MINUTE_5 = 2;
    int     MINUTE_15 = 3;
    int     MINUTE_30 = 4;
    int     MINUTE_60 = 5;


//相邻两个时间段，a、b两根线的形态（a参考线即慢线, b即快线），除重合外，向上都包括水平
//INTERSECT_LEFT_UP_、INTERSECT_LEFT_DOWN_、INTERSECT_RIGHT_UP_、INTERSECT_RIGHT_DOWN_是b快线，后缀是参考线（慢线）形态
    int     INTERSECT_COINCIDE_UP       = 1;    //和慢线重合向上
    int     INTERSECT_COINCIDE_DOWN     = 2;    //和慢线重合向下
    int     INTERSECT_COINCIDE_HORI     = 3;    //和慢线重合水平

//    int     INTERSECT_LEFT               = 3;   //两条线段在交点左侧，即交叉前
    int     INTERSECT_LEFT_UP_UP        = 4;    //交叉前两线都向上
    int     INTERSECT_LEFT_UP_DOWN      = 5;    //交叉前，快线向上，慢线向下
    int     INTERSECT_LEFT_DOWN_UP      = 6;    //交叉前，快线向下，慢线向上
    int     INTERSECT_LEFT_DOWN_DOWN    = 7;    //两线都向下

//    int     INTERSECT_RIGHT              = 8;   //两条线段在交点右侧，即交叉后
    int     INTERSECT_RIGHT_UP_UP       = 9;     //交叉后两线都向上
    int     INTERSECT_RIGHT_UP_DOWN     = 10;     //相对于参考线向上交叉，快线向上，慢线向下
    int     INTERSECT_RIGHT_DOWN_UP     = 11;    //相对于参考线向下交叉，快线向下，慢线向上
    int     INTERSECT_RIGHT_DOWN_DOWN   = 12;    //交叉后两线都向下

    int     INTERSECT_NO                 = 0;   //和参考线没有相交，平行
    int     INTERSECT_NO_UP_UP          = 13;   //快线、慢线都向上
    int     INTERSECT_NO_UP_DOWN        = 14;   //快线向上，慢线向下
    int     INTERSECT_NO_DOWN_UP        = 15;   //快线向下，慢线向上
    int     INTERSECT_NO_DOWN_DOWN      = 16;   //快线向下，慢线向下
}
