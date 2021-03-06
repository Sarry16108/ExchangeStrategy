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

public class StockShockIndex extends StockIndex {

    public StockShockIndex() {
        super(STANDARD_HEIGHT);
    }

    public StockShockIndex(int height) {
        super(height);
    }

    @Override
    public void computeMinMax(int currentIndex, TradeInfo entry) {
        TradeInfo.Shock shock = entry.getShock();
        if (null == shock) {
            return;
        }
        if (shock.getSh() < getMinY()) {
            setMinY(shock.getSh());
        }
//        if (shock.getDea() < getMinY()) {
//            setMinY(shock.getDea());
//        }
//        if (shock.getDiff() < getMinY()) {
//            setMinY(shock.getDiff());
//        }

        if (shock.getSh() > getMaxY()) {
            setMaxY(shock.getSh());
        }
//        if (shock.getDea() > getMaxY()) {
//            setMaxY(shock.getDea());
//        }
//        if (shock.getDiff() > getMaxY()) {
//            setMaxY(shock.getDiff());
//        }
    }
}
