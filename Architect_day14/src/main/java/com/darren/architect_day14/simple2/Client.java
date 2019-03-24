package com.darren.architect_day14.simple2;

/**
 * Created by hcDarren on 2017/10/8.
 */

public class Client {
    public static void main(String[] args){
        // 第一个版本只是显示人民币
        RMBAdaptee rmbAdaptee = new RMBAdaptee(560);
        float rmb = rmbAdaptee.getRmb();
        System.out.print("人民币："+rmb);

        // 第二个版本要去兼容美元 ，采用适配器模式，
        // 角色：需要适配的接口 Target (美元) ，适配器对象 （Adapter）
        Adapter adapter = new Adapter(560);
        rmb = adapter.getRmb();
        System.out.print("人民币："+rmb);
        float usd = adapter.getUsd();
        System.out.print("美元："+usd);
    }
}
