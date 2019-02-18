package com.example.finance.tradestrategy.utils;

import android.content.ClipboardManager;
import android.content.Context;

import com.example.finance.tradestrategy.base.BaseApplication;

/**
 * Created by yanghj on 2017/6/21.
 */

public class ToolClipboard {

    public static boolean copyToClipboard(String text) {
        try {
            ClipboardManager cm = (ClipboardManager) BaseApplication.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(text);
        } catch (Exception ex) {
            return false;
        }

        return true;
    }
}
