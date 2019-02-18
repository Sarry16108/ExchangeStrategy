package com.example.finance.tradestrategy.fx678;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.finance.tradestrategy.databindings.DatabindingUtls;
import com.example.finance.tradestrategy.entity.BaseResponse;
import com.example.finance.tradestrategy.entity.NetCallback;
import com.example.finance.tradestrategy.entity.StockInfo;
import com.example.finance.tradestrategy.globaldata.InitNetInfo;
import com.example.finance.tradestrategy.utils.ToolFile;
import com.example.finance.tradestrategy.utils.ToolGson;
import com.example.finance.tradestrategy.utils.ToolLog;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/9/6.
 * 主要为汇通使用
 */

public class   ToolRequestHT {

    private final String TAG = "ToolRequestHT";

    public static ToolRequestHT  instance;
    private InetAddress address;
    public DatagramSocket client;
    private DatagramPacket activepacket;
    private DatagramPacket loginpacket;
    private DatagramPacket logoutpacket;
    public DatagramPacket recpacket;

    private String  flag = "hq";
    public String selected;
    private byte[] sendActiveBuf;
    private String sendActiveStr;
    private byte[] sendLoginBuf;
    private String sendLoginStr;
    private byte[] sendLogoutBuf;
    private String sendLogoutStr;
    private byte[] recBuf;

    private int     count = 0;

    public ToolRequestHT() {
        initClient();
    }

    public static synchronized ToolRequestHT getInstance() {
        if (null == instance) {
            synchronized (ToolRequestHT.class) {
                if (null == instance) {
                    instance = new ToolRequestHT();
                }

                return instance;
            }
        }

        return instance;
    }


    private void initClient()
    {
        if (this.client == null) {
            try
            {
                this.client = new DatagramSocket();
                return;
            }
            catch (SocketException localSocketException)
            {
                localSocketException.printStackTrace();
            }
        }

    }

    private void initAddress()
    {
        if (this.address == null) {
            try
            {
                this.address = InetAddress.getByName(HQ_NET.getUDP_IP(this.flag));
                return;
            }
            catch (UnknownHostException localUnknownHostException)
            {
                localUnknownHostException.printStackTrace();
            }
        }

    }

    /**
     * 初始化receive packet
     */
    public void initrecpacket()
    {
        if (null == this.recBuf) {
            this.recBuf = new byte[2048];
            this.recpacket = new DatagramPacket(this.recBuf, this.recBuf.length);
        } else {
            Arrays.fill(this.recBuf, (byte)'\0');
        }

    }

    /**
     * 初始化请求数据的packet
     * 请求：101,EURUSD（如果请求多个代码，中间用“|”分割）
     返回：102
     继续返回：(每组返回都包括usd)
     {"e":"WH","d":"USD","o":"92.0016","h":"92.1722","l":"91.7958","c":"92.0644","p":"92.0016","t":"1505755255"}
     {"e":"WH","d":"EURUSD","o":"1.1946","h":"1.1969","l":"1.1915","c":"1.1939","p":"1.1945","t":"1505755255"

     * @param paramString
     */
    private void initLogin(String paramString)
    {
        this.selected = paramString;
        this.sendLoginStr = "101,PA_EX".replaceFirst("PA_EX", this.selected);
        this.sendLoginBuf = this.sendLoginStr.getBytes();
        this.loginpacket = new DatagramPacket(this.sendLoginBuf, this.sendLoginBuf.length, this.address, 26001);

        ToolLog.d("send login Str" + this.sendLoginStr);
    }

    /**
     * 初始化保持udp链接packet
     */
    private void setDataActiveUDP()
    {
        if ((this.client != null) && (this.address != null)) {
            if (this.activepacket == null)
            {
                this.sendActiveStr = "201";
                this.sendActiveBuf = this.sendActiveStr.getBytes();
                this.activepacket = new DatagramPacket(this.sendActiveBuf, this.sendActiveBuf.length, this.address, 26001);

                ToolLog.d("send DataActive Str" + this.sendActiveStr);
            }
        }
        try
        {
            this.client.send(this.activepacket);
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public void destroyUDP()
    {
        if (this.client != null)
            this.selected = "";
        try
        {
            if (this.address == null)
                this.address = InetAddress.getByName(HQ_NET.getUDP_IP(this.flag));
            if (this.logoutpacket == null)
            {
                this.sendLogoutStr = "301";
                this.sendLogoutBuf = this.sendLogoutStr.getBytes();
                this.logoutpacket = new DatagramPacket(this.sendLogoutBuf, this.sendLogoutBuf.length, this.address, 26001);
            }
            if (this.client != null)
            {
                this.client.send(this.logoutpacket);
                this.client.close();
            }
            this.address = null;
            this.loginpacket = null;
            this.activepacket = null;
            this.logoutpacket = null;
            if (this.client != null)
            {
                this.client.close();
                this.client = null;
            }
            return;
        }
        catch (IOException localIOException)
        {
            while (true)
            {
                localIOException.printStackTrace();
                this.address = null;
                this.loginpacket = null;
                this.activepacket = null;
                this.logoutpacket = null;
                if (this.client != null)
                {
                    this.client.close();
                    this.client = null;
                }
            }
        }
        finally
        {
            this.address = null;
            this.loginpacket = null;
            this.activepacket = null;
            this.logoutpacket = null;
            if (this.client != null)
            {
                this.client.close();
                this.client = null;
            }
        }
    }


    public void initUDP(String paramString1, final String paramString2)
    {
        this.flag = paramString1;
        new Thread("initLogin---" + this.count)
        {
            public void run()
            {
                ToolRequestHT.this.initAddress();
                ToolRequestHT.this.initrecpacket();
                ToolRequestHT.this.setLogin(paramString2);
            }
        }.start();
        this.count = (1 + this.count);
    }

    public void sendActive()
    {
        setDataActiveUDP();
    }

    public void sendLogin()
    {
        setLogin(this.selected);
    }

    public void sendUDP()
    {
        initUDP(this.flag, this.selected);
    }

    public void setLogin(String paramString)
    {
        if ((!TextUtils.isEmpty(paramString)) && (this.client != null) && (this.address != null)) {
            try
            {
                initLogin(paramString);
                this.client.send(this.loginpacket);
                ToolThreadManage.getInstance().runThreadFlags();
                return;
            }
            catch (Exception localException)
            {
                while (true)
                    localException.printStackTrace();
            }
        }

    }

    /**
     * 结束udp
     */
    public void stopUDP()
    {
        if ((this.client != null) && (this.address != null)) {
            try
            {
                if (this.logoutpacket == null)
                {
                    this.sendLogoutStr = "301";
                    this.sendLogoutBuf = this.sendLogoutStr.getBytes();
                    this.logoutpacket = new DatagramPacket(this.sendLogoutBuf, this.sendLogoutBuf.length, this.address, 26001);
                    ToolLog.d("send stop Str" + this.sendLogoutStr);
                }
                this.client.send(this.logoutpacket);
                return;
            }
            catch (IOException localIOException)
            {
                localIOException.printStackTrace();
            }
        }

    }


    /*********************************************************************************************************
     * 以上为udp请求相关，以下为http请求相关
     */

    private boolean SIMULATE_DATA = false;     //true使用模拟数据

    private String getSymbolType(String symbol, String type) {
        return symbol + "_" + type + "_history.txt";
    }

    public void getWhHistoryData(String symbol, String type, long endTime, NetCallback callback) {
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        String excode = DatabindingUtls.getSymbolExcode(symbol);
        String key = HQ_NET.getKey(excode + symbol + type + endTime + curTime);
        String urlAndParams = HQ_NET.getUrlMarketKline(HQ_NET.getDomain("hq"), excode, symbol, type, String.valueOf(endTime), curTime, key);
        threadRequest(urlAndParams, symbol, type, callback, StockInfo.class);
    }

    private <T extends BaseResponse> void threadRequest(final String urlAndParams, final String symbol, final String type, final NetCallback callback, final @NonNull Class<T> classType) {
        //模拟测试数据部分
        if (SIMULATE_DATA) {
            if (null != callback) {
                String data = ToolFile.readAppData(getSymbolType(symbol, type));
                if (null != data && 0 != data.length()) {
                    T baseResponse = ToolGson.castJsonObject(data, classType);
                    baseResponse.setSymbol(symbol);
                    baseResponse.setType(type);
                    baseResponse.setName(DatabindingUtls.getSymbolName(symbol));
                    callback.onSuccess(type, baseResponse);
                    return;
                }
            } else {
                return;
            }
        }

        //执行请求任务
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doGetRequest(urlAndParams, symbol, type, callback, classType);
            }
        });
    }

    private <T extends BaseResponse> void doGetRequest(String urlAndParams, String symbol, String type, NetCallback callback, Class<T> classType) {
        HttpURLConnection connection = null;
        try {
            //拼接url
            ToolLog.d(urlAndParams);
            URL url = new URL(urlAndParams);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(InitNetInfo.MODE_GET);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("User-Agent", "Apache-HttpClient/UNAVAILABLE (java 1.4)");

            // TODO: 2017/9/24 以下两句加上后就变成POST方式传递数据了
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
            
            connection.setConnectTimeout(2 * 1000);

            if (connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder builder = new StringBuilder();
                char[] value = new char[2048];
                int len = 0;
                while (-1 != (len = bufferedReader.read(value))) {
                    builder.append(value, 0, len);
                }

                try {
                    baseProcess(connection, symbol, type, builder.toString(), callback, classType);
                } catch (Exception e) {
                    ToolLog.e(e.getMessage());
                }
            } else {
                if (null != callback) {
                    callback.onError(getSymbolType(symbol, type), connection.getResponseCode(), connection.getResponseMessage());
                } else {
                    ToolLog.e("doGetRequest", getSymbolType(symbol, type), "errorCode:" + connection.getResponseCode() + " message:" + connection.getResponseMessage());
                }
            }
        } catch (JsonParseException e) {
            if (null != callback) {
                callback.onError(getSymbolType(symbol, type), 0, e.getMessage());
            } else {
                ToolLog.e("doGetRequest", getSymbolType(symbol, type), "message:" + e.getMessage());
            }
        } catch (IOException e) {
            if (null != callback) {
                callback.onError(getSymbolType(symbol, type), 0, e.getMessage());
            } else {
                ToolLog.e("doGetRequest", getSymbolType(symbol, type), "message:" + e.getMessage());
            }
        } finally {
            connection.disconnect();
        }
        ToolLog.i(TAG, "doGetRequest", "===========================================================");
    }

    private <T extends BaseResponse> void baseProcess(HttpURLConnection connection, String symbol, String type, String json,
                                                      NetCallback callback, Class<T> classType) throws IOException {
        String symbolType = getSymbolType(symbol, type);
        //保存模拟测试数据
        if (SIMULATE_DATA) {
            ToolFile.saveAppData(symbolType, json);
        }

        ToolLog.i(TAG, "doGetRequest", "url:" + symbolType + "   result:" + json);
        switch (symbolType) {
            default:
                T baseResponse = ToolGson.castJsonObject(json, classType);
                if (baseResponse.isRet()) {
                    if (null != callback) {
                        baseResponse.setSymbol(symbol);
                        baseResponse.setType(type);
                        baseResponse.setName(DatabindingUtls.getSymbolName(symbol));
                        callback.onSuccess(type, baseResponse);
                    } else {
                        ToolLog.d("doPostRequest success: 200  values: ");
                    }
                } else {
                    if (null != callback) {
                        callback.onError(symbolType, connection.getResponseCode(), baseResponse.getError());
                    } else {
                        ToolLog.e("doPostRequest success: 200  values: " + json);
                    }
                }
                break;

        }
    }

}
