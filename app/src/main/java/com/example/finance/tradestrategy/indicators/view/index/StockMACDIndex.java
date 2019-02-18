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

package com.example.finance.tradestrategy.indicators.view.index;

import com.example.finance.tradestrategy.entity.TradeInfo;

/**
 * <p>MACD 指标</p>
 * <p>Date: 2017/3/16</p>
 *
 * @author afon
 */

public class StockMACDIndex extends StockIndex {

    public StockMACDIndex() {
        super(STANDARD_HEIGHT);
    }

    public StockMACDIndex(int height) {
        super(height);
    }

    @Override
    public void computeMinMax(int currentIndex, TradeInfo entry) {
        TradeInfo.MACD macd = entry.getMacd();

        if (macd.getMacd() < getMinY()) {
            setMinY(macd.getMacd());
        }
        if (macd.getDea() < getMinY()) {
            setMinY(macd.getDea());
        }
        if (macd.getDiff() < getMinY()) {
            setMinY(macd.getDiff());
        }

        if (macd.getMacd() > getMaxY()) {
            setMaxY(macd.getMacd());
        }
        if (macd.getDea() > getMaxY()) {
            setMaxY(macd.getDea());
        }
        if (macd.getDiff() > getMaxY()) {
            setMaxY(macd.getDiff());
        }
    }
}
