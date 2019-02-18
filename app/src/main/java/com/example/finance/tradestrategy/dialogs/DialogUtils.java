package com.example.finance.tradestrategy.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.finance.tradestrategy.R;

/**
 * Created by Administrator on 2017/6/12.
 */

public class DialogUtils {

    public static Dialog showLoading(Context context, String content,boolean isCancel) {
        android.app.ProgressDialog dialog = new android.app.ProgressDialog(context);
        dialog.setMessage(content);
        dialog.setCancelable(isCancel);
        return dialog;
    }

    //通用：title、content、confirm
    public static Dialog showTitleContentConfirmDialog(Context context, String title, String content, final DialogInterface.OnClickListener rightListener) {
        final Dialog dialog = new Dialog(context, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_title_content_know);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = 0.3f;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        ((TextView) window.findViewById(R.id.title)).setText(title);
        ((TextView) window.findViewById(R.id.content)).setText(content);
        TextView right = (TextView) window.findViewById(R.id.iKnow);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == dialog) {
                    ((Activity)v.getContext()).finish();
                    return;
                }

                if (null == rightListener) {
                    dialog.dismiss();
                } else {
                    rightListener.onClick(dialog, v.getId());
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }


    public interface OnSubmitListener {
        void onClick(DialogInterface dialog, String value1, String value2);
    }

    //通用：title、two edit、confirm
    public static Dialog showTitleEditDialog(Context context, String title, final OnSubmitListener listener) {
        final Dialog dialog = new Dialog(context, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_title_edit_know);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = 0.3f;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        ((TextView) window.findViewById(R.id.title)).setText(title);
        TextView right = (TextView) window.findViewById(R.id.iKnow);
        final EditText account = (EditText) window.findViewById(R.id.account);
        final EditText password = (EditText) window.findViewById(R.id.password);

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == dialog) {
                    ((Activity)v.getContext()).finish();
                    return;
                }

                if (null == listener) {
                    dialog.dismiss();
                } else {
                    listener.onClick(dialog, account.getText().toString(), password.getText().toString());
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }
}
