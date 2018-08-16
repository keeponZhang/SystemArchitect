package com.darren.architect_day13.simple2;

/**
 * Created by hcDarren on 2017/10/7.
 */

public class ZhifubaoFinance implements IFinance{
    @Override
    public float finance(int month, int money) {
        if (month == 3) {
            return money + money * 0.047f / 12 * month;
        }
        if (month == 6) {
            return money + money * 0.05f / 12 * month;
        }
        if (month == 12) {
            return money + money * 0.06f / 12 * month;
        }
        throw new IllegalArgumentException("月份不对");
    }
}
