package com.example.finance.tradestrategy.utils;

import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.finance.tradestrategy.base.BaseApplication;

/**
 * Created by Administrator on 2017/6/5.
 */

public class ToolRingtone {
    private static MediaPlayer mPlayer;

    //播放通知铃声
    public static void playRing() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(BaseApplication.mContext, notification);
        r.play();
    }

    public static void release() {
        if (null != mPlayer) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
