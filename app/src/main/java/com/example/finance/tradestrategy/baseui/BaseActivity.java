package com.example.finance.tradestrategy.baseui;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.example.finance.tradestrategy.dialogs.DialogUtils;
import com.example.finance.tradestrategy.globaldata.MessageId;
import com.example.finance.tradestrategy.utils.ToolFile;
import com.example.finance.tradestrategy.utils.ToolToast;


/**
 * Created by Administrator on 2017/6/1.
 */

public class BaseActivity extends AppCompatActivity implements Handler.Callback{
    protected final String TAG = getClass().getSimpleName();

    protected Handler   mHandler = new Handler(this);


    public void packMsgAndSend(int what) {
        mHandler.sendEmptyMessage(what);
    }

    public void packMsgAndSend(int what, Object object) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = object;
        mHandler.sendMessage(msg);
    }


    public void packDelayMsgAndSend(int what, Object object, long delay) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = object;
        mHandler.sendMessageDelayed(msg, delay);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MessageId.TOAST_TIP:
                ToolToast.shortToast(this, (String) msg.obj);
                break;
            case MessageId.TOAST_TIP_LONG:
                ToolToast.longToast(this, (String) msg.obj);
                break;
            case MessageId.ACCOUNT_LOGIN:
                DialogUtils.showTitleEditDialog(this, "用户登录",  new DialogUtils.OnSubmitListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String value1, String value2) {
                        if (TextUtils.isEmpty(value1) || TextUtils.isEmpty(value2)) {
                            packMsgAndSend(MessageId.TOAST_TIP, "请输入账号和密码");
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
                break;
            case MessageId.CLEAR_APP_DATA:
                ToolFile.clearFiles("_history");
                break;
        }
        return false;
    }
}
