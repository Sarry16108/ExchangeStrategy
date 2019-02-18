package com.example.finance.tradestrategy;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.finance.tradestrategy.utils.ToolLog;

/**
 * Created by Administrator on 2017/9/29.
 * 保持服务不被清除掉的方法。
 * http://www.jianshu.com/p/06a1a434e057
 */

public class ActiveService extends Service {

    private static int notificationId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class InnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            ToolLog.d("inner service is created");
            startForeground(notificationId, new Notification());
            stopSelf();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            ToolLog.d("inner service is destroied");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(notificationId, new Notification());
        startInnerService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToolLog.d("service is destroied");
    }

    private void startInnerService() {
        Intent intent = new Intent(this, InnerService.class);
        startService(intent);
    }
}
