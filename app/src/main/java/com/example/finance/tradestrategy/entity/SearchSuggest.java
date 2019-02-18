package com.example.finance.tradestrategy.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

/**
 * serverTime : 1500007588743
 * items : [{"symbol":"BABA","nameCN":"阿里巴巴","market":"US","type":0}]
 */
public class SearchSuggest extends BaseResponse {

    private long serverTime;
    private List<SimilarCode> items;

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public List<SimilarCode> getItems() {
        return items;
    }

    public void setItems(List<SimilarCode> items) {
        this.items = items;
    }

    public static class SimilarCode {
        /**
         * symbol : BABA
         * nameCN : 阿里巴巴
         * market : US
         * type : 0
         */

        private String symbol;
        private String nameCN;
        private String market;
        private int type;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getNameCN() {
            return nameCN;
        }

        public void setNameCN(String nameCN) {
            this.nameCN = nameCN;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
