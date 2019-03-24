package com.darren.architect_day16.simple2;

import java.lang.reflect.Proxy;

/**
 * Created by hcDarren on 2017/10/15.
 */

public class Client {
    public static void main(String[] args){
        Man man = new Man("Darren");

        IBank bank =
                // 返回的是 IBank 的一个实例对象，这个对象是由 Java 给我们创建的 ,调用的是 jni
                (IBank) Proxy.newProxyInstance(
                IBank.class.getClassLoader(), // ClassLoader
                new Class<?>[]{IBank.class}, // 目标接口
                new BankInvocationHandler(man) // InvocationHandler (这个类是关键)
        );
        System.out.println(bank instanceof Proxy);
        System.out.println(bank.getClass().toString());
        // 当调用这个方法的时候会来到 BankInvocationHandler 的 invoke 方法
        bank.applyBank();

        bank.lostBank();

        bank.extraBank();
    }
}
