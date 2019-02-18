package com.example.finance.tradestrategy.entity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/7/13.
 * 请求信息
 */

public class QueryStr {
    private String vendor="xiaomi";
    private String osVer="6.0.1";
    private String platform="android";
    private String appName="TigerTrade";
    private String appVer="5.5.0";
    private String device="Redmi 3S";
    private String deviceId="6c3d5b8a-5adf-4a0f-af1c-6848fc9035ee";
    private String screenH="1280";
    private String skin="1";
    private String screenW="720";
    private String lang="zh_CN";
    private long   beginTime = -1;      //请求发起所在的分钟
    private long   endTime = -1;        //请求该时间之前的记录
    private boolean includeHourTrading = true;
    private String token="";


    public String formatStart() {
        String value = "";
        try {
            value = "vendor=" + URLEncoder.encode(vendor, "UTF-8") +
                    "&osVer=" + URLEncoder.encode(osVer, "UTF-8") +
                    "&platform=" + URLEncoder.encode(platform, "UTF-8") +
                    "&appName=" + URLEncoder.encode(appName, "UTF-8") +
                    "&appVer=" + URLEncoder.encode(appVer, "UTF-8") +
                    "&device=" + URLEncoder.encode(device, "UTF-8") +
                    "&deviceId=" + deviceId +
                    "&screenH=" + screenH +
                    "&skin=" + skin +
                    "&screenW=" + screenW +
                    "&lang=" + lang +
                    "&beginTime=" + beginTime +
                    "&includeHourTrading=" + includeHourTrading +
                    "&token=" + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value.replace("+", "%20");
    }


    public String formatEnd() {
        String value = "";
        try {
            value = "vendor=" + URLEncoder.encode(vendor, "UTF-8") +
                    "&osVer=" + URLEncoder.encode(osVer, "UTF-8") +
                    "&platform=" + URLEncoder.encode(platform, "UTF-8") +
                    "&appName=" + URLEncoder.encode(appName, "UTF-8") +
                    "&appVer=" + URLEncoder.encode(appVer, "UTF-8") +
                    "&device=" + URLEncoder.encode(device, "UTF-8") +
                    "&deviceId=" + deviceId +
                    "&screenH=" + screenH +
                    "&skin=" + skin +
                    "&screenW=" + screenW +
                    "&lang=" + lang +
                    "&endTime=" + endTime +
                    "&includeHourTrading=" + includeHourTrading +
                    "&token=" + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value.replace("+", "%20");
    }

    public String formatDataDefault() {
        String value = "";
        try {
            value = "vendor=" + URLEncoder.encode(vendor, "UTF-8") +
                    "&osVer=" + URLEncoder.encode(osVer, "UTF-8") +
                    "&platform=" + URLEncoder.encode(platform, "UTF-8") +
                    "&appName=" + URLEncoder.encode(appName, "UTF-8") +
                    "&appVer=" + URLEncoder.encode(appVer, "UTF-8") +
                    "&device=" + URLEncoder.encode(device, "UTF-8") +
                    "&deviceId=" + deviceId +
                    "&screenH=" + screenH +
                    "&skin=" + skin +
                    "&screenW=" + screenW +
                    "&lang=" + lang +
                    "&token=" + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value.replace("+", "%20");
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getScreenH() {
        return screenH;
    }

    public void setScreenH(String screenH) {
        this.screenH = screenH;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getScreenW() {
        return screenW;
    }

    public void setScreenW(String screenW) {
        this.screenW = screenW;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isIncludeHourTrading() {
        return includeHourTrading;
    }

    public void setIncludeHourTrading(boolean includeHourTrading) {
        this.includeHourTrading = includeHourTrading;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
