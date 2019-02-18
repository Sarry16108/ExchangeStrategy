package com.example.finance.tradestrategy.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.finance.tradestrategy.baseui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */

public class BaseViewpagerAdapter<T extends BaseFragment> extends FragmentPagerAdapter {

    private List<T> mDatas;

    public BaseViewpagerAdapter(FragmentManager fm, List<T> datas) {
        super(fm);
        if (null == datas) {
            mDatas = new ArrayList<>(2);
        } else {
            mDatas = datas;
        }
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
//    }
}
