package com.darren.architect_day14.simple3;

/**
 * Created by hcDarren on 2017/10/8.
 * 对象适配
 */
public class Client {
    public static void main(String[] args){
        // 第一个版本只是显示人民币
        RMBAdaptee rmbAdaptee = new RMBAdaptee(560);
        Adapter adapter = new Adapter(rmbAdaptee);

        float usd = adapter.getUsd();
        System.out.print("美元："+usd);
    }
}
