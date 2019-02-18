package com.example.finance.tradestrategy.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Build;

import com.example.finance.tradestrategy.R;
import com.example.finance.tradestrategy.base.BaseApplication;
import com.example.finance.tradestrategy.databindings.DatabindingUtls;
import com.example.finance.tradestrategy.entity.StockStrategy;

/**
 * Created by Administrator on 2017/6/5.
 */

public class ToolNotification {
    private int mNotifications = 0;
    private final int MAX_COUNT = 4;

    private static class Init{
        public static ToolNotification mIntance = new ToolNotification();
    }

    public static ToolNotification getInstance() {
        return Init.mIntance;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) BaseApplication.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(BaseApplication.mContext)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        notificationManager.notify((mNotifications++) % MAX_COUNT, notification);
    }

    /**
     *
     */
    public void showNotification(StockStrategy strategy) {
        String title = strategy.getNameCN() + "(" + strategy.getSymbol() + ")";
        showNotification(title, DatabindingUtls.getTip(strategy));
    }
}
