package com.example.finance.tradestrategy.globaldata;

/**
 * Created by yanghj on 2017/6/4.
 */

public interface MessageId {

    //base activity 0 ~ 100;
    int     REFRESH_NETWORK_STATE = 1;      //网络状态
    int     TOAST_TIP               = 2;      //toast类型提示信息
    int     TOAST_TIP_LONG          = 3;    //toast长时提醒
    int     ACCOUNT_LOGIN           = 4;      //账号登录
    int     UPDATE_START_STOP      = 5;     //开始关闭状态更新
    int     CLEAR_APP_DATA         = 6;     //清理app的数据
    int     SAVE_EXCHANGE_DATA     = 7;     //保存外汇数据


    //main
    //详情页
    int   FORECAST_DATA_LIST        = 130;      //首页预测数据刷新
    int   FORECAST_DATA_ADD_CODE    = 131;      //添加的新股票代码

    //记录页
    int   FORECAST_RECORD_ADD    = 150;      //添加的新购买项

    //stockinfo
    //详情页查看
    int     UPDATE_DATA              = 170;       //更新数据，插入到已有数据末尾

    //request code
    //main activity
    int  RequestMainPermission      = 1;    //请求权限
}
