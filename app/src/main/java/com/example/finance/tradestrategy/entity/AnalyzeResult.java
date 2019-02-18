package com.example.finance.tradestrategy.entity;

/**
 * Created by yanghj on 2017/7/24.
 * 分析特征并统计
 */

public class AnalyzeResult {

    private IndResult m1;
    private IndResult m5;
    private IndResult m15;
    private IndResult m30;
    private IndResult m60;

    public AnalyzeResult() {
        m1 = new IndResult();
        m5 = new IndResult();
        m15 = new IndResult();
//        m30 = new IndResult();    //todo:
//        m60 = new IndResult();
    }

    public static class IndResult {
        //kdj相关
        public boolean isBull = true;   //false表示超买，看空，卖出，true代表超卖，看多，买入
        public boolean hasUltra = false;   //当次是否有超卖/超买
        public int      ultraTimes = 0;    //当次前连续超卖/超买次数
        public boolean hasIntersect = false;  //当次在超买、超卖区是否相交
        public int      intersectTimes = 0;    //当次前在超买、超卖区连续交点次数


    }

    public IndResult getM1() {
        return m1;
    }

    public IndResult getM5() {
        return m5;
    }

    public IndResult getM15() {
        return m15;
    }

    public IndResult getM30() {
        return m30;
    }

    public IndResult getM60() {
        return m60;
    }

    public void setM1(IndResult m1) {
        this.m1 = m1;
    }

    public void setM5(IndResult m5) {
        this.m5 = m5;
    }

    public void setM15(IndResult m15) {
        this.m15 = m15;
    }

    public void setM30(IndResult m30) {
        this.m30 = m30;
    }

    public void setM60(IndResult m60) {
        this.m60 = m60;
    }
}
