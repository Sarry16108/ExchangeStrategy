package com.example.finance.tradestrategy.baseui;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2017/6/6.
 */

public class BaseFragment extends Fragment implements Handler.Callback{
    protected final String TAG = getClass().getSimpleName();



    @Override
    public boolean handleMessage(Message msg) {
        if (getActivity() instanceof  BaseActivity) {
            return ((BaseActivity)getActivity()).handleMessage(msg);
        } else {
            return false;
        }
    }
}
