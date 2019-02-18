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
 * <p>RSI 指标</p>
 * <p>Date: 2017/3/16</p>
 *
 * @author afon
 */

public class StockRSIIndex extends StockIndex {

    public StockRSIIndex() {
        super(STANDARD_HEIGHT);
    }

    public StockRSIIndex(int height) {
        super(height);
    }

    @Override
    public void computeMinMax(int currentIndex, TradeInfo entry) {
        TradeInfo.RSI rsi = entry.getRsi();
        if (rsi.getRsi1() < getMinY()) {
            setMinY(rsi.getRsi1());
        }
        if (rsi.getRsi2() < getMinY()) {
            setMinY(rsi.getRsi2());
        }
        if (rsi.getRsi3() < getMinY()) {
            setMinY(rsi.getRsi3());
        }

        if (rsi.getRsi1() > getMaxY()) {
            setMaxY(rsi.getRsi1());
        }
        if (rsi.getRsi2() > getMaxY()) {
            setMaxY(rsi.getRsi2());
        }
        if (rsi.getRsi3() > getMaxY()) {
            setMaxY(rsi.getRsi3());
        }
    }
}
