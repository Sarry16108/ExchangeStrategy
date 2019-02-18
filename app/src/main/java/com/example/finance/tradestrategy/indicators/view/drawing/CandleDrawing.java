/*
 * Copyright (C) 2017 WordPlat Open Source Project
 *
 *      https://wordplat.com/InteractiveKLineView/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.finance.tradestrategy.indicators.view.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.finance.tradestrategy.databindings.DatabindingUtls;
import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.indicators.view.TradeInfoSet;
import com.example.finance.tradestrategy.indicators.view.ViewUtils;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.text.DecimalFormat;

/**
 * <p>CandleDrawing</p>
 * <p>Date: 2017/3/9</p>
 *
 * @author afon
 */

public class CandleDrawing implements IDrawing {
    private static final String TAG = "CandleDrawing";
    private static final boolean DEBUG = false;

    private Paint candlePaint; // 蜡烛图画笔
    private Paint extremumPaint; // 当前可见区域内的极值画笔
    private final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    private final RectF kLineRect = new RectF(); // K 线图显示区域
    private AbstractRender render;

    private float candleSpace = 0.1f; // entry 与 entry 之间的间隙，默认 0.1f (10%)
    private float extremumToRight;
    private float[] candleLineBuffer = new float[8]; // 计算 2 根线坐标用的
    private float[] candleRectBuffer = new float[4]; // 计算 1 个矩形坐标用的

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (candlePaint == null) {
            candlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            candlePaint.setStyle(Paint.Style.FILL);
            candlePaint.setStrokeWidth(sizeColor.getCandleBorderSize());
        }

        if (extremumPaint == null) {
            extremumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        extremumPaint.setTextSize(sizeColor.getCandleExtremumLabelSize());
        extremumPaint.setColor(sizeColor.getCandleExtremumLableColor());

        kLineRect.set(contentRect);

        extremumToRight = kLineRect.right - 150;
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {

    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        final TradeInfoSet entrySet = render.getTradeInfoSet();
        final SizeColor sizeColor = render.getSizeColor();

        canvas.save();
        canvas.clipRect(kLineRect);


        for (int i = minIndex; i < maxIndex; i++) {
            TradeInfo entry = ViewUtils.setUpCandlePaint(candlePaint, entrySet, i, sizeColor);

            // 绘制 影线
            candleLineBuffer[0] = i + 0.5f;
            candleLineBuffer[2] = i + 0.5f;
            candleLineBuffer[4] = i + 0.5f;
            candleLineBuffer[6] = i + 0.5f;
            if (entry.getOpen() > entry.getClose()) {
                candleLineBuffer[1] = entry.getHigh();
                candleLineBuffer[3] = entry.getOpen();
                candleLineBuffer[5] = entry.getClose();
                candleLineBuffer[7] = entry.getLow();
            } else {
                candleLineBuffer[1] = entry.getHigh();
                candleLineBuffer[3] = entry.getClose();
                candleLineBuffer[5] = entry.getOpen();
                candleLineBuffer[7] = entry.getLow();
            }
            render.mapPoints(candleLineBuffer);
            canvas.drawLines(candleLineBuffer, candlePaint);

            // 绘制 当前显示区域的"最小"与"最大"两个值
            //区间最大值最小值不再显示
            /*if (i == entrySet.getMinYIndex()) {
                if (candleLineBuffer[6] > extremumToRight) {
                    extremumPaint.setTextAlign(Paint.Align.RIGHT);

                    canvas.drawText(decimalFormatter.format(entry.getLow()) + " →",
                            candleLineBuffer[6],
                            candleLineBuffer[7] + 20,
                            extremumPaint);
                } else {
                    extremumPaint.setTextAlign(Paint.Align.LEFT);

                    canvas.drawText("← " + decimalFormatter.format(entry.getLow()),
                            candleLineBuffer[6],
                            candleLineBuffer[7] + 20,
                            extremumPaint);
                }
            }
            if (i == entrySet.getMaxYIndex()) {
                if (candleLineBuffer[0] > extremumToRight) {
                    extremumPaint.setTextAlign(Paint.Align.RIGHT);

                    canvas.drawText(decimalFormatter.format(entry.getHigh()) + " →",
                            candleLineBuffer[0],
                            candleLineBuffer[1] - 5,
                            extremumPaint);
                } else {
                    extremumPaint.setTextAlign(Paint.Align.LEFT);

                    canvas.drawText("← " + decimalFormatter.format(entry.getHigh()),
                            candleLineBuffer[0],
                            candleLineBuffer[1] - 5,
                            extremumPaint);
                }
            }*/

            if (DEBUG) {
                ToolLog.d("candleLineBuffer:" + entry.getXLabel() + " " + candleLineBuffer[0] + "  :" + candleLineBuffer[1] + "  :" + candleLineBuffer[2] + "  :" + candleLineBuffer[3]
                        + "  :" + candleLineBuffer[4] + "  :" + candleLineBuffer[5] + "  :" + candleLineBuffer[6] + "  :" + candleLineBuffer[7]);
            }

             //绘制交易动态
            if (entry.isBuy()) {

                extremumPaint.setTextAlign(Paint.Align.CENTER);
                extremumPaint.setColor(Color.RED);
                //InitAppConstant.FORECAST_BULL_BUY == entry.getBuySell() || InitAppConstant.FORECAST_BEAR_SELL == entry.getBuySell()
                if (0 < entry.getBuyBullStrategies().size()) {

                    //在下部画
                    canvas.drawText(DatabindingUtls.getStrategyTypes("多", entry.getBuyBullStrategies()),
                            candleLineBuffer[0],
                            kLineRect.bottom - 20,
                            extremumPaint);
                } else {
                    //在上部画
                    canvas.drawText(DatabindingUtls.getStrategyTypes("空", entry.getBuyBearStrategies()),
                            candleLineBuffer[0],
                            kLineRect.top + 20,
                            extremumPaint);
                }
            }

            if (entry.isSell()) {
                extremumPaint.setTextAlign(Paint.Align.CENTER);
                extremumPaint.setColor(Color.BLACK);
                if (0 < entry.getSellBullStrategies().size()) {

                    //在下部画
                    canvas.drawText(DatabindingUtls.getStrategyTypes("多", entry.getSellBullStrategies()),
                            candleLineBuffer[0],
                            kLineRect.bottom - 20,
                            extremumPaint);
                } else {

                    //在上部画
                    canvas.drawText(DatabindingUtls.getStrategyTypes("空", entry.getSellBearStrategies()),
                            candleLineBuffer[0],
                            kLineRect.top + 20,
                            extremumPaint);
                }

            }
            // 绘制 蜡烛图的矩形
            candleRectBuffer[0] = i + candleSpace;
            candleRectBuffer[2] = i + 1 - candleSpace;

            if (entry.getOpen() > entry.getClose()) {
                candleRectBuffer[1] = entry.getOpen();
                candleRectBuffer[3] = entry.getClose();
            } else {
                candleRectBuffer[1] = entry.getClose();
                candleRectBuffer[3] = entry.getOpen();
            }
            render.mapPoints(candleRectBuffer);

            if (DEBUG) {
                if (i == minIndex || i == maxIndex - 1) {
                    ToolLog.i(TAG, "onComputeOver", "##d onComputeOver: i = " + i + ", candleRectBuffer = " + candleRectBuffer[0] + " - " + candleRectBuffer[2]);
                }
            }

            if (Math.abs(candleRectBuffer[1] - candleRectBuffer[3]) < 1.f) { // 涨停、跌停、或不涨不跌的一字板
                canvas.drawRect(candleRectBuffer[0], candleRectBuffer[1], candleRectBuffer[2], candleRectBuffer[3] + 2, candlePaint);
            } else {
                canvas.drawRect(candleRectBuffer[0], candleRectBuffer[1], candleRectBuffer[2], candleRectBuffer[3], candlePaint);
            }

            // 计算高亮坐标
            if (render.isHighlight()) {
                final float[] highlightPoint = render.getHighlightPoint();

                if (candleRectBuffer[0] <= highlightPoint[0] && highlightPoint[0] <= candleRectBuffer[2]) {
                    highlightPoint[0] = candleLineBuffer[0];
//                    highlightPoint[1] = (candleRectBuffer[1] + candleRectBuffer[3]) / 2;
                    entrySet.setHighlightIndex(i);
                }
            }
        }

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
