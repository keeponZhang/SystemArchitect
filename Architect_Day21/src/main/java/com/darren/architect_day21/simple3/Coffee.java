package com.darren.architect_day21.simple3;

/**
 * Created by hcDarren on 2017/11/4.
 */
// 咖啡 大杯，小杯 抽象 加料
public abstract class Coffee {
    protected CoffeeAdditives mAdditives;

    public Coffee(CoffeeAdditives additives){
        this.mAdditives = additives;
    }
    // 生成一杯咖啡
    public abstract void makeCoffee();

}
