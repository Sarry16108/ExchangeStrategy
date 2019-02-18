package com.example.finance.tradestrategy.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.finance.tradestrategy.base.BaseApplication;
import com.example.finance.tradestrategy.globaldata.MessageId;

/**
 * Created by Administrator on 2017/6/12.
 */

public class ToolPermissionsChecker {

    private String [] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    public ToolPermissionsChecker() {

    }

    public boolean checkImportantPermissions() {
        return lacksPermissions(permissions);
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(BaseApplication.mContext, permission) != PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions(AppCompatActivity activity) {
        activity.requestPermissions(permissions, MessageId.RequestMainPermission);
    }
}
