package com.darren.architect_day16;

import java.lang.reflect.Proxy;

/**
 * description:代理设计模式 - 测试
 * author: Darren on 2017/10/11 12:54
 * email: 240336124@qq.com
 * version: 1.0
 */
public class Client {
    public static void main(String[] args) {
        Man man = new Man();
        IBank bank = (IBank) Proxy.newProxyInstance(IBank.class.getClassLoader(),
                new Class<?>[]{IBank.class}, new DynamicBankProxy(man));
        bank.applyBank();
    }
}
