package com.example.finance.tradestrategy.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.finance.tradestrategy.entity.AuthenticationRes;
import com.example.finance.tradestrategy.entity.BaseResponse;
import com.example.finance.tradestrategy.entity.NetCallback;
import com.example.finance.tradestrategy.entity.QueryAuth;
import com.example.finance.tradestrategy.entity.QueryStr;
import com.example.finance.tradestrategy.entity.SearchSuggest;
import com.example.finance.tradestrategy.entity.StockInfo;
import com.example.finance.tradestrategy.entity.StocksBriefInfo;
import com.example.finance.tradestrategy.globaldata.InitNetInfo;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2017/6/1.
 */

public class ToolRequest {
    private final String TAG = "ToolRequest";
    private static ToolRequest mInstance = new ToolRequest();
    private QueryAuth mQueryAuth = new QueryAuth();
    private long mBeginTime = 0;       //请求所在的分钟，millisecond

    private String mQueryStr;
    private boolean SIMULATE_DATA = true;     //true使用模拟数据

    private ToolRequest() {
    }

    public static ToolRequest getInstance() {
        return mInstance;
    }

    //获取登录的信息
    public void getInitInfo() {

    }

    //https://oauth1.tigerbrokers.com/api/v4/auth/guest?vendor=xiaomi&osVer=6.0.1&platform=android&appName=TigerTrade&appVer=5.7.0&device=Redmi%203S&deviceId=c7539b2f-4ce4-444b-a1fb-c23cd5d75388&screenH=1280&skin=1&screenW=720&lang=zh_CN&token
    public void userAuthentication() {
        String queryParam = new QueryStr().formatDataDefault();
        threadRequest(InitNetInfo.MODE_POST, InitNetInfo.SERVER_AUTHENTICATION, InitNetInfo.AUTHEN_API_V4,
                null, queryParam, null, null, AuthenticationRes.class);
    }


    public void searchByStockCode(String stockCode, NetCallback callback) {
        String queryParameters = getDefaultQueryStr(-1);
        threadRequest(InitNetInfo.MODE_GET, InitNetInfo.SERVER_HOST, InitNetInfo.SEARCH_SUGGEST_V4,
                stockCode, queryParameters, null, callback, SearchSuggest.class);
    }

    //获取stock信息
    //https://hq.tigerbrokers.com/stock_info/candle_stick/1min/TSLA?
    public void getStockInfo(String period, String stockCode, long time, NetCallback callback) {
        String queryParameters = getDefaultQueryStr(time);
        threadRequest(InitNetInfo.MODE_GET, InitNetInfo.SERVER_HOST, period, stockCode, queryParameters, null, callback, StockInfo.class);
    }


    //获取stock信息，查询period时间点之前的数据信息
    //period：查询时间点
    //return：返回和getStockInfo一样，只是没有detail字段
    //https://hq.tigerbrokers.com/stock_info/candle_stick/1min/TSLA?
    public void getStockInfoBefore(String period, String stockCode, long time, NetCallback callback) {
        String queryParameters = getDefaultQueryStrEnd(time);
        threadRequest(InitNetInfo.MODE_GET, InitNetInfo.SERVER_HOST, period, stockCode, queryParameters, null, callback, StockInfo.class);
    }

    public void getStockInfoCookie() {
        userAuthentication();

        String queryParam = new QueryStr().formatDataDefault();
        String requestParam = "{\"items\":[{\"symbol\":\"BABA\"},{\"symbol\":\"JD\"}]}";
        threadRequest(InitNetInfo.MODE_POST, InitNetInfo.SERVER_HOST, InitNetInfo.STOCK_INFO_COOKIE, null, queryParam, requestParam, null, StocksBriefInfo.class);
    }

    private <T extends BaseResponse> void threadRequest(final String mode, final String host, final String method,
                                                        final String stockCode, final String queryParameters, final String requestData, final NetCallback callback, final @NonNull Class<T> classType) {
        //模拟测试数据部分
        if (SIMULATE_DATA) {
            if (null != callback) {
                String data = ToolFile.readAppData(ToolFile.makeName(method, stockCode, mBeginTime));
                if (null != data && 0 != data.length()) {
                    T baseResponse = ToolGson.castJsonObject(data, classType);
                    callback.onSuccess(method, baseResponse);
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
                if (InitNetInfo.MODE_POST.equals(mode)) {
                    doPostRequest(host, method, stockCode, requestData, callback, classType);
                } else {
                    doGetRequest(host, method, stockCode, queryParameters, requestData, callback, classType);
                }

            }
        });
    }

    /**
     * 默认请求period时间段的
     */
    private String getDefaultQueryStr(long period) {
        mBeginTime = period;

        QueryStr queryStr = new QueryStr();
        queryStr.setBeginTime(period);
        mQueryStr = queryStr.formatStart();

        return mQueryStr.toString();
    }

    /**
     * 该时间点之前的记录
     * @param period
     * @return
     */
    private String getDefaultQueryStrEnd(long period) {
        mBeginTime = period;

        QueryStr queryStr = new QueryStr();
        queryStr.setEndTime(period);
        mQueryStr = queryStr.formatEnd();

        return mQueryStr.toString();
    }

    /**
     * https://hq.tigerbrokers.com/stock_info/candle_stick/3min/BABA.jhtml
     *
     * @param host        biz-tuan.uspard.com
     * @param method      freeBullBearHoldTodayPub
     * @param requestData
     * @param callback
     * @param classType
     * @param <T>
     */
    private <T extends BaseResponse> void doPostRequest(String host, String method,
                                                        final String stockCode, String requestData, NetCallback callback, Class<T> classType) {
        HttpURLConnection connection = null;
        try {
            StringBuilder urlString = new StringBuilder("https://");
            urlString.append(host).append(method);
            if (!TextUtils.isEmpty(stockCode)) {
                urlString.append('/').append(stockCode);
            }
            URL url = new URL(urlString.toString());
            connection = (HttpURLConnection) url.openConnection();

            //cookie必须放在最开始，否则会在设置Cookie时候出现异常

            if (!TextUtils.isEmpty(mQueryAuth.getCookie())) {
                connection.setRequestProperty("Cookie", mQueryAuth.getCookie());
            }

            connection.setRequestMethod(InitNetInfo.MODE_POST);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("User-Agent", "okhttp/3.6.0");
            connection.setRequestProperty("X-NewRelic-ID", "XAUGVl9TGwYDVFVRAAQ=");
            connection.setRequestProperty("Authorization", mQueryAuth.getAuthorization());

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(2 * 1000);

            if (!TextUtils.isEmpty(requestData)) {
                ToolLog.i(TAG, "doPostRequest", "content:" + requestData);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");    //针对info接口，无数据不传递该字段
                connection.setRequestProperty("Content-Length", String.valueOf(requestData.length()));

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(requestData);
                dataOutputStream.flush();
                dataOutputStream.close();
            } else {
                connection.setRequestProperty("Content-Length", "0");
            }

            if (connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream()), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                char[] value = new char[2048];
                int len = 0;
                while (-1 != (len = bufferedReader.read(value))) {
                    builder.append(value, 0, len);
                }

                try {
                    baseProcess(connection, method, stockCode, builder.toString(), callback, classType);
                } catch (Exception e) {
                    ToolLog.e(e.getMessage());
                }
            } else {
                if (null != callback) {
                    callback.onError(method, connection.getResponseCode(), connection.getResponseMessage());
                } else {
                    ToolLog.e("doPostRequest", method, "errorCode:" + connection.getResponseCode() + " message:" + connection.getResponseMessage());
                }
            }
            ToolLog.i(TAG, "doPostRequest", "response code:" + connection.getResponseCode());
        } catch (JsonParseException e) {
            if (null != callback) {
                callback.onError(method, 0, e.getMessage());
            } else {
                ToolLog.e("doPostRequest", method, "message:" + e.getMessage());
            }
        } catch (IOException e) {
            if (null != callback) {
                callback.onError(method, 0, e.getMessage());
            } else {
                ToolLog.e("doPostRequest", method, "message:" + e.getMessage());
            }
        } finally {
            connection.disconnect();
        }
        ToolLog.i(TAG, "doPostRequest", "===========================================================");
    }

    private <T extends BaseResponse> void doGetRequest(String host, String method,
                                                       final String stockCode, String queryParameters, String requestData, NetCallback callback, Class<T> classType) {
        HttpURLConnection connection = null;
        try {
            //拼接url
            StringBuilder urlString = new StringBuilder("https://");
            urlString.append(host).append(method);
            if (!TextUtils.isEmpty(stockCode)) {
                urlString.append('/').append(stockCode);
            }

            //请求的数据
            if (!TextUtils.isEmpty(requestData)) {
                urlString.append('?').append(requestData);
            } else {
                urlString.append('?').append(queryParameters);
            }

            URL url = new URL(urlString.toString());
            connection = (HttpURLConnection) url.openConnection();

            //cookie必须放在最开始，否则会在设置Cookie时候出现异常
            if (!TextUtils.isEmpty(mQueryAuth.getCookie())) {
                connection.setRequestProperty("Cookie", mQueryAuth.getCookie());
            }

            connection.setRequestMethod(InitNetInfo.MODE_GET);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("User-Agent", "okhttp/3.6.0");
            connection.setRequestProperty("X-NewRelic-ID", "XAUGVl9TGwYDVFVRAAQ=");
            connection.setRequestProperty("Authorization", mQueryAuth.getAuthorization());

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(2 * 1000);

            if (connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream()), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                char[] value = new char[2048];
                int len = 0;
                while (-1 != (len = bufferedReader.read(value))) {
                    builder.append(value, 0, len);
                }

                try {
                    baseProcess(connection, method, stockCode, builder.toString(), callback, classType);
                } catch (Exception e) {
                    ToolLog.e(e.getMessage());
                }
            } else {
                if (null != callback) {
                    callback.onError(method, connection.getResponseCode(), connection.getResponseMessage());
                } else {
                    ToolLog.e("doGetRequest", method, "errorCode:" + connection.getResponseCode() + " message:" + connection.getResponseMessage());
                }
            }
        } catch (JsonParseException e) {
            if (null != callback) {
                callback.onError(method, 0, e.getMessage());
            } else {
                ToolLog.e("doGetRequest", method, "message:" + e.getMessage());
            }
        } catch (IOException e) {
            if (null != callback) {
                callback.onError(method, 0, e.getMessage());
            } else {
                ToolLog.e("doGetRequest", method, "message:" + e.getMessage());
            }
        } finally {
            connection.disconnect();
        }
        ToolLog.i(TAG, "doGetRequest", "===========================================================");
    }

    private <T extends BaseResponse> void baseProcess(HttpURLConnection connection, String method, String stockCode, String json,
                                                      NetCallback callback, Class<T> classType) throws IOException {
        //保存模拟测试数据
        if (SIMULATE_DATA) {
            ToolFile.saveAppData(ToolFile.makeName(method, stockCode, mBeginTime), json);
        }

        ToolLog.i(TAG, "doPostRequest", "url:" + method + "result:" + json);
        switch (method) {
            case InitNetInfo.AUTHEN_API_V4:
                updateAuthentication(json);
                break;
            default:
                T baseResponse = ToolGson.castJsonObject(json, classType);
                if (baseResponse.isRet()) {
                    getCookie(connection);
                    if (null != callback) {
                        callback.onSuccess(method, baseResponse);
                    } else {
                        ToolLog.d("doPostRequest success: 200  values: ");
                    }
                } else {
                    if (null != callback) {
                        //{"error":"invalid_token","error_description":"Invalid access token: teGkGeauMglhkw418y220jO5hleAwByCwHGDr9NiFpACF761qN=\"\""}
                        callback.onError(method, connection.getResponseCode(), baseResponse.getError());
                    } else {
                        ToolLog.e("doPostRequest success: 200  values: " + json);
                    }
                }
                break;

        }
    }

    private void getCookie(URLConnection connection) {
        Map<String, List<String>> headers = connection.getHeaderFields();
        if (headers.containsKey("Set-Cookie")) {
            for (String item : headers.get("Set-Cookie")) {
                String keyValue = ToolString.takeBeforeFirstSperator(item, ";");
                String[] str = keyValue.split("=");
                switch (str[0]) {
                    case "JSESSIONID":
                        mQueryAuth.setSessionId(str[1]);
                        break;
                    case "ngxid":
                        mQueryAuth.setNgxid(str[1]);
                        break;
                    default:
                        continue;
                }
            }
        }
    }

    private void updateAuthentication(String json) {
        AuthenticationRes baseResponse = ToolGson.castJsonObject(json, AuthenticationRes.class);
        if (null != baseResponse && baseResponse.isIs_succ()) {
            mQueryAuth.setAuthorization("Bearer " + baseResponse.getData().getAccess_token());
        }
    }
}
