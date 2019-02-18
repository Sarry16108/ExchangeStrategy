package com.example.finance.tradestrategy.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.annotations.Nullable;
import com.example.finance.tradestrategy.BR;
import com.example.finance.tradestrategy.R;
import com.example.finance.tradestrategy.activities.StockInfoAct;
import com.example.finance.tradestrategy.adapters.CommonAdapter;
import com.example.finance.tradestrategy.baseui.BaseFragment;
import com.example.finance.tradestrategy.databinding.FragIndicatorDetailBinding;
import com.example.finance.tradestrategy.entity.StockStrategy;

/**
 * Created by Administrator on 2017/6/6.
 * 分析列表页
 */

public class IndicatorDetailFrag extends BaseFragment implements CommonAdapter.OnLongClickListener<StockStrategy> {

    private FragIndicatorDetailBinding mBinding;
    private CommonAdapter<StockStrategy> mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frag_indicator_detail, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    public void initData() {
        //初始化列表
        mAdapter = new CommonAdapter<StockStrategy>(getContext(), R.layout.layout_item_indicator_simple, null) {
            @Override
            public void convert(NewViewHolder holder, int pos, StockStrategy stockInfo) {
                holder.setVariable(BR.stockStrategy, stockInfo);
            }
        };
        mAdapter.setOnLongClickListener(this);

        mBinding.showDatas.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mBinding.showDatas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.showDatas.setAdapter(mAdapter);

    }

    public void appendItem(StockStrategy item) {
        if (null == mAdapter) {
            return;
        }
        mAdapter.appendItem(item);
    }

    public void updateItem(int pos) {
        mAdapter.updateItem(pos);
    }

    @Override
    public void onClick(CommonAdapter.NewViewHolder holder, int postion, StockStrategy value) {
        Intent intent = new Intent(getActivity(), StockInfoAct.class);
        intent.putExtra("symbol", value.getSymbol());
        startActivity(intent);
    }
}
