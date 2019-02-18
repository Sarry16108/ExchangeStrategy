package com.example.finance.tradestrategy.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.android.annotations.Nullable;
import com.example.finance.tradestrategy.R;
import com.example.finance.tradestrategy.adapters.BaseViewpagerAdapter;
import com.example.finance.tradestrategy.base.BaseApplication;
import com.example.finance.tradestrategy.base.PresenterManager;
import com.example.finance.tradestrategy.baseui.BaseActivity;
import com.example.finance.tradestrategy.baseui.BaseFragment;
import com.example.finance.tradestrategy.databinding.ActMainBinding;
import com.example.finance.tradestrategy.databindings.DatabindingUtls;
import com.example.finance.tradestrategy.entity.BaseResponse;
import com.example.finance.tradestrategy.entity.NetCallback;
import com.example.finance.tradestrategy.entity.RequestPeriod;
import com.example.finance.tradestrategy.entity.StockInfo;
import com.example.finance.tradestrategy.entity.StockStrategy;
import com.example.finance.tradestrategy.entity.TradeInfo;
import com.example.finance.tradestrategy.entity.TradeInfoSimple;
import com.example.finance.tradestrategy.fragments.ForecastRecordFrag;
import com.example.finance.tradestrategy.fragments.IndicatorDetailFrag;
import com.example.finance.tradestrategy.fx678.PushCallback;
import com.example.finance.tradestrategy.fx678.ToolRequestHT;
import com.example.finance.tradestrategy.fx678.ToolThreadManage;
import com.example.finance.tradestrategy.globaldata.InitAppConstant;
import com.example.finance.tradestrategy.globaldata.InitData;
import com.example.finance.tradestrategy.globaldata.InitNetInfo;
import com.example.finance.tradestrategy.globaldata.MessageId;
import com.example.finance.tradestrategy.indicators.calculate.AnalyzeInd;
import com.example.finance.tradestrategy.utils.ToolFile;
import com.example.finance.tradestrategy.utils.ToolGson;
import com.example.finance.tradestrategy.utils.ToolLog;
import com.example.finance.tradestrategy.utils.ToolNotification;
import com.example.finance.tradestrategy.utils.ToolRequest;
import com.example.finance.tradestrategy.utils.ToolSharePre;
import com.example.finance.tradestrategy.utils.ToolString;
import com.example.finance.tradestrategy.utils.ToolTime;
import com.example.finance.tradestrategy.utils.ToolToast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class MainActivity extends BaseActivity implements NetCallback{
    private ActMainBinding mActBinding;

    //菜单项
    private Menu    mMainMenu;

    private ForecastRecordFrag mForecastRecordFra;
    private IndicatorDetailFrag mIndicatorDetailFra;

    //adapter对应的数据，用于显示
    private LinkedHashMap<String, StockStrategy> mStockStrategy = new LinkedHashMap<>(10);

    //所有项的列表数据，用于分析计算
    private Map<String, StockInfo>    mStockInfos;  //stockcode， StockInfo
    private Map<String, RequestPeriod>   mRequestPeriod;   //各股票的请求时间段

    private Timer       mTimer;
    private long        mPeriod = 6000;    //timer循环间隔周期6s
    private long        mPeriod5  = 0;      //5分更新时刻
    private long        mPeriod15  = 0;     //15分更新时刻
    private final int   UPDATE_PERIOD = 30000;     //5分、15分更新周期
    private boolean     mIsStart = false;   //是否开始
    private long        mLastBackClick = 0; //上次点击退出时间

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActBinding = DataBindingUtil.setContentView(this, R.layout.act_main);


        initView();
//        ToolRequestHT.getInstance().getWhHistoryData("EURUSD", InitData.PERIOD_LINE_5_MINUTE, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //更新市场时间
        ToolTime.initTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                PresenterManager.toSetting(this, 1);
                break;
            case R.id.startOrStop:
                mHandler.sendEmptyMessage(MessageId.UPDATE_START_STOP);
                break;
            case R.id.clearData:
                mHandler.sendEmptyMessage(MessageId.CLEAR_APP_DATA);
                break;
            case R.id.saveData:
                mHandler.sendEmptyMessage(MessageId.SAVE_EXCHANGE_DATA);
                break;
        }

        return true;
    }

    /**
     * 测试数据
     */
    private boolean test() {
        if (true) {
            ToolRequestHT.getInstance().getWhHistoryData("GBPUSD", "min10", 0, this);
            return true;
        }

        //
        String symbols = ToolString.arySperator(BaseApplication.mStockCodes, "|");

        ToolThreadManage.getInstance().initDate("hq", "EURUSD", new PushCallback() {
            @Override
            public void onUDP_push(TradeInfoSimple tradeInfo) {

            }
        });

        if (true) {
            return true;
        }


        for (int i = 0; i < 7; ++i) {
            final String symbol = "AA" + i;
            final StockStrategy stockStrategy = new StockStrategy("阿里", symbol, 0);
            stockStrategy.resetBuy();

            mStockStrategy.put(symbol, stockStrategy);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ToolNotification.getInstance().showNotification(stockStrategy);
                    packMsgAndSend(MessageId.FORECAST_RECORD_ADD, stockStrategy);
                    packMsgAndSend(MessageId.FORECAST_DATA_ADD_CODE, symbol);
                }
            }).start();

        }

        return true;
    }

    private void initView() {
        //历史数据保存位置，便于以后分析
        BaseApplication.mForecastRecordFileName = ToolTime.getMDHMS(System.currentTimeMillis()) + ".txt";

        mStockInfos = new ArrayMap<>(BaseApplication.mStockCodes.size());
        mRequestPeriod = new ArrayMap<>(BaseApplication.mStockCodes.size());

        for (String symbol : BaseApplication.mStockCodes) {
            mRequestPeriod.put(symbol, new RequestPeriod());
        }


        //页面
        mForecastRecordFra = new ForecastRecordFrag();
        mIndicatorDetailFra = new IndicatorDetailFrag();

        List<BaseFragment> fragments = new ArrayList<>(3);
        fragments.add(0, mIndicatorDetailFra);
        fragments.add(1, mForecastRecordFra);

        mActBinding.dataContainer.setOffscreenPageLimit(3);
        mActBinding.dataContainer.setAdapter(new BaseViewpagerAdapter<BaseFragment>(getSupportFragmentManager(), fragments));
        mActBinding.dataContainer.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }
        });

        //添加外汇列表
        for (String symbol : BaseApplication.mStockCodes) {
            if (!mStockStrategy.containsKey(symbol)) {
                mStockStrategy.put(symbol, new StockStrategy(DatabindingUtls.getSymbolName(symbol), symbol, mStockStrategy.size()));
                //界面添加新项
                packDelayMsgAndSend(MessageId.FORECAST_DATA_ADD_CODE, symbol, 2000);
            }
        }
    }

    /**
     * 请求服务器端历史数据
     */
    private void requestDatas() {
        for (String symbol : BaseApplication.mStockCodes) {
            ToolRequestHT.getInstance().getWhHistoryData(symbol, InitData.PERIOD_LINE_1_MINUTE, 0, this);
        }

        for (String symbol : BaseApplication.mStockCodes) {
            ToolRequestHT.getInstance().getWhHistoryData(symbol, InitData.PERIOD_LINE_30_MINUTE, 0, this);
        }
    }

    private synchronized void organizeNewToList(TradeInfoSimple tradeInfoSimple) {
//        // TODO: 2017/10/5 更新数据保存，以便对照数据
//        ToolFile.appendFileData("udp_" + tradeInfoSimple.getSymbol() + "_" + BaseApplication.mForecastRecordFileName, tradeInfoSimple.toString());
        
        //没有记录的symbol不计算
        if (!mStockInfos.containsKey(tradeInfoSimple.getSymbol())) {
            return;
        }

        StockInfo stockInfo = mStockInfos.get(tradeInfoSimple.getSymbol());
        TradeInfo tradeInfo = null;
        StockStrategy stockStrategy = mStockStrategy.get(tradeInfoSimple.getSymbol());


        //1分钟数据
        if (null != stockInfo.getItems()) {
            tradeInfo = stockInfo.getTmpTradeInfoM1();

            //1分钟内
            if (tradeInfo.getTime() > tradeInfoSimple.getTime()) {
                if (tradeInfo.getHigh() < tradeInfoSimple.getClose()) {
                    tradeInfo.setHigh(tradeInfoSimple.getClose());
                } else if (tradeInfo.getLow() > tradeInfoSimple.getClose()) {
                    tradeInfo.setLow(tradeInfoSimple.getClose());
                }

                tradeInfo.setClose(tradeInfoSimple.getClose());
                //添加一条新项
            } else {
                if (tradeInfo != stockInfo.getItems().get(stockInfo.getItems().size() - 1)) {
                    AnalyzeInd.INSTANCE.analyzeStockInfoResult(this, InitAppConstant.MINUTE_1, stockInfo,
                            tradeInfo, 0l, stockStrategy);
                }

                tradeInfo = new TradeInfo(tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(), tradeInfoSimple.getClose(), 0, tradeInfo.getTime() + 60);
                tradeInfo.setSymbol(tradeInfoSimple.getSymbol());
                stockInfo.setTmpTradeInfoM1(tradeInfo);
            }
        }

        //5分数据
        if (null != stockInfo.getItems5()) {
            tradeInfo = stockInfo.getTmpTradeInfoM5();

            //5分钟内
            if (tradeInfo.getTime() > tradeInfoSimple.getTime()) {
                if (tradeInfo.getHigh() < tradeInfoSimple.getClose()) {
                    tradeInfo.setHigh(tradeInfoSimple.getClose());
                } else if (tradeInfo.getLow() > tradeInfoSimple.getClose()) {
                    tradeInfo.setLow(tradeInfoSimple.getClose());
                }

                //更新收盘价
                tradeInfo.setClose(tradeInfoSimple.getClose());

                //添加一条新项
            } else {
                if (tradeInfo != stockInfo.getItems5().get(stockInfo.getItems5().size() - 1)) {
                    AnalyzeInd.INSTANCE.analyzeStockInfoResult(this, InitAppConstant.MINUTE_5, stockInfo,
                            tradeInfo, 0l, stockStrategy);
                }

                tradeInfo = new TradeInfo(tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(), tradeInfoSimple.getClose(), 0, tradeInfo.getTime() + 300);
                tradeInfo.setSymbol(tradeInfoSimple.getSymbol());
                stockInfo.setTmpTradeInfoM5(tradeInfo);
            }
        }

        //15分数据
        if (null != stockInfo.getItems15()) {
            tradeInfo = stockInfo.getTmpTradeInfoM15();

            //1分钟内
            if (tradeInfo.getTime() > tradeInfoSimple.getTime()) {
                if (tradeInfo.getHigh() < tradeInfoSimple.getClose()) {
                    tradeInfo.setHigh(tradeInfoSimple.getClose());
                } else if (tradeInfo.getLow() > tradeInfoSimple.getClose()) {
                    tradeInfo.setLow(tradeInfoSimple.getClose());
                }

                //更新收盘价
                tradeInfo.setClose(tradeInfoSimple.getClose());
                //添加一条新项
            } else {
                if (tradeInfo != stockInfo.getItems15().get(stockInfo.getItems15().size() - 1)) {
                    AnalyzeInd.INSTANCE.analyzeStockInfoResult(this, InitAppConstant.MINUTE_15, stockInfo,
                            tradeInfo, 0l, stockStrategy);
                }

                tradeInfo = new TradeInfo(tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(), tradeInfoSimple.getClose(), 0, tradeInfo.getTime() + 900);
                tradeInfo.setSymbol(tradeInfoSimple.getSymbol());
                stockInfo.setTmpTradeInfoM15(tradeInfo);
            }
        }

        //30分数据
        if (null != stockInfo.getItems30()) {
            tradeInfo = stockInfo.getTmpTradeInfoM30();

            //30分钟内
            if (tradeInfo.getTime() > tradeInfoSimple.getTime()) {
                if (tradeInfo.getHigh() < tradeInfoSimple.getClose()) {
                    tradeInfo.setHigh(tradeInfoSimple.getClose());
                } else if (tradeInfo.getLow() > tradeInfoSimple.getClose()) {
                    tradeInfo.setLow(tradeInfoSimple.getClose());
                }

                //更新收盘价
                tradeInfo.setClose(tradeInfoSimple.getClose());

                //添加一条新项
            } else {
                if (tradeInfo != stockInfo.getItems30().get(stockInfo.getItems30().size() - 1)) {
                    AnalyzeInd.INSTANCE.analyzeStockInfoResult(this, InitAppConstant.MINUTE_30, stockInfo,
                            tradeInfo, 0l, stockStrategy);
                }

                tradeInfo = new TradeInfo(tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(), tradeInfoSimple.getClose(), 0, tradeInfo.getTime() + 1800);
                tradeInfo.setSymbol(tradeInfoSimple.getSymbol());
                stockInfo.setTmpTradeInfoM30(tradeInfo);
            }
        }

        //60分数据
        if (null != stockInfo.getItems60()) {
            tradeInfo = stockInfo.getTmpTradeInfoM60();

            //60分钟内
            if (tradeInfo.getTime() > tradeInfoSimple.getTime()) {
                if (tradeInfo.getHigh() < tradeInfoSimple.getClose()) {
                    tradeInfo.setHigh(tradeInfoSimple.getClose());
                } else if (tradeInfo.getLow() > tradeInfoSimple.getClose()) {
                    tradeInfo.setLow(tradeInfoSimple.getClose());
                }

                //更新收盘价
                tradeInfo.setClose(tradeInfoSimple.getClose());

                //添加一条新项
            } else {
                if (tradeInfo != stockInfo.getItems60().get(stockInfo.getItems60().size() - 1)) {
                    AnalyzeInd.INSTANCE.analyzeStockInfoResult(this, InitAppConstant.MINUTE_60, stockInfo,
                            tradeInfo, 0l, stockStrategy);
                }

                tradeInfo = new TradeInfo(tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(),
                        tradeInfoSimple.getClose(), tradeInfoSimple.getClose(), 0, tradeInfo.getTime() + 3600);
                tradeInfo.setSymbol(tradeInfoSimple.getSymbol());
                stockInfo.setTmpTradeInfoM60(tradeInfo);
            }
        }


        //刷新列表价格
        stockStrategy.setClose(tradeInfoSimple.getClose());
        packMsgAndSend(MessageId.FORECAST_DATA_LIST, tradeInfoSimple.getSymbol());
    }


    @Override
    public void onSuccess(String method, BaseResponse data) {
        if (null == data) {
            ToolLog.e(TAG, "onSuccess", method,  "data is null");
            return;
        }
        StockInfo stockInfo = null;
        TradeInfo tradeInfo = null;

        switch (method) {
            case InitData.PERIOD_LINE_1_MINUTE:
                stockInfo = (StockInfo)data;

                //当数据项数大于3就代表是历史数据
                if (stockInfo.getItems().size() > 3) {
                    if (mStockInfos.containsKey(stockInfo.getSymbol())) {
                        mStockInfos.get(stockInfo.getSymbol()).setItems(stockInfo.getItems());
                        stockInfo = mStockInfos.get(stockInfo.getSymbol());
                    } else {
                        mStockInfos.put(stockInfo.getSymbol(), stockInfo);
                    }

                    //设置时间区间的临时值
                    tradeInfo = stockInfo.getItems().get(stockInfo.getItems().size() - 1);
                    tradeInfo.setSymbol(stockInfo.getSymbol());
                    stockInfo.setTmpTradeInfoM1(tradeInfo);

                    long period1 = AnalyzeInd.INSTANCE.analyzeHistIndex(InitAppConstant.MINUTE_1, stockInfo.getServerTime(), stockInfo.getSymbol(), stockInfo.getItems());
                    mRequestPeriod.get(stockInfo.getSymbol()).m1 = period1;
                }
                break;
            case InitData.PERIOD_LINE_5_MINUTE:
                stockInfo = (StockInfo)data;
                if (stockInfo.getItems().size() > 3) {
                    if (mStockInfos.containsKey(stockInfo.getSymbol())) {
                        mStockInfos.get(stockInfo.getSymbol()).setItems5(stockInfo.getItems());
                        stockInfo = mStockInfos.get(stockInfo.getSymbol());
                    } else {
                        stockInfo.setItems5(stockInfo.getItems());
                        stockInfo.setItems(null);
                        mStockInfos.put(stockInfo.getSymbol(), stockInfo);
                    }

                    tradeInfo = stockInfo.getItems5().get(stockInfo.getItems5().size() - 1);
                    tradeInfo.setSymbol(stockInfo.getSymbol());
                    stockInfo.setTmpTradeInfoM5(tradeInfo);

                    long period5 = AnalyzeInd.INSTANCE.analyzeHistIndex(InitAppConstant.MINUTE_5, stockInfo.getServerTime(), stockInfo.getSymbol(), stockInfo.getItems5());
                    mRequestPeriod.get(stockInfo.getSymbol()).m5 = period5;
                }
                break;
            case InitData.PERIOD_LINE_15_MINUTE:
                stockInfo = (StockInfo)data;
                if (stockInfo.getItems().size() > 3) {
                    if (mStockInfos.containsKey(stockInfo.getSymbol())) {
                        mStockInfos.get(stockInfo.getSymbol()).setItems15(stockInfo.getItems());
                        stockInfo = mStockInfos.get(stockInfo.getSymbol());
                    } else {
                        stockInfo.setItems15(stockInfo.getItems());
                        stockInfo.setItems(null);
                        mStockInfos.put(stockInfo.getSymbol(), stockInfo);
                    }

                    tradeInfo = stockInfo.getItems15().get(stockInfo.getItems15().size() - 1);
                    tradeInfo.setSymbol(stockInfo.getSymbol());
                    stockInfo.setTmpTradeInfoM15(tradeInfo);

                    long period15 = AnalyzeInd.INSTANCE.analyzeHistIndex(InitAppConstant.MINUTE_15, stockInfo.getServerTime(), stockInfo.getSymbol(), stockInfo.getItems15());
                    mRequestPeriod.get(stockInfo.getSymbol()).m15 = period15;
                }
                break;
        }
    }

    @Override
    public void onError(String method, int connCode, String data) {
        packMsgAndSend(MessageId.TOAST_TIP, data);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (super.handleMessage(msg)) {
            return true;
        }

        switch (msg.what) {
            case MessageId.FORECAST_DATA_LIST:
                mIndicatorDetailFra.updateItem(mStockStrategy.get(msg.obj).getPos());
                break;
            case MessageId.FORECAST_DATA_ADD_CODE:
                mIndicatorDetailFra.appendItem(mStockStrategy.get(msg.obj));
                break;
            case MessageId.FORECAST_RECORD_ADD:
                mForecastRecordFra.addHead((StockStrategy) msg.obj);
                break;
            case MessageId.UPDATE_START_STOP:
                updateStartStop();
                break;
            case MessageId.CLEAR_APP_DATA:
                mStockInfos.clear();
//                requestDatas();
                break;
            case MessageId.SAVE_EXCHANGE_DATA:
                saveExchangeData();
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            return;
        }

        //设置
        if (1 == requestCode) {
            String[] codes = data.getStringArrayExtra("newCodes");
            String[] removeCodes = data.getStringArrayExtra("removeCodes");

            //请求新的code
            if (null != codes && 0 < codes.length) {
                for (String code : codes) {
                    BaseApplication.mStockCodes.add(code);
                    mRequestPeriod.put(code, new RequestPeriod());
                    ToolRequest.getInstance().getStockInfo(InitNetInfo.PERIOD_CANDLE_1_MINUTE, code, -1, this);
                }
            }


            //存储的数据也清除
            if (null != removeCodes && 0 < removeCodes.length) {
                for (String code : removeCodes) {
                    mRequestPeriod.remove(code);
                    mStockInfos.remove(code);
                    BaseApplication.mStockCodes.remove(code);
                }
            }

            if ((null != codes && 0 < codes.length) || (null != removeCodes && 0 < removeCodes.length)) {
                ToolSharePre.putObject(InitData.TigerStockCodes, BaseApplication.mStockCodes);
            }
        }
    }


    private void updateStartStop() {
        MenuItem menuItem = mMainMenu.findItem(R.id.startOrStop);
        mIsStart = !mIsStart;

        //开始操作
        if (mIsStart) {
            if (startService()) {
                menuItem.setTitle("结束");
            }

        //结束操作
        } else {
            menuItem.setTitle("开始");
            ToolThreadManage.getInstance().destroyUDP();
        }
    }

    // TODO: 2017/8/11 请求服务器对照时间，然后定时，股市开市自动请求数据。
    private boolean startService() {
//        if (test()) {
//            return false;
//        }

        if (0 == BaseApplication.mStockCodes.size()) {
            ToolToast.shortToast(this, "目前没有代码可查询");
            return false;
        }

        //先请求udp更新数据，以便和历史数据相错不多
        String symbols = ToolString.arySperator(BaseApplication.mStockCodes, "|");
        ToolThreadManage.getInstance().initDate("hq", symbols, new PushCallback() {
            @Override
            public void onUDP_push(TradeInfoSimple tradeInfo) {
                organizeNewToList(tradeInfo);
            }
        });


        //请求新数据，以便于计算数据连贯
        requestDatas();
        return true;
    }

    /**
     * 保存所有外汇数据，文件名symbol + type + BaseApplication.mForecastRecordFileName
     */
    private void saveExchangeData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (StockInfo stockInfo : mStockInfos.values()) {
                    try {
                        String jsonObject = ToolGson.castObjectJson(stockInfo);
                        ToolFile.saveAppData(stockInfo.getSymbol() + BaseApplication.mForecastRecordFileName, jsonObject);
                    } catch (Exception ex) {
                        ToolLog.e(ex.getMessage());
                    }
                }
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolThreadManage.getInstance().destroyUDP();
    }

    @Override
    public void onBackPressed() {
        //双击退出
        if (System.currentTimeMillis() - mLastBackClick < 1000) {
            super.onBackPressed();
        } else {
            mLastBackClick = System.currentTimeMillis();
        }

    }
}
