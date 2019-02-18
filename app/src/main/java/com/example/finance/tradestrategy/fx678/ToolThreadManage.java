package com.example.finance.tradestrategy.fx678;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/9/6.
 * //请求指定代码数据
     request：101 		后面加请求的代码，多个代码用“|”分割
     reponse：102		如果请求的是多个代码，那么多个代码是分别以下json格式发送，中间会有USD

     {"e":"WH","d":"GBPUSD","o":"1.3494","h":"1.3508","l":"1.3473","c":"1.3494","p":"1.3495","t":"1505964660"}


     request: 201		10秒请求一次
     response: 202


     结束当前操作
     request：301		请求两次
     reponse：302

 */

public class ToolThreadManage {

    public static ToolThreadManage  instance;
    private String code;
    private int count = 0;
    private String flag;
    private boolean isStoped = false;
    private PushCallback mCallback;
    private UDPThread udpThread;

    public static synchronized ToolThreadManage getInstance() {
        if (null == instance) {
            synchronized (ToolThreadManage.class) {
                if (null == instance) {
                    instance = new ToolThreadManage();
                }

                return instance;
            }
        }

        return instance;
    }


    private void initThread()
    {
        this.isStoped = false;
        if (this.udpThread == null)
        {
            this.udpThread = new UDPThread();
            this.udpThread.setmCallback(this.mCallback);
            this.udpThread.setName("udp-price-" + this.flag);
            this.udpThread.start();
            return;
        }
        this.udpThread.setmCallback(this.mCallback);
    }

    public void destroyUDP()
    {
        new Thread("onStop" + this.count)
        {
            public void run()
            {
                ToolRequestHT.getInstance().stopUDP();
            }
        }.start();
        stopUDP();
    }

    //WGJS|XAU,WGJS|XAG,NYMEX|CONC,WH|USD,WH|EURUSD,WH|AUDUSD,WH|GBPUSD,WH|USDJPY
    public void initDate(final String paramString1, final String paramString2, PushCallback paramg)
    {
        if (TextUtils.isEmpty(paramString2))
            return;

        this.flag = paramString1;
        this.code = paramString2;
        this.mCallback = paramg;
        this.count = 0;
        initThread();
        stopUDP();
        new Thread("initLogin---" + this.count)
        {
            public void run()
            {
                try
                {
                    sleep(1200L);
                    ToolRequestHT.getInstance().initUDP(paramString1, paramString2);
                    return;
                } catch (InterruptedException localInterruptedException)
                {
                    localInterruptedException.printStackTrace();
                }
            }
        }.start();
    }

    public void pauseThreadFlags()
    {
        if (this.udpThread != null)
            this.udpThread.setFlag(false);
    }

    public void resetData()
    {
        if (this.udpThread != null)
            initDate(this.flag, this.code, this.mCallback);
    }

    /**
     * 接收数据
     */
    public void runThreadFlags()
    {
        this.isStoped = false;
        if (this.udpThread != null)
        {
            this.udpThread.setFlag(true);
            this.udpThread.run();
        }
    }

    public void stopUDP()
    {
        if ((this.udpThread != null) && (!this.udpThread.getFlag()) && (this.isStoped))
            return;
        pauseThreadFlags();
        this.isStoped = true;
        this.count = (1 + this.count);
        new Thread("onStop" + this.count)
        {
            public void run()
            {
                ToolRequestHT.getInstance().stopUDP();
            }
        }.start();
    }
}
