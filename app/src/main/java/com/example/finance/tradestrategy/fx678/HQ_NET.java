package com.example.finance.tradestrategy.fx678;

import com.example.finance.tradestrategy.utils.ToolEncrypt;

/**
 * Created by Administrator on 2017/9/6.
 */

public class HQ_NET
{
    public static final String COLUMN_HQ_NEWS = "column_hq_news";
    public static final String COLUMN_HQ_NEWS_YB = "column_hq_news_yb";
    public static final String FLAG_YB = "flag_yb";
    public static final String MARKET_CUSTOM = "/fx678/1708/custom.php?ms=d149bfcaea2248bf3f563262785e01a7&code=";
    public static final String MARKET_KLINE = "/fx678/1708/kline.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=";
    public static final String MARKET_LIST = "/fx678/1708/list.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=";
    public static final String MARKET_PORT = "/fx678/1708";
    public static final String MARKET_PORT17 = "/fx678/17";
    public static final String MARKET_TIME = "/fx678/1708/time.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=";
    public static final String MARKET_TIME5 = "/fx678/1708/time5.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=";
    public static final String MD5_KEY_RED_MARKET = "key_fx678red_2099";
    public static final String PA_CODE = "PA_CODE";
    public static final String PA_CUSTOM = "custom";
    public static final String PA_CUSTOMS = "PA_CUSTOMS";
    public static final String PA_CUSTOMS_NAME = "PA_CUSTOMS_NAME";
    public static final String PA_EX = "PA_EX";
    public static final String PA_KEY = "PA_KEY";
    public static final String PA_MARK = "|";
    public static final String PA_MARK_HEX = "%7C";
    public static final String PA_TIME = "PA_TIME";
    public static final String PA_TOKEN = "PA_TOKEN";
    public static final String PA_TYPE = "PA_TYPE";
    public static final String SAFE_KEY_RED_MARKET = "d149bfcaea2248bf3f563262785e01a7";
    public static final String SECRET = "&time=PA_TIME&token=PA_TOKEN&key=PA_KEY";
    public static final String SP_UNIX_TIME = "unixtime";
    public static final String SP_UNIX_TIME_DIF = "unixtimedif";
    public static final String TOKEN = "6a066cff07860a54000cf04ea53ebfe3";
    public static final String TYPE_DAY = "day";
    public static final String TYPE_HOUR2 = "hr2";
    public static final String TYPE_HOUR4 = "hr4";
    public static final String TYPE_MIN1 = "min1";
    public static final String TYPE_MIN15 = "min15";
    public static final String TYPE_MIN30 = "min30";
    public static final String TYPE_MIN5 = "min5";
    public static final String TYPE_MIN60 = "min60";
    public static final String TYPE_MONTH = "month";
    public static final String TYPE_WEEK = "week";
    public static final String UDP_ACTIVE = "201";
    public static final String UDP_ACTIVE_ERROR = "203";
    public static final String UDP_ACTIVE_SUCCESS = "202";
    public static final String UDP_LOGIN = "101,PA_EX";
    public static final String UDP_LOGOUT = "301";
    private static final String UDP_MHTH_IP = "marketudp.fx678red.com";
    public static final int UDP_MHTH_PORT = 26001;
    private static final String UDP_YB_IP = "wenmarketudp.fx678red.com";
    public static final String URL = "https://market.fx678red.com";
    private static final String YB_URL = "https://wenmarket.fx678red.com";
    public static String[] warnSounds = { "", "warn01", "warn02", "warn03", "warn04", "warn05", "warn06", "" };

    public static String getDomain(String paramString)
    {
        if (paramString.equals("flag_yb"))
            return "https://wenmarket.fx678red.com";
        return "https://market.fx678red.com";
    }

    public static String getKey(String paramString)
    {
        return ToolEncrypt.md5Encrypt("d149bfcaea2248bf3f563262785e01a7" + paramString + "key_fx678red_2099");
    }

    public static String getUDP_IP(String paramString)
    {
        if (paramString.equals("flag_yb"))
            return "wenmarketudp.fx678red.com";
        return "marketudp.fx678red.com";
    }

    public static String getUrlMarketCustom(String paramString1, String paramString2, String paramString3, String paramString4)
    {
        return paramString1 + "/fx678/1708/custom.php?ms=d149bfcaea2248bf3f563262785e01a7&code=" + paramString2 + "&time=" + paramString3 + "&key=" + paramString4;
    }

    public static String getUrlMarketKline(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
    {
        return paramString1 + "/fx678/1708/kline.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=" + paramString2 + "&code=" + paramString3 + "&type=" + paramString4 + "&t=" + paramString5 + "&time=" + paramString6 + "&key=" + paramString7;
    }

    public static String getUrlMarketList(String paramString1, String paramString2, String paramString3, String paramString4)
    {
        return paramString1 + "/fx678/1708/list.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=" + paramString2 + "&time=" + paramString3 + "&key=" + paramString4;
    }

    public static String getUrlMarketTime(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt)
    {
        if (paramInt == 1)
            return paramString1 + "/fx678/1708/time5.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=" + paramString2 + "&code=" + paramString3 + "&time=" + paramString4 + "&key=" + paramString5;
        return paramString1 + "/fx678/1708/time.php?ms=d149bfcaea2248bf3f563262785e01a7&excode=" + paramString2 + "&code=" + paramString3 + "&time=" + paramString4 + "&key=" + paramString5;
    }
}