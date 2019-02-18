package com.example.finance.tradestrategy.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.finance.tradestrategy.base.BaseApplication;

/**
 * Created by Administrator on 2017/6/5.
 */

public class ToolToast {

    public static void shortToast(Context context, String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }


    public static void longToast(Context context, String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }

    public static void shortToast(Context context, int res) {
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }
}
