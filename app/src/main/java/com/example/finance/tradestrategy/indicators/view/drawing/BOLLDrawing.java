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
 * <p>BOLLDrawing</p>
 * <p>Date: 2017/3/15</p>
 *
 * @author afon
 */

public class BOLLDrawing implements IDrawing{

    private Paint axisPaint; // X 轴和 Y 轴的画笔
    private Paint r1Paint;
    private Paint r2Paint;
    private Paint r3Paint;

    private final RectF indexRect = new RectF();
    private AbstractRender render;

    private float[] xPointBuffer = new float[4];
    private float[] r1Buffer = new float[4];
    private float[] r2Buffer = new float[4];
    private float[] r3Buffer = new float[4];

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

        if (r1Paint == null) {
            r1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            r1Paint.setStyle(Paint.Style.STROKE);
        }

        if (r2Paint == null) {
            r2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            r2Paint.setStyle(Paint.Style.STROKE);
        }

        if (r3Paint == null) {
            r3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            r3Paint.setStyle(Paint.Style.STROKE);
        }

        r1Paint.setStrokeWidth(sizeColor.getBollLineSize());
        r2Paint.setStrokeWidth(sizeColor.getBollLineSize());
        r3Paint.setStrokeWidth(sizeColor.getBollLineSize());

        r1Paint.setColor(sizeColor.getBollMidLineColor());
        r2Paint.setColor(sizeColor.getBollUpperLineColor());
        r3Paint.setColor(sizeColor.getBollLowerLineColor());

        indexRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;
        if (xPointBuffer.length < count) {
            xPointBuffer = new float[count];
            r1Buffer = new float[count];
            r2Buffer = new float[count];
            r3Buffer = new float[count];
        }

        final TradeInfoSet entrySet = render.getTradeInfoSet();
        final TradeInfo.Boll entry = entrySet.getTradeInfoList().get(currentIndex).getBoll();
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex - 1) {
            final TradeInfo.Boll entryNext = entrySet.getTradeInfoList().get(currentIndex + 1).getBoll();
            xPointBuffer[i * 4 + 0] = currentIndex + 0.5f;
            xPointBuffer[i * 4 + 1] = 0;
            xPointBuffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            xPointBuffer[i * 4 + 3] = 0;

            r1Buffer[i * 4 + 0] = 0;
            r1Buffer[i * 4 + 1] = entry.getMb();
            r1Buffer[i * 4 + 2] = 0;
            r1Buffer[i * 4 + 3] = entryNext.getMb();

            r2Buffer[i * 4 + 0] = 0;
            r2Buffer[i * 4 + 1] = entry.getUpper();
            r2Buffer[i * 4 + 2] = 0;
            r2Buffer[i * 4 + 3] = entryNext.getUpper();

            r3Buffer[i * 4 + 0] = 0;
            r3Buffer[i * 4 + 1] = entry.getDner();
            r3Buffer[i * 4 + 2] = 0;
            r3Buffer[i * 4 + 3] = entryNext.getDner();
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        if (Float.isNaN(minY) || Float.isNaN(maxY)) {
            return;
        }

        canvas.save();
        canvas.clipRect(indexRect);

        canvas.drawRect(indexRect, axisPaint);

        gridBuffer[0] = 0;
        gridBuffer[1] = (maxY + minY) / 2;
        render.mapPoints(null, gridBuffer);

        canvas.drawLine(indexRect.left, gridBuffer[1], indexRect.right, gridBuffer[1], axisPaint);

        render.mapPoints(xPointBuffer);
        render.mapPoints(null, r1Buffer);
        render.mapPoints(null, r2Buffer);
        render.mapPoints(null, r3Buffer);

        final int count = (maxIndex - minIndex) * 4;

        for (int i = 0 ; i < count ; i = i + 4) {
            r1Buffer[i + 0] = xPointBuffer[i + 0];
            r1Buffer[i + 2] = xPointBuffer[i + 2];

            r2Buffer[i + 0] = xPointBuffer[i + 0];
            r2Buffer[i + 2] = xPointBuffer[i + 2];

            r3Buffer[i + 0] = xPointBuffer[i + 0];
            r3Buffer[i + 2] = xPointBuffer[i + 2];
        }

        final int offset = minIndex == 0 ? 4 : 0;

        canvas.drawLines(r1Buffer, 0, count, r1Paint);
        canvas.drawLines(r2Buffer, offset, count - offset, r2Paint);
        canvas.drawLines(r3Buffer, offset, count - offset, r3Paint);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
