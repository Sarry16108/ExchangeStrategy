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

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.indicators.view.TradeInfoSet;

/**
 * <p>MADrawing</p>
 * <p>Date: 2017/3/9</p>
 *
 * @author afon
 */

public class OBVDrawing implements IDrawing {

    private Paint axisPaint; // X 轴和 Y 轴的画笔
    private Paint vPaint;
    private Paint netVPaint;

    private final RectF indexRect = new RectF(); // 指标图显示区域
    private AbstractRender render;

    private float[] xPointBuffer = new float[4];
    private float[] vBuffer = new float[4];
    private float[] netVBuffer = new float[4];


    private float[] jBuffer = new float[4];
    private Paint jPaint;

    private float[] gridBuffer = new float[2];

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

        if (vPaint == null) {
            vPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            vPaint.setStyle(Paint.Style.STROKE);
        }

        if (netVPaint == null) {
            netVPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            netVPaint.setStyle(Paint.Style.STROKE);
        }

        if (jPaint == null) {
            jPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            jPaint.setStyle(Paint.Style.STROKE);
        }

        vPaint.setStrokeWidth(sizeColor.getMaLineSize());
        netVPaint.setStrokeWidth(sizeColor.getMaLineSize());

        vPaint.setColor(sizeColor.getDiffLineColor());
        netVPaint.setColor(sizeColor.getDeaLineColor());

        jPaint.setStrokeWidth(sizeColor.getKdjLineSize());
        jPaint.setColor(Color.BLACK);

        indexRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;
        if (vBuffer.length < count) {
            xPointBuffer = new float[count];
            vBuffer = new float[count];
            netVBuffer = new float[count];
            jBuffer = new float[count];
        }

        final TradeInfoSet entrySet = render.getTradeInfoSet();
        final TradeInfo.OBV entry = entrySet.getTradeInfoList().get(currentIndex).getObv();
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex - 1) {
            final TradeInfo.OBV entryNext = entrySet.getTradeInfoList().get(currentIndex + 1).getObv();
            xPointBuffer[i * 4 + 0] = currentIndex + 0.5f;
            xPointBuffer[i * 4 + 1] = 0;
            xPointBuffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            xPointBuffer[i * 4 + 3] = 0;

            vBuffer[i * 4 + 0] = 0;
            vBuffer[i * 4 + 1] = entry.getV();
            vBuffer[i * 4 + 2] = 0;
            vBuffer[i * 4 + 3] = entryNext.getV();

            netVBuffer[i * 4 + 0] = 0;
            netVBuffer[i * 4 + 1] = entry.getNetV();
            netVBuffer[i * 4 + 2] = 0;
            netVBuffer[i * 4 + 3] = entryNext.getNetV();

            jBuffer[i * 4 + 0] = 0;
            jBuffer[i * 4 + 1] = 0;
            jBuffer[i * 4 + 2] = 0;
            jBuffer[i * 4 + 3] = 0;
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        canvas.save();
        canvas.clipRect(indexRect);

        canvas.drawRect(indexRect, axisPaint);

        gridBuffer[0] = 0;
        gridBuffer[1] = (maxY + minY) / 2;
        render.mapPoints(null, gridBuffer);

        canvas.drawLine(indexRect.left, gridBuffer[1], indexRect.right, gridBuffer[1], axisPaint);

        render.mapPoints(xPointBuffer);
        render.mapPoints(null, vBuffer);
        render.mapPoints(null, netVBuffer);
        render.mapPoints(null, jBuffer);

        final int count = (maxIndex - minIndex) * 4;

        for (int i = 0 ; i < count ; i = i + 4) {
            vBuffer[i + 0] = xPointBuffer[i + 0];
            vBuffer[i + 2] = xPointBuffer[i + 2];

            netVBuffer[i + 0] = xPointBuffer[i + 0];
            netVBuffer[i + 2] = xPointBuffer[i + 2];

            jBuffer[i + 0] = xPointBuffer[i + 0];
            jBuffer[i + 2] = xPointBuffer[i + 2];
        }

        canvas.drawLines(vBuffer, 0, count, vPaint);
        canvas.drawLines(netVBuffer, 0, count, netVPaint);
        canvas.drawLines(jBuffer, 0, count, jPaint);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
