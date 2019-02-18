package com.example.finance.tradestrategy.fx678;

import com.example.finance.tradestrategy.entity.TradeInfoSimple;
import com.example.finance.tradestrategy.utils.ToolGson;
import com.example.finance.tradestrategy.utils.ToolLog;

import java.io.IOException;

/**
 * Created by Administrator on 2017/9/6.
 */

public class UDPThread extends Thread {
    private final String TAG = "UDPThread";

    private int activeTimesNoData = 0;
    private PushCallback mCallback;
    private boolean running = false;
    private long sendActiveTime = 0L;

    public boolean getFlag()
    {
        return this.running;
    }

    public void run()
    {
        if (null == ToolRequestHT.getInstance().client || ToolRequestHT.getInstance().client.isClosed()) {
            return;
        }

        sendActiveTime = System.currentTimeMillis() + 10000;
        try {

            while (running) {
                ToolRequestHT.getInstance().initrecpacket();
                ToolRequestHT.getInstance().client.receive(ToolRequestHT.getInstance().recpacket);

                String response = new String(ToolRequestHT.getInstance().recpacket.getData()).trim();

                ToolLog.d("response" + response);

                //102,201,202301,302,等
                if (3 == response.length()) {
                    switch (response) {
                        case "102":
                            ToolLog.d("response 请求数据应答成功");
                            break;
                        case "202":
                            ToolLog.d("response 请求保持激活应答成功 " + (++activeTimesNoData) + " 次");
                            break;
                        case "302":
                            ToolLog.d("response 请求结束应答成功");
                            break;
                    }
                } else {
                    mCallback.onUDP_push(ToolGson.castJsonObject(response, TradeInfoSimple.class));
                }

                //发送保持激活消息
                if (sendActiveTime <= System.currentTimeMillis()) {
                    sendActiveTime = System.currentTimeMillis() + 10000;
                    ToolRequestHT.getInstance().sendActive();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFlag(boolean paramBoolean)
    {
        this.running = paramBoolean;
        this.activeTimesNoData = 0;
    }

    public void setmCallback(PushCallback paramg)
    {
        this.mCallback = paramg;
    }
}