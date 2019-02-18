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
 * <p>MADrawing</p>
 * <p>Date: 2017/3/9</p>
 *
 * @author afon
 */

public class TBRegionDrawing implements IDrawing {

    private Paint topPaint;
    private Paint botPaint;

    private final RectF candleRect = new RectF(); // K 线图显示区域
    private AbstractRender render;

    // 计算 MA(5, 10, 20) 线条坐标用的
    private float[] topBuffer = new float[4];
    private float[] botBuffer = new float[4];

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (topPaint == null) {
            topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            topPaint.setStyle(Paint.Style.STROKE);
        }

        if (botPaint == null) {
            botPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            botPaint.setStyle(Paint.Style.STROKE);
        }

        topPaint.setStrokeWidth(sizeColor.getMaLineSize());
        botPaint.setStrokeWidth(sizeColor.getMaLineSize());

        topPaint.setColor(sizeColor.getMa5Color());
        botPaint.setColor(sizeColor.getMa10Color());

        candleRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;
        if (topBuffer.length < count) {
            topBuffer = new float[count];
            botBuffer = new float[count];
        }

        final TradeInfoSet entrySet = render.getTradeInfoSet();
        final TradeInfo.TBRegion entry = entrySet.getTradeInfoList().get(currentIndex).getTbRegion();
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex - 1) {
            final TradeInfo.TBRegion entryNext = entrySet.getTradeInfoList().get(currentIndex + 1).getTbRegion();
            topBuffer[i * 4 + 0] = currentIndex + 0.5f;
            topBuffer[i * 4 + 1] = entry.getTop();
            topBuffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            topBuffer[i * 4 + 3] = entryNext.getTop();

            botBuffer[i * 4 + 0] = topBuffer[i * 4 + 0];
            botBuffer[i * 4 + 1] = entry.getBot();
            botBuffer[i * 4 + 2] = topBuffer[i * 4 + 2];
            botBuffer[i * 4 + 3] = entryNext.getBot();

        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        canvas.save();
        canvas.clipRect(candleRect);

        render.mapPoints(topBuffer);
        render.mapPoints(botBuffer);

        final int count = (maxIndex - minIndex) * 4;

        // 使用 drawLines 方法比依次调用 drawLine 方法要快
        canvas.drawLines(topBuffer, 0, count, topPaint);
        canvas.drawLines(botBuffer, 0, count, botPaint);

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
