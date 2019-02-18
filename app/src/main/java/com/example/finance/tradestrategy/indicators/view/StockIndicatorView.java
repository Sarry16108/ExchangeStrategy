package com.example.finance.tradestrategy.indicators.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.example.finance.tradestrategy.entity.TradeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */
@Deprecated
public class StockIndicatorView extends View {

    //是否显示这种图
    private boolean isShowCandle = false;
    private boolean isShowMa = false;
    private boolean isShowMacd = false;
    private boolean isShowBoll = false;
    private boolean isShowKdj = false;
    private boolean isShowObv = false;
    private boolean isShowRsi = false;

    private List<TradeInfo>     mInfoList;

    //最后一条的位置
    private int                 mEnd = 0;

    //显示区域大小
    private int         mWidth = 0;
    private int         mHeight = 0;

    //(mWidth + mHeight) / 2
    private int         mCenterVertical = 0;

    //单条数据的高、宽
    private int         mItemWidth = 0;
    private int         mItemHeight = 0;

    //当前屏幕可显示的条数
    private int mCount = 0;

    //可显示区高价和最低价
    private float mHighest = 0;
    private float mLowest = 0;

    //显示的缩放比例
    private float         mScale = 1;

    //每条数据的单位宽高
    private final int     mWidthUnit = 10;
    private final int     mHeightUnit = 20;

    public StockIndicatorView(Context context) {
        this(context, null);
    }

    public StockIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StockIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateData(List<TradeInfo> list) {
        mInfoList = list;
        mEnd = list.size() - 1;

        searchHighestLowestPrice(list, mEnd - mCount, mEnd);


        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mItemWidth = (int) (mScale * mWidthUnit);
        mCount = mWidth / mItemWidth;
        mItemHeight = (int) ((mHighest - mLowest) / 10);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null == mInfoList) {
            return;
        }

        //从后往前画
        for (int i = mEnd; i >= mEnd - mCount; i--) {
            TradeInfo tradeInfo = mInfoList.get(i);

            if (isShowCandle) {
                drawCandleStick(canvas, tradeInfo);
            }
            if (isShowMa) {
                drawMa(canvas, tradeInfo.getMa());
            }
            if (isShowMacd) {
                drawMacd(canvas, tradeInfo.getMacd());
            }
            if (isShowBoll) {
                drawBoll(canvas, tradeInfo.getBoll());
            }
            if (isShowKdj) {
                drawKdj(canvas, tradeInfo.getKdj());
            }
            if (isShowObv) {
                drawObv(canvas, tradeInfo.getObv());
            }
            if (isShowRsi) {
                drawRsi(canvas, tradeInfo.getRsi());
            }

        }

    }

    private void searchHighestLowestPrice(List<TradeInfo> list, int startIndext, int endIndex) {
        for (int i = startIndext; i < endIndex; ++i) {
            TradeInfo tradeInfo = list.get(i);

            if (tradeInfo.getHigh() > mHighest) {
                mHighest = tradeInfo.getHigh();
            }
            if (tradeInfo.getLow() < mLowest) {
                mLowest = tradeInfo.getLow();
            }
        }
    }

    /**
     * 蜡烛图
     */
    private void drawCandleStick(Canvas canvas, TradeInfo tradeInfo) {

    }

    /**
     * MACD
     * @param canvas
     */
    private void drawMacd(Canvas canvas, TradeInfo.MACD macd) {

    }

    /**
     * KDJ
     * @param canvas
     */
    private void drawKdj(Canvas canvas, TradeInfo.KDJ kdj) {

    }

    /**
     * RSI
     * @param canvas
     */
    private void drawRsi(Canvas canvas, TradeInfo.RSI rsi) {

    }

    /**
     * BOLL
     * @param canvas
     */
    private void drawBoll(Canvas canvas, TradeInfo.Boll boll) {

    }

    /**
     * MA
     * @param canvas
     */
    private void drawMa(Canvas canvas, TradeInfo.MA ma) {

    }

    /**
     * OBV
     * @param canvas
     */
    private void drawObv(Canvas canvas, TradeInfo.OBV obv) {

    }
}
