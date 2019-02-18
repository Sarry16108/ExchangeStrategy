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

package com.example.finance.tradestrategy.indicators.view.maker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.indicators.view.align.YMarkerAlign;
import com.example.finance.tradestrategy.indicators.view.drawing.AbstractRender;
import com.example.finance.tradestrategy.indicators.view.drawing.SizeColor;

import java.text.DecimalFormat;

/**
 * <p>YAxisTextMarkerView</p>
 * <p>Date: 2017/8/11</p>
 *      显示各项指标值
 * @author afon
 */

public class DetailYAxisTextMarkerView implements IMarkerView {
    private static final String TAG = "YAxisTextMarkerView";

    private TextPaint markerTextPaint;
    private Paint markerBorderPaint;

    private final RectF contentRect = new RectF();
    private AbstractRender render;

    private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
    private final DecimalFormat decimalFormatter = new DecimalFormat("0.00");
    private final float[] pointCache = new float[2];
    private final int height;
    private final RectF markerInsets = new RectF(0, 0, 0, 0);
    private float inset = 0;
    private YMarkerAlign yMarkerAlign;

    public DetailYAxisTextMarkerView(int height) {
        this.height = height;
    }

    @Override
    public void onInitMarkerView(RectF contentRect, AbstractRender render) {
        this.contentRect.set(contentRect);
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (markerTextPaint == null) {
            markerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            markerTextPaint.setTextAlign(Paint.Align.LEFT);
        }

        markerTextPaint.setColor(Color.WHITE);//sizeColor.getMarkerTextColor());
        markerTextPaint.setTextSize(sizeColor.getMarkerTextSize());
        markerTextPaint.getFontMetrics(fontMetrics);

        if (markerBorderPaint == null) {
            markerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            markerBorderPaint.setStyle(Paint.Style.FILL);
        }

        markerBorderPaint.setStrokeWidth(sizeColor.getMarkerBorderSize());
        markerBorderPaint.setColor(0x90000000);//sizeColor.getMarkerBorderColor());
        inset = markerBorderPaint.getStrokeWidth() / 2;

        yMarkerAlign = sizeColor.getYMarkerAlign();
    }

    @Override
    public void onDrawMarkerView(Canvas canvas, float highlightPointX, float highlightPointY) {

        //todo:改为显示指标值
        TradeInfo highlightTradeInfo = render.getTradeInfoSet().getHighlightTradeInfo();

        if (null != highlightTradeInfo) {
            StringBuilder selectedValue = new StringBuilder();
            TradeInfo.KDJ kdj = highlightTradeInfo.getKdj();
            TradeInfo.Boll boll = highlightTradeInfo.getBoll();
            TradeInfo.RSI rsi = highlightTradeInfo.getRsi();
            TradeInfo.MACD macd = highlightTradeInfo.getMacd();
            selectedValue.append("Date   ").append(highlightTradeInfo.getXLabel()).append(" H:").append(highlightTradeInfo.getHigh())
                    .append(" L:").append(highlightTradeInfo.getLow()).append(" C:").append(highlightTradeInfo.getClose());
            selectedValue.append("\nKDJ   k:").append(kdj.getK()).append("  D:").append(kdj.getD()).append("  J:").append(kdj.getJ());
            selectedValue.append("\nBOLL  up:").append(boll.getUpper()).append("  mb:").append(boll.getMb()).append("  dn:").append(boll.getDner())
                    .append("\n           width:").append(boll.getWidth()).append("  B:").append(boll.getB());
            selectedValue.append("\nRSI   r1:").append(rsi.getRsi1()).append("  r2:").append(rsi.getRsi2()).append("  r3:").append(rsi.getRsi3());
            selectedValue.append("\nMACD  dea:").append(macd.getDea()).append("  diff:").append(macd.getDiff()).append("  macd:").append(macd.getMacd());

            canvas.save();

            //计算换行文字
            int width = (int) (markerTextPaint.measureText(selectedValue.toString(), 0, selectedValue.length()) + 40);
            StaticLayout staticLayout = new StaticLayout(selectedValue.toString(), markerTextPaint, width, Layout.Alignment.ALIGN_NORMAL,
                    1.5f, 0.0f, false);

            //画背景色
            canvas.drawRect(0, 0, width, staticLayout.getHeight() + 30, markerBorderPaint);
            //写数据
            canvas.translate(30, 20);
            staticLayout.draw(canvas);

            canvas.restore();

        }
    }
}
