package com.example.finance.tradestrategy.base;

import android.app.Application;
import android.content.Context;
import android.support.v4.util.ArraySet;

import com.example.finance.tradestrategy.globaldata.InitData;
import com.example.finance.tradestrategy.utils.ToolFile;

import java.util.Set;

/**
 * Created by yanghj on 2017/6/4.
 */

public class BaseApplication extends Application {

    public static Context  mContext;


    public static boolean       mIsLogined = false;

    //监控的stocks
    public static Set<String> mStockCodes = new ArraySet<>(5);
    public static String      mForecastRecordFileName;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        initData();
    }



    private void initData() {
        ToolFile.initAppDir();

//        mStockCodes.add(InitData.Ex_XAU);
//        mStockCodes.add(InitData.Ex_XAG);
//        mStockCodes.add(InitData.Ex_CONC);
//        mStockCodes.add(InitData.Ex_AUDUSD);
//        mStockCodes.add(InitData.Ex_USD);
        mStockCodes.add(InitData.Ex_EURUSD);
        mStockCodes.add(InitData.Ex_GBPUSD);
//        ToolLog.i("读取数据完成-----排行榜：" + BaseApplication.mRankLongPeriod.size() + "  images:" + BaseApplication.mSavedUserImgs.size());
    }
}
