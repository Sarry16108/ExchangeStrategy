package com.example.finance.tradestrategy.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.annotations.Nullable;
import com.android.databinding.library.baseAdapters.BR;
import com.example.finance.tradestrategy.R;
import com.example.finance.tradestrategy.adapters.CommonAdapter;
import com.example.finance.tradestrategy.base.BaseApplication;
import com.example.finance.tradestrategy.baseui.BaseFragment;
import com.example.finance.tradestrategy.databinding.FragForecastRecordBinding;
import com.example.finance.tradestrategy.databindings.DatabindingUtls;
import com.example.finance.tradestrategy.entity.StockForecast;
import com.example.finance.tradestrategy.entity.StockStrategy;
import com.example.finance.tradestrategy.utils.ToolFile;

/**
 * Created by Administrator on 2017/6/6.
 * 提醒记录
 */

public class ForecastRecordFrag extends BaseFragment {

    private FragForecastRecordBinding mBinding;
    private CommonAdapter<StockForecast> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frag_forecast_record, null, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }


    public void initData() {
        //初始化列表
        mAdapter = new CommonAdapter<StockForecast>(getContext(), R.layout.layout_item_forecast_record, null) {
            @Override
            public void convert(NewViewHolder holder, int pos, StockForecast stockInfo) {
                holder.setVariable(BR.stockForecast, stockInfo);
            }
        };
        mAdapter.setMaxSize(100);

        mBinding.showDatas.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mBinding.showDatas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.showDatas.setAdapter(mAdapter);
    }

    public void addHead(StockStrategy strategy) {
        StockForecast forecast = new StockForecast(strategy.getNameCN(), strategy.getSymbol(), strategy.getClose(),
                DatabindingUtls.getTip(strategy), System.currentTimeMillis());
        mAdapter.addHeadItem(forecast);

        ToolFile.appendFileData(BaseApplication.mForecastRecordFileName, forecast.toString());
    }
}
