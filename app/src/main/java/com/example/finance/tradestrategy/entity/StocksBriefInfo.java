package com.example.finance.tradestrategy.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/7/21.
 */

public class StocksBriefInfo extends BaseResponse {

    /**
     * items : [{"symbol":"WUBA","market":"US","secType":"STK","nameCN":"58同城","latestPrice":50,"timestamp":1500580800000,"preClose":49.97,"halted":0,"hourTrading":{"tag":"盘后","latestPrice":50,"preClose":50,"latestTime":"16:45 EDT","volume":1072,"timestamp":1500583515544},"delay":0},{"symbol":"BABA","market":"US","secType":"STK","nameCN":"阿里巴巴","latestPrice":152.11,"timestamp":1500580800000,"preClose":153.15,"halted":0,"hourTrading":{"tag":"盘后","latestPrice":152.13,"preClose":152.11,"latestTime":"19:59 EDT","volume":340486,"timestamp":1500595194044},"delay":0},{"symbol":"BIDU","market":"US","secType":"STK","nameCN":"百度","latestPrice":191.32,"timestamp":1500580800000,"preClose":190.91,"halted":0,"hourTrading":{"tag":"盘后","latestPrice":191.32,"preClose":191.32,"latestTime":"18:50 EDT","volume":43437,"timestamp":1500591045009},"delay":0},{"symbol":"WB","market":"US","secType":"STK","nameCN":"微博","latestPrice":73.55,"timestamp":1500580800000,"preClose":74.2,"halted":0,"hourTrading":{"tag":"盘后","latestPrice":73.55,"preClose":73.55,"latestTime":"18:12 EDT","volume":4235,"timestamp":1500588738552},"delay":0},{"symbol":"JD","market":"US","secType":"STK","nameCN":"京东","latestPrice":42.92,"timestamp":1500580800000,"preClose":43.22,"halted":0,"hourTrading":{"tag":"盘后","latestPrice":42.9,"preClose":42.92,"latestTime":"19:58 EDT","volume":11256,"timestamp":1500595103754},"delay":0}]
     * serverTime : 1500622465608
     */

    private long serverTime;
    private List<StockBrief> items;

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public List<StockBrief> getItems() {
        return items;
    }

    public void setItems(List<StockBrief> items) {
        this.items = items;
    }

    public static class StockBrief {
        /**
         * symbol : WUBA
         * market : US
         * secType : STK
         * nameCN : 58同城
         * latestPrice : 50
         * timestamp : 1500580800000
         * preClose : 49.97
         * halted : 0
         * hourTrading : {"tag":"盘后","latestPrice":50,"preClose":50,"latestTime":"16:45 EDT","volume":1072,"timestamp":1500583515544}
         * delay : 0
         */

        private String symbol;
        private String market;
        private String secType;
        private String nameCN;
        private float latestPrice;
        private long timestamp;
        private float preClose;
        private int halted;
        private HourTradingBean hourTrading;
        private int delay;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getSecType() {
            return secType;
        }

        public void setSecType(String secType) {
            this.secType = secType;
        }

        public String getNameCN() {
            return nameCN;
        }

        public void setNameCN(String nameCN) {
            this.nameCN = nameCN;
        }

        public float getLatestPrice() {
            return latestPrice;
        }

        public void setLatestPrice(float latestPrice) {
            this.latestPrice = latestPrice;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public float getPreClose() {
            return preClose;
        }

        public void setPreClose(float preClose) {
            this.preClose = preClose;
        }

        public int getHalted() {
            return halted;
        }

        public void setHalted(int halted) {
            this.halted = halted;
        }

        public HourTradingBean getHourTrading() {
            return hourTrading;
        }

        public void setHourTrading(HourTradingBean hourTrading) {
            this.hourTrading = hourTrading;
        }

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public static class HourTradingBean {
            /**
             * tag : 盘后
             * latestPrice : 50
             * preClose : 50
             * latestTime : 16:45 EDT
             * volume : 1072
             * timestamp : 1500583515544
             */

            private String tag;
            private float latestPrice;
            private float preClose;
            private String latestTime;
            private int volume;
            private long timestamp;

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public float getLatestPrice() {
                return latestPrice;
            }

            public void setLatestPrice(float latestPrice) {
                this.latestPrice = latestPrice;
            }

            public float getPreClose() {
                return preClose;
            }

            public void setPreClose(float preClose) {
                this.preClose = preClose;
            }

            public String getLatestTime() {
                return latestTime;
            }

            public void setLatestTime(String latestTime) {
                this.latestTime = latestTime;
            }

            public int getVolume() {
                return volume;
            }

            public void setVolume(int volume) {
                this.volume = volume;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
