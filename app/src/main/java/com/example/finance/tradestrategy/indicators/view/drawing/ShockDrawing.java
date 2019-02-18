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
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.indicators.view.TradeInfoSet;


/**
 * <p>MACDDrawing</p>
 * <p>Date: 2017/3/14</p>
 *
 * @author afon
 */

public class ShockDrawing implements IDrawing {
    private static final String TAG = "MACDDrawing";

    private Paint axisPaint; // X 轴和 Y 轴的画笔
    private Paint sh1Paint;
    private Paint sh2Paint;
    private Paint difPaint;

    private final RectF indexRect = new RectF();
    private AbstractRender render;

    private float candleSpace = 0.1f;

    private float[] xPointBuffer = new float[4];
    private float[] sh1Buffer = new float[4];
    private float[] sh2Buffer = new float[4];

    private float[] gridBuffer = new float[2];

    private float[] xRectBuffer = new float[4];
    private float[] difBuffer = new float[4];

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (axisPaint == null) {
            axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            axisPaint.setStyle(Paint.Style.STROKE);
        }
        axisPaint.setStrokeWidth(sizeColor.getAxisSize());
        axisPaint.setColor(sizeColor.getAxisColor());

        if (sh1Paint == null) {
            sh1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            sh1Paint.setStyle(Paint.Style.STROKE);
        }

        if (sh2Paint == null) {
            sh2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            sh2Paint.setStyle(Paint.Style.STROKE);
        }

        if (difPaint == null) {
            difPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            difPaint.setStyle(Paint.Style.FILL);
            difPaint.setStrokeWidth(sizeColor.getMacdLineSize());
        }
        difPaint.setColor(sizeColor.getIncreasingColor());

        sh1Paint.setStrokeWidth(sizeColor.getMacdLineSize());
        sh2Paint.setStrokeWidth(sizeColor.getMacdLineSize());

        sh1Paint.setColor(sizeColor.getDeaLineColor());
        sh2Paint.setColor(sizeColor.getDiffLineColor());

        indexRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;
        if (xPointBuffer.length < count) {
            xPointBuffer = new float[count];
            sh1Buffer = new float[count];
            sh2Buffer = new float[count];
        }

        final TradeInfoSet entrySet = render.getTradeInfoSet();
        final TradeInfo.Shock entry = entrySet.getTradeInfoList().get(currentIndex).getShock();
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex - 1) {
            final TradeInfo.Shock entryNext = entrySet.getTradeInfoList().get(currentIndex + 1).getShock();
            xPointBuffer[i * 4 + 0] = currentIndex + 0.5f;
            xPointBuffer[i * 4 + 1] = 0;
            xPointBuffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            xPointBuffer[i * 4 + 3] = 0;

            sh1Buffer[i * 4 + 0] = 0;
            sh1Buffer[i * 4 + 1] = entry.getSh();
            sh1Buffer[i * 4 + 2] = 0;
            sh1Buffer[i * 4 + 3] = entryNext.getSh();

            sh2Buffer[i * 4 + 0] = 0;
            sh2Buffer[i * 4 + 1] = entry.getSh();
            sh2Buffer[i * 4 + 2] = 0;
            sh2Buffer[i * 4 + 3] = entryNext.getSh();
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        final TradeInfoSet entrySet = render.getTradeInfoSet();
        final SizeColor sizeColor = render.getSizeColor();

        canvas.save();
        canvas.clipRect(indexRect);

        canvas.drawRect(indexRect, axisPaint);

        gridBuffer[0] = 0;
        gridBuffer[1] = 0;
        render.mapPoints(null, gridBuffer);

        canvas.drawLine(indexRect.left, gridBuffer[1], indexRect.right, gridBuffer[1], axisPaint);

        render.mapPoints(xPointBuffer);
        render.mapPoints(null, sh1Buffer);
        render.mapPoints(null, sh2Buffer);
//
//        for (int i = minIndex; i < maxIndex; i++) {
//            TradeInfo.Shock entry = entrySet.getTradeInfoList().get(i).getShock();
//
//            xRectBuffer[0] = i + candleSpace;
//            xRectBuffer[1] = 0;
//            xRectBuffer[2] = i + 1 - candleSpace;
//            xRectBuffer[3] = 0;
//            render.mapPoints(xRectBuffer);
//
//            difBuffer[0] = 0;
//            difBuffer[2] = 0;
//
//            if (entry.getMacd() >= 0) {
//                difBuffer[1] = entry.getMacd();
//                difBuffer[3] = 0;
//            } else {
//                difBuffer[1] = 0;
//                difBuffer[3] = entry.getMacd();
//            }
//            render.mapPoints(null, difBuffer);
//
//            if (difBuffer[3] <= gridBuffer[1]) {
//                difPaint.setColor(sizeColor.getIncreasingColor());
//            } else {
//                difPaint.setColor(sizeColor.getDecreasingColor());
//            }
//
//            canvas.drawRect(xRectBuffer[0], difBuffer[1], xRectBuffer[2], difBuffer[3], difPaint);
//        }

        final int count = (maxIndex - minIndex) * 4;

        for (int i = 0 ; i < count ; i = i + 4) {
            sh1Buffer[i + 0] = xPointBuffer[i + 0];
            sh1Buffer[i + 2] = xPointBuffer[i + 2];

            sh2Buffer[i + 0] = xPointBuffer[i + 0];
            sh2Buffer[i + 2] = xPointBuffer[i + 2];
        }

        canvas.drawLines(sh1Buffer, 0, count, sh1Paint);
        canvas.drawLines(sh2Buffer, 0, count, sh2Paint);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
