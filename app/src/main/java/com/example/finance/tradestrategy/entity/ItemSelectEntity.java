package com.example.finance.tradestrategy.entity;

/**
 * Created by Administrator on 2017/8/21.
 */

public class ItemSelectEntity {

    private boolean checked;

    //以下两个值是一一对应关系
    private String   value;
    private int     valueInt;


    public ItemSelectEntity(boolean checked, String value, int valueInt) {
        this.checked = checked;
        this.value = value;
        this.valueInt = valueInt;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getValue() {
        return value;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getValueInt() {
        return valueInt;
    }
}
