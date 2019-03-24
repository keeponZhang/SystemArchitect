package com.darren.architect_day14.simple1;

/**
 * Created by hcDarren on 2017/10/8.
 */

public class Client {
    public static void main(String[] args){
        // 第一个版本只是显示人民币
        RMBAdaptee rmbAdaptee = new RMBAdaptee(560);
        float rmb = rmbAdaptee.getRmb();
        System.out.print("人民币："+rmb);

        // 第二个版本要去兼容美元 ，这么写可以设计模式只是一种思想
        float usd = rmbAdaptee.getUsd();
        System.out.print("美元："+usd);
    }
}
