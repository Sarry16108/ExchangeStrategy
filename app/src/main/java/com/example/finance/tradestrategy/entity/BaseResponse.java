package com.example.finance.tradestrategy.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/6/1.
 */
public abstract class BaseResponse{

    /**
     * success : true
     * message : 获取成功
     */
    @SerializedName("code")
    private int ret = 1;        //0:成功，30001：失败，无值：url类错误。

    //请求处理结果错误
    private String msg;

    @SerializedName("d")
    private String symbol;      //外汇代码

    private String name;        //名称

    private String type;        //分时类型


    public boolean isRet() {
        return 0 == ret;
    }

    public String getError() {
        return msg;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
