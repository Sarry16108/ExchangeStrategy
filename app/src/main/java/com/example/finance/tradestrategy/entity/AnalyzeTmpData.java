package com.example.finance.tradestrategy.entity;

import com.example.finance.tradestrategy.indicators.calculate.BollInd;
import com.example.finance.tradestrategy.indicators.calculate.EMAInd;
import com.example.finance.tradestrategy.indicators.calculate.KDJInd;
import com.example.finance.tradestrategy.indicators.calculate.MACDInd;
import com.example.finance.tradestrategy.indicators.calculate.MAInd;
import com.example.finance.tradestrategy.indicators.calculate.OBVInd;
import com.example.finance.tradestrategy.indicators.calculate.RSIInd;
import com.example.finance.tradestrategy.indicators.calculate.ShockInd;
import com.example.finance.tradestrategy.indicators.calculate.TBRegionInd;

/**
 * Created by Administrator on 2017/7/24.
 * 各种指标计算的连续中间值，为了对获取的新数据计算时，不用做重复工作，节省时间
 */

public class AnalyzeTmpData {
    private TmpData  m1;
    private TmpData  m5;
    private TmpData  m15;
    private TmpData  m30;
    private TmpData  m60;

    public AnalyzeTmpData() {
        m1 = new TmpData();
        m5 = new TmpData();
        m15 = new TmpData();
        m30 = new TmpData();   //目前不需要不添加
        m60 = new TmpData();
    }

    public static class TmpData {
        public RSIInd.RSITmp  rsiTmp;
        public KDJInd.KDJTmp  kdjTmp;
        public MAInd.MATmp    maTmp;
        public MACDInd.MACDTmp macdTmp;
        public BollInd.BollTmp bollTmp;
        public OBVInd.OBVTmp    obvTmp;
        public TBRegionInd.TBRegionTmp  tbRegionTmp;
        public ShockInd.ShockTmp    shockTmp;
        public EMAInd.EMATmp        emaTmp;

        public TmpData() {
            rsiTmp = new RSIInd.RSITmp();
            kdjTmp = new KDJInd.KDJTmp();
            maTmp = new MAInd.MATmp();
            macdTmp = new MACDInd.MACDTmp();
            bollTmp = new BollInd.BollTmp();
            obvTmp = new OBVInd.OBVTmp();
            tbRegionTmp = new TBRegionInd.TBRegionTmp();
            shockTmp = new ShockInd.ShockTmp();
            emaTmp = new EMAInd.EMATmp();
        }

        public TmpData(TmpData tmpData) {
            this.rsiTmp = new RSIInd.RSITmp(tmpData.rsiTmp);
            this.kdjTmp = new KDJInd.KDJTmp(tmpData.kdjTmp);
            this.maTmp = new MAInd.MATmp(tmpData.maTmp);
            this.macdTmp = new MACDInd.MACDTmp(tmpData.macdTmp);
            this.bollTmp = new BollInd.BollTmp(tmpData.bollTmp);
            this.obvTmp = new OBVInd.OBVTmp(tmpData.obvTmp);
            this.tbRegionTmp = new TBRegionInd.TBRegionTmp(tmpData.tbRegionTmp);
            this.shockTmp = new ShockInd.ShockTmp(tmpData.shockTmp);
            this.emaTmp = new EMAInd.EMATmp(tmpData.emaTmp);

        }

        @Override
        public String toString() {
            return "TmpData{" +
                    "rsiTmp=" + rsiTmp.toString() +
                    ", kdjTmp=" + kdjTmp.toString() +
                    ", maTmp=" + maTmp.toString() +
                    ", macdTmp=" + macdTmp.toString() +
                    ", bollTmp=" + bollTmp.toString() +
                    ", obvTmp=" + obvTmp.toString() +
                    ", tbRegionTmp=" + tbRegionTmp.toString() +
                    ", shockTmp=" + shockTmp.toString() +
                    '}';
        }
    }

    public TmpData getM1() {
        return m1;
    }

    public void setM1(TmpData m1) {
        this.m1 = m1;
    }

    public TmpData getM5() {
        return m5;
    }

    public void setM5(TmpData m5) {
        this.m5 = m5;
    }

    public TmpData getM15() {
        return m15;
    }

    public void setM15(TmpData m15) {
        this.m15 = m15;
    }

    public TmpData getM30() {
        return m30;
    }

    public void setM30(TmpData m30) {
        this.m30 = m30;
    }

    public TmpData getM60() {
        return m60;
    }

    public void setM60(TmpData m60) {
        this.m60 = m60;
    }
}
