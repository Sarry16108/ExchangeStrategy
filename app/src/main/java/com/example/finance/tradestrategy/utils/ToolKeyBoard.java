package com.example.finance.tradestrategy.utils;


import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

//打开或关闭软键盘
public class ToolKeyBoard {
    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param editText 输入框
     */
    public static void closeKeybord(EditText editText) {
        closeKeybord(editText.getWindowToken(), editText.getContext());
    }

    public static void closeKeybord(IBinder token, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(token, 0);
    }

    public static void hideKeyboard(Activity activity) {
        if (null != activity) {
            InputMethodManager imm = ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE));
            if (null != imm) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
