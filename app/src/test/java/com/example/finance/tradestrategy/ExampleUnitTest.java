package com.example.finance.tradestrategy;

import android.support.v4.util.ArrayMap;

import com.example.finance.tradestrategy.entity.StockStrategy;
import com.example.finance.tradestrategy.fx678.HQ_NET;
import com.example.finance.tradestrategy.utils.ToolData;
import com.example.finance.tradestrategy.utils.ToolDigitFormat;
import com.example.finance.tradestrategy.utils.ToolGson;
import com.example.finance.tradestrategy.utils.ToolMath;
import com.example.finance.tradestrategy.utils.ToolTime;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testData() {
        String time = "1504605934";
        String key = HQ_NET.getKey("WH" + "EURUSD" + "min5" + time);
        System.out.println(key);
    }


    protected String byteArrayToHexString(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer(2 * paramArrayOfByte.length);
        int i = paramArrayOfByte.length;
        for (int j = 0; ; j++)
        {
            if (j >= i)
                return localStringBuffer.toString().toUpperCase();
            int k = 0xFF & paramArrayOfByte[j];
            if (k < 16)
                localStringBuffer.append('0');
            localStringBuffer.append(Integer.toHexString(k));
        }
    }


    protected byte[] hexStringToByteArray(String paramString)
    {
        int i = paramString.length();
        byte[] arrayOfByte = new byte[i / 2];
        for (int j = 0; ; j += 2)
        {
            if (j >= i)
                return arrayOfByte;
            arrayOfByte[(j / 2)] = ((byte)((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16)));
        }
    }

    @Test
    public void timeConvert() {
        long t = ToolTime.getMinuteStartInMilli();
        t = ToolTime.getDayStartTime(21, 30);
        System.out.println(t);
    }

    @Test
    public void gsonTest() {
//        Set<String> datas = new ArraySet<>(5);
        List<String> datas = new ArrayList<>(5);
        datas.add("aaa");
        datas.add("bbb");
        datas.add("ccc");
        datas.add("ddd");
        datas.add("eee");

        String value = ToolGson.castObjectJson(datas);
        List<String> reflectDatas = ToolGson.castJsonObjList2(value, String.class);

        System.out.println(value);
        for (Iterator iterator = reflectDatas.iterator() ; iterator.hasNext();) {
            System.out.println("value:" + iterator.next().toString());
        }

        for (int i = 0; i < reflectDatas.size(); ++i) {
            System.out.println("value:" + reflectDatas.get(i));
        }
    }


    @Test
    public void referenceTest() {
        LinkedHashMap<Integer, StockStrategy> datas = new LinkedHashMap<>(5);
        datas.put(0, new StockStrategy("aaaaa", "a", 0));
        datas.put(3, new StockStrategy("bbbbbbb", "b", 0));
        datas.put(1, new StockStrategy("cccccccc", "c", 0));
        datas.put(7, new StockStrategy("ddddddd", "d", 0));
        datas.put(9, new StockStrategy("eeeee", "e", 0));
        datas.put(14, new StockStrategy("ffffffff", "f", 0));
        datas.put(2, new StockStrategy("gggggg", "g", 0));

//        StockStrategy stockStrategy = datas.get(0);
//        testUp(stockStrategy, datas.get(1));
////        testUp(stockStrategy, datas.get(2));
//        datas.get(1).setNameCN("ddddd");
//        datas.get(1).setSymbol("ddddd");
        for (StockStrategy stock : datas.values()) {
            System.out.println("value:" + stock.getNameCN() + "  symbol:" + stock.getSymbol());
        }

    }

    private void testUp(StockStrategy stockStrategy, StockStrategy stockStrategy2) {
        stockStrategy = stockStrategy2;
    }

    //macd = {TradeInfo$MACD@5875} "MACD{dea=3.1880157, diff=3.084097, macd=-0.20783758}"
//    preMacd = {TradeInfo$MACD@5871} "MACD{dea=3.2139955, diff=3.2223282, macd=0.016665459}"
//    third   MACD{dea=3.2119122, diff=3.348938, macd=0.27405167}

//    MACD{dea=-0.008397465, diff=-0.0019836426,
//            MACD{dea=-0.006980614, diff=-0.0013132095,
    @Test
    public void linesIntersect() {
        float [] line1S = {0, 1.3185124f};
        float [] line1E = {1, 1.3186232f};
        float [] line2S = {0, 1.3185239f};
        float [] line2E = {1, 1.3185817f};

        boolean isIntersect = doLinesIntersect(line1S[0], line1S[1], line1E[0], line1E[1], line2S[0], line2S[1], line2E[0], line2E[1]);
        boolean isSegmentIntersect = get_line_intersection(line1S[0], line1S[1], line1E[0], line1E[1], line2S[0], line2S[1], line2E[0], line2E[1]);
        boolean is3 = false;//ToolMath.isSegmentsIntersection(line1S[0], line1S[1], line1E[0], line1E[1], line2S[0], line2S[1], line2E[0], line2E[1]);

        System.out.println("is intersect:" + isIntersect + "   test2:" + isSegmentIntersect + "  test3:" + is3);
    }

    //线相交
    private boolean doLinesIntersect(float x1, float y1,
                                     float x2, float y2,
                                     float x3, float y3,
                                     float x4, float y4) {
        if (x1 == x2) {
            return !(x3 == x4 && x1 != x3);
        } else if (x3 == x4) {
            return true;
        } else {
            // Both lines are not parallel to the y-axis
            float m1 = (y1-y2)/(x1-x2);
            float m2 = (y3-y4)/(x3-x4);
            return m1 != m2;
        }
    }

    //线段相交
    private boolean get_line_intersection(float p0_x, float p0_y, float p1_x, float p1_y,
                               float p2_x, float p2_y, float p3_x, float p3_y)
    {
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

        float s, t;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            // Collision detected
//            float intersectX = p0_x + (t * s1_x);
//            float intersectY = p0_y + (t * s1_y);
            return true;
        }

        return false; // No collision
    }

    /**
     * map遍历时候被修改是否有影响
     * 结果：没有影响，正常显示修改值
     */
    Map<String, Long> mRequestPeriod = new ArrayMap<>(10);
    @Test
    public void testDecimalPlace() {
        System.out.println(ToolDigitFormat.decimalPlace(19.854f));
        System.out.println(ToolDigitFormat.decimalPlace(19.85f));
        System.out.println(ToolDigitFormat.decimalPlace(19.856f));
        System.out.println(ToolDigitFormat.decimalPlace(19.859f));
        System.out.println(ToolDigitFormat.decimalPlace(19.8f));


        for (long i = 0; i < 3000; ++i) {
            mRequestPeriod.put("" + i, i);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (long i = 0; i < 3000; ++i) {
                    mRequestPeriod.put("" + i, i+1000);
                }
            }
        }).start();
        for (Map.Entry<String, Long> entry : mRequestPeriod.entrySet()) {
            System.out.println(entry.getKey() + "    " + entry.getValue());
        }
    }

    @Test
    public void reduceData() {
        List<String> list = new ArrayList<>(100);
        for (int i = 0; i < 100; ++i) {
            list.add("i = " + i);
        }

        ToolData.dataCompactionFromHead(list, 70, 20);

        for (int i = 0; i < list.size(); ++i) {
            System.out.println(list.get(i));
        }

    }

    @Test
    public void getScientificNotationImpower() {

    }
}