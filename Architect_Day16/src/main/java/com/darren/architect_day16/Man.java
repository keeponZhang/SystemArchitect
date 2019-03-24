package com.darren.architect_day16;

/**
 * description: 静态代理设计模式 - 被代理对象
 * author: Darren on 2017/10/11 12:51
 * email: 240336124@qq.com
 * version: 1.0
 */
public class Man implements IBank{
    @Override
    public void applyBank() {
        System.out.println("办卡");
    }
}
