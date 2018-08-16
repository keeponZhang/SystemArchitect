package com.darren.architect_day08.simple3;

/**
 * 单例设计模式 - 静态内部类（比较常用）
 * Created by hcDarren on 2017/9/17.
 */

public class Singleton {
    private Singleton() {
    }

    public static Singleton getInstance(){
        return SingletonHolder.mInstance;
    }

    public static class SingletonHolder{
        // 加上 volatile 的用处是什么？
        private static volatile Singleton mInstance = new Singleton();
    }
}
