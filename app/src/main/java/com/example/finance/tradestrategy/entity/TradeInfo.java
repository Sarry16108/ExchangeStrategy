package com.example.finance.tradestrategy.entity;

import android.text.TextUtils;

import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.utils.ToolDigitFormat;
import com.example.finance.tradestrategy.utils.ToolTime;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 *
 * 汇通网外汇数据：{"e":"WH","d":"USD","o":"92.4531","h":"92.6579","l":"92.4313","c":"92.5270","p":"92.4531","t":"1505964709"}
 */

public class TradeInfo extends BaseResponse {
    public TradeInfo() {
    }

    //价格相关
    @SerializedName("o")
    private float open;         //开价
    @SerializedName("c")
    private float close;        //收价
//    private float price;        //当前
    @SerializedName("h")
    private float high;         //最高价
    @SerializedName("l")
    private float low;          //最低价
    private int   volume;       //成交量

    private String   xLabel;       //x轴时间标签

    //汇通分钟时间点是指一段时间结束时候的时间，而且是包头不包尾。
    //比如1分钟图，时间点是1507174620，它代表的数据是[1507174560,1507174620)1分钟的数据值
    @SerializedName("t")
    private long  time;
//    private int   buySell = InitAppConstant.FORECAST_BULL_BEAR;      //交易动作
//    private int     strategyType;       //策略类型

    //open和close相比较起来，entityTop是相对大的那个值，entityBot是相对小的那个值
    private float entityTop = 0;
    private float entityBot = 0;


    /////////////////////////////////////////////////
    // 测试模块内容。。。

    //策略组，只要size不为0，则代表有购买的策略
    private List<Integer> buyBullStrategies = new ArrayList<>(2);
    private List<Integer> buyBearStrategies = new ArrayList<>(2);

    //策略组，只要size不为0，则代表有卖出的策略
    private List<Integer> sellBullStrategies = new ArrayList<>(2);
    private List<Integer> sellBearStrategies = new ArrayList<>(2);

    //////////////////////////////////////////////////
    /**
     * 自定义 K 线图用的数据
     *
     * @param open 开盘价
     * @param high 最高价
     * @param low 最低价
     * @param close 收盘价
     * @param volume 量
     * @param time 时间
     */
    public TradeInfo(float open, float high, float low, float close, int volume, long time) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.time = time;
    }

    @Override
    public String toString() {
        return "TradeInfo{" +
                "time=" + getXLabel() +
                ", open=" + open +
                ", close=" + close +
//                ", price=" + price +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                '}';
    }

    //多种指标
    private Boll        boll;         //布林线
    private MA          ma;           //移动平均线
    private TBRegion    tbRegion;    //股价上下边缘平均线
    private MACD        macd;         //指数平滑移动平均线
    private KDJ         kdj;          //随机指标
    private RSI         rsi;          //相对强弱指数
    private OBV         obv;           //能量指标
    private Shock       shock;         //震荡指标
    private SMA         sma;            //加权移动平均
    private EMA         ema;            //指数平滑移动平均

    //指标类
    public static class Boll {
        private final float RATIO = 2;      //股票特性
        private final float RATIO_0382 = 0.382f;      //0.382
        private final float RATIO_0618 = 0.618f;      //0.618
        private final float RATIO_1382 = 1.382f;      //1.382
        private final float RATIO_1618 = 1.618f;      //1.382


        private float md;       //标准差
        private float upper;    //上2
        private float up;       //上1
        private float mb;       //中轨
        private float dn;       //下轨1
        private float dner;     //下轨2

        private float width;    //宽比   WIDTH=（布林上限值－布林下限值）/布林股价平均值
        private float b;        //位置  %b指标 （收盘价-布林线下轨价格）/（布林线上轨价格-布林线下轨价格）

        //黄金分割点的上下轨
        private float goldenUp1618;    //1.618
        private float goldenUp1382;    //1.382
        private float goldenUp0618;    //0.618
        private float goldenUp0382;    //0.382

        private float goldenDown0382;  //0.382
        private float goldenDown0618;  //0.618
        private float goldenDown1382;  //1.382
        private float goldenDown1618;  //1.618

        /** 只传mb和md，其他的各值在此方法内计算的处
         *  便于扩展，方便
         * @param mb  中轨值
         * @param md  标准差
         * @param close 收盘价
         */
        public Boll(float mb, float md, float open, float close) {
            this.mb = mb;
            this.md = md;

            this.upper = mb + RATIO * md;;
            this.up = mb + md;
            this.dn = mb - md;
            this.dner = mb - RATIO * md;
            this.width = 2 * RATIO * md / mb;
            this.b = 0 != md ? (close - this.dner) / (2 * RATIO * md) : 0;

            //目前此值用在外汇中，
            //精度调节数值，最大调整为0.00005f，相当于四舍五入值，正常为md值的1/5，减小对差量不一的的误差
            float adjustion = Math.min(0.00005f, md / 10);

            this.goldenUp1618 = mb + RATIO_1618 * md - adjustion;
            this.goldenUp1382 = mb + RATIO_1382 * md - adjustion;
            this.goldenUp0618 = mb + RATIO_0618 * md - adjustion;
            this.goldenUp0382 = mb + RATIO_0382 * md - adjustion;

            this.goldenDown0382 = mb - RATIO_0382 * md + adjustion;
            this.goldenDown0618 = mb - RATIO_0618 * md + adjustion;
            this.goldenDown1382 = mb - RATIO_1382 * md + adjustion;
            this.goldenDown1618 = mb - RATIO_1618 * md + adjustion;
        }

        public float getMd() {
            return md;
        }

        public void setMd(float md) {
            this.md = md;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getUp() {
            return up;
        }

        public void setUp(float up) {
            this.up = up;
        }

        public float getDn() {
            return dn;
        }

        public void setDn(float dn) {
            this.dn = dn;
        }

        public float getMb() {
            return mb;
        }

        public void setMb(float mb) {
            this.mb = mb;
        }

        public float getUpper() {
            return upper;
        }

        public void setUpper(float upper) {
            this.upper = upper;
        }

        public float getDner() {
            return dner;
        }

        public void setDner(float dner) {
            this.dner = dner;
        }

        public float getB() {
            return b;
        }

        public float getGoldenDown0382() {
            return goldenDown0382;
        }

        public float getGoldenDown0618() {
            return goldenDown0618;
        }

        public float getGoldenDown1382() {
            return goldenDown1382;
        }

        public float getGoldenDown1618() {
            return goldenDown1618;
        }

        public float getGoldenUp0382() {
            return goldenUp0382;
        }

        public float getGoldenUp0618() {
            return goldenUp0618;
        }

        public float getGoldenUp1382() {
            return goldenUp1382;
        }

        public float getGoldenUp1618() {
            return goldenUp1618;
        }

        @Override
        public String toString() {
            return "Boll{" +
                    "mb=" + mb +
                    ", md=" + md +
                    ", md=" + ToolDigitFormat.formatFloatStr(md) +
                    ", goldenUp1618=" + goldenUp1618 +
                    ", goldenUp1382=" + goldenUp1382 +
                    ", goldenUp0618=" + goldenUp0618 +
                    ", goldenUp0382=" + goldenUp0382 +
                    ", goldenDown0382=" + goldenDown0382 +
                    ", goldenDown0618=" + goldenDown0618 +
                    ", goldenDown1382=" + goldenDown1382 +
                    ", goldenDown1618=" + goldenDown1618 +
                    '}';
        }
    }

    public static class MA {
        private float ma1;      //5日
        private float ma2;      //10日
        private float ma3;      //20日
        private float ma4;      //40日

        public boolean ma5_10 = false; //5日线和10日线相交
        public boolean ma10_20 = false; //10日线和20日线相交
        public boolean ma5_20 = false; //5日和20日相交
        public boolean ma20_40 = false; //20日和40日相交

        public float dif1 = 0; //5日和前一个差值
        public float dif2 = 0; //10日和前一个差值
        public float dif3 = 0; //20日和前一个差值
        public float dif4 = 0; //40日和前一个差值

        public MA(float ma1, float ma2, float ma3, float ma4) {
            this.ma1 = ma1;
            this.ma2 = ma2;
            this.ma3 = ma3;
            this.ma4 = ma4;
        }

        public float getMa1() {
            return ma1;
        }

        public void setMa1(float ma1) {
            this.ma1 = ma1;
        }

        public float getMa2() {
            return ma2;
        }

        public void setMa2(float ma2) {
            this.ma2 = ma2;
        }

        public float getMa3() {
            return ma3;
        }

        public void setMa3(float ma3) {
            this.ma3 = ma3;
        }

        public float getMa4() {
            return ma4;
        }

        public void setMa4(float ma4) {
            this.ma4 = ma4;
        }

        @Override
        public String toString() {
            return "MA{" +
                    "dif1=" + dif1 +
                    ", dif2=" + dif2 +
                    ", dif3=" + dif3 +
                    ", dif4=" + dif4 +
                    ", ma5_10=" + ma5_10 +
                    ", ma10_20=" + ma10_20 +
                    ", ma5_20=" + ma5_20 +
                    ", ma20_40=" + ma20_40 +
                    ", ma1=" + ma1 +
                    ", ma2=" + ma2 +
                    ", ma3=" + ma3 +
                    ", ma4=" + ma4 +
                    '}';
        }
    }

    public static class EMA {
        private float ema1;
        private float ema2;
        private float ema3;

        public boolean ema1_2 = false;
        public boolean ema2_3 = false;
        public boolean ema1_3 = false;

        public float diff1 = 0;

        public EMA() {
        }

        public EMA(float ema1, float ema2, float ema3) {
            this.ema1 = ema1;
            this.ema2 = ema2;
            this.ema3 = ema3;
        }

        public float getEma1() {
            return ema1;
        }

        public void setEma1(float ema1) {
            this.ema1 = ema1;
        }

        public float getEma2() {
            return ema2;
        }

        public void setEma2(float ema2) {
            this.ema2 = ema2;
        }

        public float getEma3() {
            return ema3;
        }

        public void setEma3(float ema3) {
            this.ema3 = ema3;
        }

        @Override
        public String toString() {
            return "EMA{" +
                    "ema1=" + ema1 +
                    ", ema2=" + ema2 +
                    ", ema3=" + ema3 +
                    ", ema1_2=" + ema1_2 +
                    ", ema2_3=" + ema2_3 +
                    ", ema1_3=" + ema1_3 +
                    '}';
        }
    }

    public static class SMA {
        private float sma1;
        private float sma2;
        private float sma3;

        public SMA(float sma1, float sma2, float sma3) {
            this.sma1 = sma1;
            this.sma2 = sma2;
            this.sma3 = sma3;
        }

        @Override
        public String toString() {
            return "SMA{" +
                    "sma1=" + sma1 +
                    ", sma2=" + sma2 +
                    ", sma3=" + sma3 +
                    '}';
        }
    }
    //顶底区间：top-bottom-region
    //用于计算变动上下边沿
    //目标是查出脱离稳定区域的方向
    public static class TBRegion {
        private float top;      //上边
        private float bot;      //下边

        public TBRegion(float top, float bot) {
            this.top = top;
            this.bot = bot;
        }

        public float getTop() {
            return top;
        }

        public float getBot() {
            return bot;
        }

        public void setBot(float bot) {
            this.bot = bot;
        }

        public void setTop(float top) {
            this.top = top;
        }

        @Override
        public String toString() {
            return "TBRegion{" +
                    "bot=" + bot +
                    ", top=" + top +
                    '}';
        }
    }


    public static class MACD {
        private float dea;
        private float diff;
        private float macd;

        //是否通知过
        public boolean isNotified = false;
        //金叉死叉交点
        public boolean hasIntersect = false;
        //该点距离交点的距离
        public int distance = 0;
        //之前相邻两个交点的距离
        public int preDistance = 0;
        //交点的类型
        public int feature = InitAppConstant.INTERSECT_NO;

        //是否拐角的关键点，即此处发生了方向翻转（由上转下/由下转上），切线方向应当水平。
        public boolean  isKeyPoint = false;
        //角度，当前点与上一个点在竖直方向的diff差值：diff-preDiff，因为水平差值一样，都是1,。
        public float angle;


        public MACD(float dea, float diff, float macd) {
            this.dea = dea;
            this.diff = diff;
            this.macd = macd;
        }

        public float getDea() {
            return dea;
        }

        public void setDea(float dea) {
            this.dea = dea;
        }

        public float getDiff() {
            return diff;
        }

        public void setDiff(float diff) {
            this.diff = diff;
        }

        public float getMacd() {
            return macd;
        }

        public void setMacd(float macd) {
            this.macd = macd;
        }

        @Override
        public String toString() {
            return "MACD{" +
                    "dea=" + ToolDigitFormat.formatFloatStr(dea) +
                    ", diff=" + ToolDigitFormat.formatFloatStr(diff) +
                    ", macd=" + ToolDigitFormat.formatFloatStr(macd) +
                    ", isKeyPoint=" + isKeyPoint +
                    ", angle=" + angle +
                    '}';
        }
    }

    public static class KDJ {
        private float k;
        private float d;
        private float j;
        private float average;

        //该时间段内特征计算
        // 本时间段内是否存在j<0或j>100次数
        public boolean  hasUltra = false;
        //当前是否在超买超卖区
        public boolean overBoughtOrSellZone = false;
        //本时间段内是否右交叉
        public boolean  hasIntersect = false;

        //历史统计次数
        //连续多少时间段ultraTimes不为0
        public int  continueUltraTimes = 0;
        //连续多少时间段intersectTimes不为0
        public int  continueIntersectTimes = 0;


        public KDJ(float k, float d, float j) {
            this.k = k;
            this.d = d;
            this.j = j;

            average = (this.k  + this.d + this.j) / 3;
        }

        public float getK() {
            return k;
        }

        public float getD() {
            return d;
        }

        public float getJ() {
            return j;
        }

        public float getAverage() {
            return average;
        }

        @Override
        public String toString() {
            return "KDJ{" +
                    "k=" + k +
                    ", d=" + d +
                    ", j=" + j +
                    ", average=" + average +
                    ", overBoughtOrSellZone=" + overBoughtOrSellZone +
                    ", hasUltra=" + hasUltra +
                    ", hasIntersect=" + hasIntersect +
                    ", continueUltraTimes=" + continueUltraTimes +
                    ", continueIntersectTimes=" + continueIntersectTimes +
                    '}';
        }
    }

    public static class RSI {
        private float rsi1;
        private float rsi2;
        private float rsi3;
        private boolean isReverse = false;  //是否达到极限，开始有翻转

        public RSI(float rsi1, float rsi2, float rsi3) {
            this.rsi1 = rsi1;
            this.rsi2 = rsi2;
            this.rsi3 = rsi3;

        }

        public float getRsi1() {
            return rsi1;
        }

        public float getRsi2() {
            return rsi2;
        }

        public float getRsi3() {
            return rsi3;
        }

        public boolean isReverse() {
            return isReverse;
        }

        public void setReverse(boolean reverse) {
            isReverse = reverse;
        }

        @Override
        public String toString() {
            return "RSI{" +
                    "rsi1=" + rsi1 +
                    ", rsi2=" + rsi2 +
                    ", rsi3=" + rsi3 +
                    ", isReverse=" + isReverse +
                    '}';
        }
    }

    public static class OBV {
        private float v;        //obv;
        private float netV;    //多空比率净额

        public OBV(float v, float netV) {
            this.v = v;
            this.netV = netV;
        }

        public float getV() {
            return v;
        }

        public float getNetV() {
            return netV;
        }

        @Override
        public String toString() {
            return "OBV{" +
                    "v=" + v +
                    ", netV=" + netV +
                    '}';
        }
    }

    public static class Shock {
        private float sh;

        public Shock(float sh) {
            this.sh = sh;
        }

        public float getSh() {
            return sh;
        }

        @Override
        public String toString() {
            return "Shock{" +
                    "sh=" + ToolDigitFormat.formatFloatStr(sh) +
                    '}';
        }
    }
    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Boll getBoll() {
        if (null == boll) {
            boll = new Boll(getClose(), 0, getOpen(), getClose());
        }
        return boll;
    }

    public void setBoll(Boll boll) {
        this.boll = boll;
    }

    public MA getMa() {
        if (null == ma) {
            ma = new MA(0, 0, 0, 0);
        }
        return ma;
    }

    public void setMa(MA ma) {
        this.ma = ma;
    }

    public MACD getMacd() {
        if (null == macd) {
            macd = new MACD(0, 0, 0);
        }
        return macd;
    }

    public void setMacd(MACD macd) {
        this.macd = macd;
    }

    public KDJ getKdj() {
        if (null == kdj) {
            kdj = new KDJ(0, 0, 0);
        }
        return kdj;
    }

    public void setKdj(KDJ kdj) {
        this.kdj = kdj;
    }

    public RSI getRsi() {
        if (null == rsi) {
            rsi = new RSI(0, 0, 0);
        }
        return rsi;
    }

    public void setRsi(RSI rsi) {
        this.rsi = rsi;
    }

    public OBV getObv() {
        return obv;
    }

    public void setObv(OBV obv) {
        this.obv = obv;
    }

    public String getXLabel() {
        if (TextUtils.isEmpty(xLabel)) {
            xLabel = ToolTime.getMDHM(this.time);
        }
        return xLabel;
    }

    public void setTbRegion(TBRegion tbRegion) {
        this.tbRegion = tbRegion;
    }

    public TBRegion getTbRegion() {
        if (null == tbRegion) {
            tbRegion = new TBRegion(0, 0);
        }

        return tbRegion;
    }

    public Shock getShock() {
        return shock;
    }

    public void setShock(Shock shock) {
        this.shock = shock;
    }

    public void setEntityTop(float entityTop) {
        this.entityTop = entityTop;
    }

    public void setEntityBot(float entityBot) {
        this.entityBot = entityBot;
    }

    public float getEntityTop() {
        return entityTop;
    }

    public float getEntityBot() {
        return entityBot;
    }

    public SMA getSma() {
        return sma;
    }

    public void setSma(SMA sma) {
        this.sma = sma;
    }

    public EMA getEma() {
        if (null == ema) {
            ema = new EMA(this.close, this.close, this.close);
        }
        return ema;
    }

    public void setEma(EMA ema) {
        this.ema = ema;
    }

    // TODO: 2017/8/21 改为和StockStrategy记录方式一样，更精确
//
//    public int getBuySell() {
//        return buySell;
//    }
//
//    public void setBuySell(int buySell, int strategoryType) {
//        this.buySell = buySell;
//        this.strategyType = strategoryType;
//    }
//
//    public int getStrategyType() {
//        return strategyType;
//    }
//
//    public void resetData() {
//        this.buySell = InitAppConstant.FORECAST_BULL_BEAR;
//        this.strategyType = InitAppConstant.FORECAST_BULL_BEAR;
//    }


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


    /**
     * 是否购买
     */
    public boolean isBuy() {
        return buyBullStrategies.size() > 0 || buyBearStrategies.size() > 0;
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
        return sellBullStrategies.size() > 0 || sellBearStrategies.size() > 0;
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
