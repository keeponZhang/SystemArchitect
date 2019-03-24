package com.darren.architect_day21.simple3;

/**
 * Created by hcDarren on 2017/11/4.
 */

public class SmallCoffee extends Coffee{
    public SmallCoffee(CoffeeAdditives additives) {
        super(additives);
    }

    @Override
    public void makeCoffee() {
        System.out.println("小杯的"+mAdditives+"咖啡");
    }
}
