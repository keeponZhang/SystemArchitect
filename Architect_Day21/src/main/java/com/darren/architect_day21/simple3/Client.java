package com.darren.architect_day21.simple3;

/**
 * Created by hcDarren on 2017/11/4.
 */

public class Client {
    public static void main(String[] args){
        Sugar sugar = new Sugar();
        SmallCoffee smallCoffee = new SmallCoffee(sugar);
        smallCoffee.makeCoffee();
        // 一个对象依赖另一个对象 ，所依赖的是接口,之间没有紧密的联系
        Original original = new Original();
        LargeCoffee largeCoffee = new LargeCoffee(original);
        largeCoffee.makeCoffee();

        MiddleCoffee middleCoffee = new MiddleCoffee(original);
        middleCoffee.makeCoffee();
    }
}
