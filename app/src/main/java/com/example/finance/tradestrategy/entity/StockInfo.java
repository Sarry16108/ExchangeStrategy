package com.example.finance.tradestrategy.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 * 每次请求返回的股票数据
 */

public class StockInfo extends BaseResponse{
    /**
     {"code":0,
     "msg":"",
     "timestamp":"1504603767",
     "more":500,
     "data":[{"o":"1.1872","h":"1.1874","l":"1.1869","c":"1.1872","t":1504284900},{"o":"1.1872","h":"1.1873","l":"1.1855","c":"1.1858","t":1504285200}]}
     */

    @SerializedName("timestamp")
    private long serverTime;
    private String period;          //指定是1分还是5
    @SerializedName("data")
    private List<TradeInfo> items;  //历史数据或者指定分钟内的数据
    private List<TradeInfo> items5;
    private List<TradeInfo> items15;
    private List<TradeInfo> items30;
    private List<TradeInfo> items60;

    //时间段内的临时数据
    private TradeInfo tmpTradeInfoM1;
    private TradeInfo tmpTradeInfoM5;
    private TradeInfo tmpTradeInfoM15;
    private TradeInfo tmpTradeInfoM30;
    private TradeInfo tmpTradeInfoM60;

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    //1min
    public List<TradeInfo> getItems() {
        return items;
    }

    public void setItems(List<TradeInfo> items) {
        this.items = items;
    }

    public void addItemsHead(List<TradeInfo> items) {
        this.items.addAll(0, items);
    }

    //5min
    public List<TradeInfo> getItems5() {
        return items5;
    }

    public void setItems5(List<TradeInfo> items5) {
        this.items5 = items5;
    }

    public void addItems5Head(List<TradeInfo> items5) {
        this.items5.addAll(0, items5);
    }

    //15min
    public List<TradeInfo> getItems15() {
        return items15;
    }

    public void setItems15(List<TradeInfo> items15) {
        this.items15 = items15;
    }
    public void addItems15Head(List<TradeInfo> items15) {
        this.items15.addAll(0, items15);
    }

    //30min
    public List<TradeInfo> getItems30() {
        return items30;
    }

    public void setItems30(List<TradeInfo> items30) {
        this.items30 = items30;
    }
    public void addItems30Head(List<TradeInfo> items30) {
        this.items30.addAll(0, items30);
    }


    //60min
    public List<TradeInfo> getItems60() {
        return items60;
    }

    public void setItems60(List<TradeInfo> items60) {
        this.items60 = items60;
    }
    public void addItems60Head(List<TradeInfo> items60) {
        this.items60.addAll(0, items60);
    }

    public TradeInfo getTmpTradeInfoM1() {
        return tmpTradeInfoM1;
    }

    public void setTmpTradeInfoM1(TradeInfo tmpTradeInfoM1) {
        this.tmpTradeInfoM1 = tmpTradeInfoM1;
    }

    public TradeInfo getTmpTradeInfoM5() {
        return tmpTradeInfoM5;
    }

    public void setTmpTradeInfoM5(TradeInfo tmpTradeInfoM5) {
        this.tmpTradeInfoM5 = tmpTradeInfoM5;
    }

    public TradeInfo getTmpTradeInfoM15() {
        return tmpTradeInfoM15;
    }

    public void setTmpTradeInfoM15(TradeInfo tmpTradeInfoM15) {
        this.tmpTradeInfoM15 = tmpTradeInfoM15;
    }

    public TradeInfo getTmpTradeInfoM30() {
        return tmpTradeInfoM30;
    }

    public void setTmpTradeInfoM30(TradeInfo tmpTradeInfoM30) {
        this.tmpTradeInfoM30 = tmpTradeInfoM30;
    }

    public TradeInfo getTmpTradeInfoM60() {
        return tmpTradeInfoM60;
    }

    public void setTmpTradeInfoM60(TradeInfo tmpTradeInfoM60) {
        this.tmpTradeInfoM60 = tmpTradeInfoM60;
    }
}
