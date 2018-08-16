package com.darren.architect_day13.simple2;

/**
 * Created by hcDarren on 2017/10/7.
 */

public class RenzhongFinance implements IFinance{
    @Override
    public float finance(int month, int money) {
        if (month == 3) {
            return money + money * 0.09f / 12 * month;
        }
        if (month == 6) {
            return money + money * 0.112f / 12 * month;
        }
        if (month == 12) {
            return money + money * 0.12f / 12 * month;
        }
        throw new IllegalArgumentException("月份不对");
    }
}
